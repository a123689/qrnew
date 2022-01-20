package com.dmobileapps.barcodescanner.feature

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.dmobileapps.barcodescanner.AppConstant
import com.dmobileapps.barcodescanner.R
import com.dmobileapps.barcodescanner.setPreventDoubleClickScaleView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.InterstitialListener
import kotlinx.android.synthetic.main.dialog_rate.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_splash.*

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if (isExit) {
                        showAds()
                    } else {
                        isExit = true
                    }

                }
            }
        activity?.onBackPressedDispatcher!!.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    var currentUnifiedNativeAd: NativeAd? = null
    var isExit = false
    val handler by lazy {
        Handler(Looper.getMainLooper())
    }
    var check = false
    var dialogLoading: MaterialDialog? = null
    private var navController: NavController? = null
    private var mInterstitialAd: InterstitialAd? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        sharedPreference = activity?.getSharedPreferences("NOTE", Context.MODE_PRIVATE)!!
        ivSetting.setPreventDoubleClickScaleView {
            if (navController != null && navController?.currentDestination?.id == R.id.homeFragment) {
                navController?.navigate(R.id.action_homeFragment_to_settingsFragment)
            }
        }

        btnScanCode.setPreventDoubleClickScaleView {
            if (navController != null && navController?.currentDestination?.id == R.id.homeFragment) {
                navController?.navigate(R.id.action_homeFragment_to_scanBarcodeFromCameraFragment)
            }
        }

        btnCreateCode.setPreventDoubleClickScaleView {
            if (navController != null && navController?.currentDestination?.id == R.id.homeFragment) {
                navController?.navigate(R.id.action_homeFragment_to_createBarcodeFragment)
            }
        }

        btnHistory.setPreventDoubleClickScaleView {
            if (navController != null && navController?.currentDestination?.id == R.id.homeFragment) {
                navController?.navigate(R.id.action_homeFragment_to_barcodeHistoryFragment)
            }
        }

        val layoutAds =
            LayoutInflater.from(context).inflate(R.layout.layout_ads_home, null)

        activity?.let {
            load(it, loadSuccess = {
                show(it, layoutAd, layoutAds)
            })
        }
    }


//    override fun onPause() {
//        super.onPause()
//        IronSource.onPause(activity)
//    }

    private fun gotoExit() {
        if (navController != null && navController?.currentDestination?.id == R.id.homeFragment) {
            navController?.navigate(R.id.action_homeFragment_to_exitFragment)
        }
    }

    fun showDialogLoading() {
        if (dialogLoading == null) {
            dialogLoading = activity?.let {
                MaterialDialog(it).apply {
                    cancelable(false)
                    customView(R.layout.dialog_loading)
                }
            }
        }
        if (dialogLoading?.isShowing == false) {
            dialogLoading?.show {
                cornerRadius(16f)
            }
        }

    }

    fun hideDialogLoading() {

        dialogLoading?.dismiss()
        dialogLoading = null
    }

    private fun showAds() {
        showDialogLoading()
        handler.postDelayed(object : Runnable {
            override fun run() {
                hideDialogLoading()
                check = true
                 mInterstitialAd = null
                gotoExit()
            }

        }, 6000)
//        IronSource.init(activity, "1267d4f45", IronSource.AD_UNIT.INTERSTITIAL)
//        IronSource.loadInterstitial()
//        IronSource.setInterstitialListener(object : InterstitialListener {
//            override fun onInterstitialAdReady() {
//                if(!check){
//                    IronSource.showInterstitial("DefaultInterstitial")
//                }
//
//            }
//
//            override fun onInterstitialAdLoadFailed(p0: IronSourceError?) {
//                hideDialogLoading()
//                gotoExit()
//            }
//
//            override fun onInterstitialAdOpened() {
//
//            }
//
//            override fun onInterstitialAdClosed() {
//
//            }
//
//            override fun onInterstitialAdShowSucceeded() {
//                hideDialogLoading()
//                gotoExit()
//            }
//
//            override fun onInterstitialAdShowFailed(p0: IronSourceError?) {
//
//            }
//
//            override fun onInterstitialAdClicked() {
//
//            }
//
//        })
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            "ca-app-pub-2051755608375377/6728317075",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    hideDialogLoading()
                    mInterstitialAd = null
                    check = true
                    gotoExit()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    if (mInterstitialAd != null && !check) {
                        Log.d("dat123","zxx")
                        mInterstitialAd?.show(activity)
                    }
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {}
                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                hideDialogLoading()
                                gotoExit()
                            }
                            override fun onAdShowedFullScreenContent() {
                                AppConstant.isClickAds = true
                                hideDialogLoading()
                                gotoExit()
                                check = true
                                mInterstitialAd = null
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                AppConstant.isClickAds = true
                            }

                        }
                }

            })
    }

    private fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        val viewGroup = adView.findViewById<ViewGroup>(R.id.ad_media)
        if (viewGroup != null) {
            val mediaView = MediaView(adView.context)
            viewGroup.addView(
                mediaView,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            adView.mediaView = mediaView
        } else {
            val mediaView = MediaView(adView.context)
            adView.addView(
                mediaView,
                ViewGroup.LayoutParams(
                    0,
                    0
                )
            )
            adView.mediaView = mediaView
        }

        val viewGroupIcon = adView.findViewById<View>(R.id.ad_app_icon)
        if (viewGroupIcon != null) {
            if (viewGroupIcon is ViewGroup) {
                val nativeAdIcon = ImageView(adView.context)
                viewGroupIcon.addView(
                    nativeAdIcon,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                adView.iconView = nativeAdIcon
            } else {
                adView.iconView = viewGroupIcon
            }

        }

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        if (adView.mediaView != null) {
            adView.mediaView.setMediaContent(nativeAd.mediaContent)
        }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (adView.callToActionView != null) {
            if (adView.callToActionView != null) {
                if (nativeAd.callToAction == null) {
                    adView.callToActionView.visibility = View.INVISIBLE
                } else {
                    adView.callToActionView.visibility = View.VISIBLE
                    if (adView.callToActionView is Button) {
                        (adView.callToActionView as Button).text = nativeAd.callToAction
                    } else {
                        (adView.callToActionView as TextView).text = nativeAd.callToAction
                    }
                }
            }
        }
        if (adView.iconView != null) {
            if (nativeAd.icon == null) {
                adView.iconView.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable
                )
                adView.iconView.visibility = View.VISIBLE
            }
        }
        if (adView.priceView != null) {
            if (nativeAd.price == null) {
                adView.priceView.visibility = View.INVISIBLE
            } else {
                adView.priceView.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }
        }
        if (adView.storeView != null) {
            if (nativeAd.store == null) {
                adView.storeView.visibility = View.INVISIBLE
            } else {
                adView.storeView.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }
        }
        if (adView.starRatingView != null) {
            if (nativeAd.starRating == null) {
                adView.starRatingView.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView.visibility = View.VISIBLE
            }
        }
        if (adView.advertiserView != null) {
            if (nativeAd.advertiser == null) {
                adView.advertiserView.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView.visibility = View.VISIBLE
            }
        }

        adView.setNativeAd(nativeAd)
//        val vc = nativeAd.videoController
//
//        if (vc.hasVideoContent()) {
//            vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {
//                override fun onVideoEnd() {
//                    super.onVideoEnd()
//                }
//            }
//        } else {
//        }

    }

    private fun load(activity: Activity, loadSuccess: () -> Unit) {
        val idAds = "ca-app-pub-2051755608375377/4344075742"
        val builder = AdLoader.Builder(activity.applicationContext, idAds)
        builder.forNativeAd { unifiedNativeAd ->
            if (currentUnifiedNativeAd != null) {
                currentUnifiedNativeAd?.destroy()
            }

            currentUnifiedNativeAd?.destroy()
            currentUnifiedNativeAd = unifiedNativeAd
            loadSuccess()
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()
        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // ivBanner.visibility = View.VISIBLE
                // layoutAd.visibility = View.INVISIBLE
            }

            override fun onAdClicked() {
                super.onAdClicked()
                AppConstant.isClickAds = true
            }

        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onDestroy() {
        currentUnifiedNativeAd?.destroy()
        super.onDestroy()
    }

    fun show(
        activity: Activity,
        layout: ViewGroup?,
        layoutAds: View?,
    ) {

        if (layout != null) {
            if (layoutAds != null) {
                val unifiedNativeAdView = NativeAdView(activity)
                unifiedNativeAdView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                layoutAds.parent?.let {
                    (it as ViewGroup).removeView(layoutAds)
                }
                unifiedNativeAdView.addView(layoutAds)
                currentUnifiedNativeAd?.let {
                    populateUnifiedNativeAdView(it, unifiedNativeAdView)
                    layout.removeAllViews()
                    layout.addView(unifiedNativeAdView)

                }
            } else {
                val adView = LayoutInflater.from(activity)
                    .inflate(R.layout.ad_unified, null) as NativeAdView
                currentUnifiedNativeAd?.let {
                    populateUnifiedNativeAdView(it, adView)
                    layout.removeAllViews()
                    layout.addView(adView)
                }

            }

        } else {
            // Utils.showToastDebug(activity, "layout ad native not null")
        }

    }

    override fun onStop() {
        super.onStop()
        isRate = true
    }

    private lateinit var sharedPreference: SharedPreferences
    private var isRate = false
    override fun onResume() {
        super.onResume()
        IronSource.onResume(activity)
        if (isRate) {
            if (!sharedPreference.getBoolean("rate", false)) {
                context?.let {
                    val dialog: Dialog
                    val view: View = LayoutInflater.from(it).inflate(R.layout.dialog_rate, null)
                    val builder = AlertDialog.Builder(it)
                        .setView(view)
                        .setCancelable(true)
                    dialog = builder.create()
                    dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    if (!dialog.isShowing) {
                        dialog.show()
                    }

                    view.rbRate.setOnRatingBarChangeListener { ratingBar, fl, b ->
                        var editor = sharedPreference.edit()
                        editor.putBoolean("rate", true)
                        editor.apply()
                        if (ratingBar.rating < 5) {
                            sendEmailMoree(
                                requireContext(),
                                arrayOf("khoanglang270102@gmail.com"),
                                "Feedback to Camera",
                                ""
                            )
                            dialog.dismiss()
                        } else {
                            dialog.dismiss()
                            activity?.let {
                                openMarket(requireContext(), it.packageName)
                            }

                        }
                    }
                }
            }

        }

    }

    private fun openMarket(context: Context, packageName: String) {
        val i = Intent(Intent.ACTION_VIEW)
        try {
            i.data = Uri.parse("market://details?id=$packageName")
            context.startActivity(i)
        } catch (ex: ActivityNotFoundException) {
            openBrowser(
                context,
                "https://play.google.com/store/apps/details?id=\"" + packageName
            )
        }
    }

    private fun openBrowser(context: Context, url: String) {
        var url = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(browserIntent)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    private fun sendEmailMoree(
        context: Context,
        mail: Array<String>,
        subject: String,
        body: String
    ) {
        disableExposure()
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, mail)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "you need install gmail", Toast.LENGTH_SHORT).show()
        }
    }

    private fun disableExposure() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}