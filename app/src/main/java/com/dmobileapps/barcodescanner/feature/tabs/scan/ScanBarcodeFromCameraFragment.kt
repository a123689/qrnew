package com.dmobileapps.barcodescanner.feature.tabs.scan

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.budiyev.android.codescanner.*
import com.dmobileapps.barcodescanner.AppConstant
import com.dmobileapps.barcodescanner.R
import com.dmobileapps.barcodescanner.di.*
import com.dmobileapps.barcodescanner.extension.*
import com.dmobileapps.barcodescanner.feature.barcode.BarcodeActivity
import com.dmobileapps.barcodescanner.feature.common.dialog.ConfirmBarcodeDialogFragment
import com.dmobileapps.barcodescanner.feature.tabs.scan.file.ScanBarcodeFromFileActivity
import com.dmobileapps.barcodescanner.model.Barcode
import com.dmobileapps.barcodescanner.usecase.SupportedBarcodeFormats
import com.dmobileapps.barcodescanner.usecase.save
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_scan_barcode_from_camera.*
import java.util.concurrent.TimeUnit

class ScanBarcodeFromCameraFragment : Fragment(), ConfirmBarcodeDialogFragment.Listener {

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val PERMISSION_REQUEST_CODE = 101
        private const val ZXING_SCAN_INTENT_ACTION = "com.google.zxing.client.android.SCAN"
        private const val CONTINUOUS_SCANNING_PREVIEW_DELAY = 500L
    }

    private val vibrationPattern = arrayOf<Long>(0, 350).toLongArray()
    private val disposable = CompositeDisposable()
    private var maxZoom: Int = 0
    private val zoomStep = 5
    private lateinit var codeScanner: CodeScanner
    private var toast: Toast? = null
    private var lastResult: Barcode? = null
    var mInterstitialAd: InterstitialAd? = null
    var handler = Handler(Looper.getMainLooper())
    var time = 1000
    var dialogLoading: MaterialDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan_barcode_from_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportEdgeToEdge()
        setDarkStatusBar()
        initScanner()
        initFlashButton()
        handleScanFromFileClicked()
        handleZoomChanged()
        handleDecreaseZoomClicked()
        handleIncreaseZoomClicked()
        requestPermissions()

    }

    override fun onResume() {
        super.onResume()
        if (areAllPermissionsGranted()) {
            initZoomSeekBar()
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE && areAllPermissionsGranted(grantResults)) {
            initZoomSeekBar()
            codeScanner.startPreview()
        }
    }

    override fun onBarcodeConfirmed(barcode: Barcode) {
        handleConfirmedBarcode(barcode)
    }

    override fun onBarcodeDeclined() {
        restartPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setLightStatusBar()
        disposable.clear()
    }

    private fun supportEdgeToEdge() {
        image_view_flash.applySystemWindowInsets(applyTop = true)
        image_view_scan_from_file.applySystemWindowInsets(applyTop = true)
    }

    private fun setDarkStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        if (settings.isDarkTheme) {
            return
        }

        requireActivity().window.decorView.apply {
            systemUiVisibility = systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setLightStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        if (settings.isDarkTheme) {
            return
        }

        requireActivity().window.decorView.apply {
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun initScanner() {
        codeScanner = CodeScanner(requireActivity(), scanner_view).apply {
            camera = if (settings.isBackCamera) {
                CodeScanner.CAMERA_BACK
            } else {
                CodeScanner.CAMERA_FRONT
            }
            autoFocusMode = if (settings.simpleAutoFocus) {
                AutoFocusMode.SAFE
            } else {
                AutoFocusMode.CONTINUOUS
            }
            formats = SupportedBarcodeFormats.FORMATS.filter(settings::isFormatSelected)
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = settings.flash
            isTouchFocusEnabled = false
            decodeCallback = DecodeCallback(::handleScannedBarcode)
            errorCallback = ErrorCallback(::showError)
        }
    }

    private fun initZoomSeekBar() {
        scannerCameraHelper.getCameraParameters(settings.isBackCamera)?.apply {
            this@ScanBarcodeFromCameraFragment.maxZoom = maxZoom
            seek_bar_zoom.max = maxZoom
            seek_bar_zoom.progress = zoom
        }
    }

    private fun initFlashButton() {
        layout_flash_container.setOnClickListener {
            toggleFlash()
        }
        image_view_flash.isActivated = settings.flash
    }

    private fun handleScanFromFileClicked() {
        layout_scan_from_file_container.setOnClickListener {
            navigateToScanFromFileScreen()
        }
    }

    private fun handleZoomChanged() {
        seek_bar_zoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    codeScanner.zoom = progress
                }
            }
        })
    }

    private fun handleDecreaseZoomClicked() {
        button_decrease_zoom.setOnClickListener {
            decreaseZoom()
        }
    }

    private fun handleIncreaseZoomClicked() {
        button_increase_zoom.setOnClickListener {
            increaseZoom()
        }
    }

    private fun decreaseZoom() {
        codeScanner.apply {
            if (zoom > zoomStep) {
                zoom -= zoomStep
            } else {
                zoom = 0
            }
            seek_bar_zoom.progress = zoom
        }
    }

    private fun increaseZoom() {
        try {
            codeScanner.apply {
                if (zoom < maxZoom - zoomStep) {
                    zoom += zoomStep
                } else {
                    zoom = maxZoom
                }
                seek_bar_zoom.progress = zoom
            }
        }catch (e: Exception){}

    }

    private fun handleScannedBarcode(result: Result) {
        if (requireActivity().intent?.action == ZXING_SCAN_INTENT_ACTION) {
            vibrateIfNeeded()
            finishWithResult(result)
            return
        }

        if (settings.continuousScanning && result.equalTo(lastResult)) {
            restartPreviewWithDelay(false)
            return
        }

        vibrateIfNeeded()

        val barcode = barcodeParser.parseResult(result)

        when {
            settings.confirmScansManually -> showScanConfirmationDialog(barcode)
            settings.saveScannedBarcodesToHistory || settings.continuousScanning -> saveScannedBarcode(
                barcode
            )
            else -> navigateToBarcodeScreen(barcode)
        }
    }

    private fun handleConfirmedBarcode(barcode: Barcode) {
        when {
            settings.saveScannedBarcodesToHistory || settings.continuousScanning -> saveScannedBarcode(
                barcode
            )
            else -> navigateToBarcodeScreen(barcode)
        }
    }

    private fun vibrateIfNeeded() {
        if (settings.vibrate) {
            requireActivity().apply {
                runOnUiThread {
                    applicationContext.vibrator?.vibrateOnce(vibrationPattern)
                }
            }
        }
    }

    private fun showScanConfirmationDialog(barcode: Barcode) {
        val dialog = ConfirmBarcodeDialogFragment.newInstance(barcode)
        dialog.show(childFragmentManager, "")
    }

    private fun saveScannedBarcode(barcode: Barcode) {
        barcodeDatabase.save(barcode, settings.doNotSaveDuplicates)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { id ->
                    lastResult = barcode
                    when (settings.continuousScanning) {
                        true -> restartPreviewWithDelay(true)
                        else -> navigateToBarcodeScreen(barcode.copy(id = id))
                    }
                },
                ::showError
            )
            .addTo(disposable)
    }

    private fun restartPreviewWithDelay(showMessage: Boolean) {
        Completable
            .timer(CONTINUOUS_SCANNING_PREVIEW_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (showMessage) {
                    showToast(R.string.fragment_scan_barcode_from_camera_barcode_saved)
                }
                restartPreview()
            }
            .addTo(disposable)
    }

    private fun restartPreview() {
        requireActivity().runOnUiThread {
            codeScanner.startPreview()
        }
    }

    private fun toggleFlash() {
        image_view_flash.isActivated = image_view_flash.isActivated.not()
        codeScanner.isFlashEnabled = codeScanner.isFlashEnabled.not()
    }

    private fun showToast(stringId: Int) {
        toast?.cancel()
        toast = Toast.makeText(requireActivity(), stringId, Toast.LENGTH_SHORT).apply {
            show()
        }
    }

    private fun requestPermissions() {
        permissionsHelper.requestNotGrantedPermissions(
            requireActivity() as AppCompatActivity,
            PERMISSIONS,
            PERMISSION_REQUEST_CODE
        )
    }

    private fun areAllPermissionsGranted(): Boolean {
        return permissionsHelper.areAllPermissionsGranted(requireActivity(), PERMISSIONS)
    }

    private fun areAllPermissionsGranted(grantResults: IntArray): Boolean {
        return permissionsHelper.areAllPermissionsGranted(grantResults)
    }

    private fun navigateToScanFromFileScreen() {
        ScanBarcodeFromFileActivity.start(requireActivity())
    }

    fun showDialogLoading() {
        if (dialogLoading == null) {
            context?.let {
                dialogLoading = MaterialDialog(it).apply {
                    cancelable(false)
                    customView(R.layout.dialog_loading)
                }
            }

        }
        dialogLoading?.show {
            cornerRadius(16f)
        }
    }

    fun hideDialogLoading() {
        dialogLoading?.dismiss()
        dialogLoading = null
    }


    private fun navigateToBarcodeScreen(barcode: Barcode) {

       // BarcodeActivity.start(requireActivity(), barcode)
        var check = false
        val dialogProcess = ProgressDialog(activity, R.style.DialogMyTheme)
        dialogProcess.setMessage("Loading....")
        dialogProcess.setCancelable(false)
        dialogProcess.setCanceledOnTouchOutside(false)
        handler.postDelayed(object : Runnable {
            override fun run() {
                check = true
                mInterstitialAd = null
                hideDialogLoading()
                handler.removeCallbacksAndMessages(null)
                BarcodeActivity.start(requireActivity(), barcode)
            }

        }, 9000)
        showDialogLoading()
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            "ca-app-pub-2051755608375377/9170124498",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    time = 0
                    hideDialogLoading()
                    mInterstitialAd = null
                    handler.removeCallbacksAndMessages(null)
                    BarcodeActivity.start(requireActivity(), barcode)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {

                    mInterstitialAd = interstitialAd
                    if (mInterstitialAd != null && !check) {
                        mInterstitialAd?.show(activity)
                    }

                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                //Log.d("dat123","xcxzczc")
                                 handler.removeCallbacksAndMessages(null)
                                BarcodeActivity.start(requireActivity(), barcode)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (dialogProcess != null) {
                                        try {
                                            dialogProcess.dismiss()
                                        }
                                        catch (e: Exception) {

                                        }

                                    }

                                }, 1000)

                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                time = 0
                                handler.removeCallbacksAndMessages(null)
                                hideDialogLoading()
                                BarcodeActivity.start(requireActivity(), barcode)
                            }

                            override fun onAdShowedFullScreenContent() {
                                AppConstant.isClickAds = true
                                time = 0
                                mInterstitialAd = null
                                hideDialogLoading()
                                handler.removeCallbacksAndMessages(null)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (dialogProcess != null) {
                                        try {
                                            dialogProcess.show()
                                        }catch (e: Exception){

                                        }

                                    }
                                }, 300)

                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                AppConstant.isClickAds = true
                            }

                        }
                }

            })

    }

    private fun finishWithResult(result: Result) {
        val intent = Intent()
            .putExtra("SCAN_RESULT", result.text)
            .putExtra("SCAN_RESULT_FORMAT", result.barcodeFormat.toString())

        if (result.rawBytes?.isNotEmpty() == true) {
            intent.putExtra("SCAN_RESULT_BYTES", result.rawBytes)
        }

        result.resultMetadata?.let { metadata ->
            metadata[ResultMetadataType.UPC_EAN_EXTENSION]?.let {
                intent.putExtra("SCAN_RESULT_ORIENTATION", it.toString())
            }

            metadata[ResultMetadataType.ERROR_CORRECTION_LEVEL]?.let {
                intent.putExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL", it.toString())
            }

            metadata[ResultMetadataType.UPC_EAN_EXTENSION]?.let {
                intent.putExtra("SCAN_RESULT_UPC_EAN_EXTENSION", it.toString())
            }

            metadata[ResultMetadataType.BYTE_SEGMENTS]?.let {
                var i = 0
                @Suppress("UNCHECKED_CAST")
                for (seg in it as Iterable<ByteArray>) {
                    intent.putExtra("SCAN_RESULT_BYTE_SEGMENTS_$i", seg)
                    ++i
                }
            }
        }

        requireActivity().apply {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}