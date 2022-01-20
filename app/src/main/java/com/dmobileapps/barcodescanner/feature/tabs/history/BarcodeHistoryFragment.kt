package com.dmobileapps.barcodescanner.feature.tabs.history

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.dmobileapps.barcodescanner.AppConstant
import com.dmobileapps.barcodescanner.R
import com.dmobileapps.barcodescanner.di.barcodeDatabase
import com.dmobileapps.barcodescanner.extension.applySystemWindowInsets
import com.dmobileapps.barcodescanner.extension.showError
import com.dmobileapps.barcodescanner.feature.barcode.BarcodeActivity
import com.dmobileapps.barcodescanner.feature.common.dialog.DeleteConfirmationDialogFragment
import com.dmobileapps.barcodescanner.feature.tabs.history.export.ExportHistoryActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_barcode_history.*


class BarcodeHistoryFragment : Fragment(), DeleteConfirmationDialogFragment.Listener {
    private val disposable = CompositeDisposable()
    var mInterstitialAd: InterstitialAd? = null
    var handler = Handler(Looper.getMainLooper())
    var time = 1000
    var dialogLoading: MaterialDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                    //showAds()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    fun showAds(){
        var check = false
        handler.postDelayed(object : Runnable {
            override fun run() {
                check = true
                mInterstitialAd = null
                hideDialogLoading()
                handler.removeCallbacksAndMessages(null)
                findNavController().popBackStack()
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
                    findNavController().popBackStack()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {

                    mInterstitialAd = interstitialAd
                    if (mInterstitialAd != null && !check) {
                        mInterstitialAd?.show(activity)
                    }

                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {

                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                time = 0
                                handler.removeCallbacksAndMessages(null)
                                hideDialogLoading()
                                findNavController().popBackStack()
                            }

                            override fun onAdShowedFullScreenContent() {
                                AppConstant.isClickAds = true
                                time = 0
                                handler.removeCallbacksAndMessages(null)
                                hideDialogLoading()
                                mInterstitialAd = null
                                findNavController().popBackStack()
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                AppConstant.isClickAds = true
                            }
                        }
                }

            })
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_barcode_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportEdgeToEdge()
        initTabs()
        handleMenuClicked()
    }

    override fun onDeleteConfirmed() {
        clearHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    private fun supportEdgeToEdge() {
        app_bar_layout.applySystemWindowInsets(applyTop = true)
    }

    private fun initTabs() {
        view_pager.adapter = BarcodeHistoryViewPagerAdapter(requireContext(), childFragmentManager)
        tab_layout.setupWithViewPager(view_pager)
    }

    private fun handleMenuClicked() {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_export_history -> navigateToExportHistoryScreen()
                R.id.item_clear_history -> showDeleteHistoryConfirmationDialog()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun navigateToExportHistoryScreen() {
        ExportHistoryActivity.start(requireActivity())
    }

    private fun showDeleteHistoryConfirmationDialog() {
        val dialog = DeleteConfirmationDialogFragment.newInstance(R.string.dialog_delete_clear_history_message)
        dialog.show(childFragmentManager, "")
    }

    private fun clearHistory() {
        barcodeDatabase.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { },
                ::showError
            )
            .addTo(disposable)
    }
}