package com.dmobileapps.barcodescanner.feature

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.dmobileapps.barcodescanner.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.fragment_splash.*
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.dmobileapps.barcodescanner.AppConstant
import com.dmobileapps.barcodescanner.AppConstant.isClickAds
import com.dmobileapps.barcodescanner.AppOpenManager
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.model.Placement
import com.ironsource.mediationsdk.sdk.RewardedVideoListener
import java.util.*


class SplashFragment : Fragment() {

    private var mInterstitialAd: InterstitialAd? = null
    var count = 1
    val handler by lazy {
        Handler(Looper.getMainLooper())
    }
    var count2 = 0
    var check = false
    val handler2 by lazy {
        Handler(Looper.getMainLooper())
    }
    private var navController: NavController? = null
    private var loadCallback: AppOpenAdLoadCallback? = null
    private var appOpenAd: AppOpenAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        progressCustoom.setMax(100)
        timeOut()
        showAds()
        isClickAds = true
    }

    private fun timeOut(){
        handler.postDelayed(object : Runnable {
            override fun run() {
                count += 2
                progressCustoom?.setProgress(count)
                handler.postDelayed(this, 50)
            }

        }, 0)

        handler2.postDelayed(object : Runnable {
            override fun run() {
                check = true
                //mInterstitialAd = null
                appOpenAd = null
                progressCustoom?.setProgress(100)
                navHome()
            }

        }, 15000)
    }

    var idReal = "ca-app-pub-2051755608375377/9784094946"
    private fun showAds(){
//        var adRequest = AdRequest.Builder().build()
//        InterstitialAd.load(
//            activity,
//            idReal,
//            adRequest,
//            object : InterstitialAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    mInterstitialAd = null
//                    check = true
//                    navHome()
//                }
//
//                override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                    mInterstitialAd = interstitialAd
//                    if (mInterstitialAd != null && !check) {
//                        Log.d("dat123","zxx")
//                        mInterstitialAd?.show(activity)
//                    }
//                    mInterstitialAd?.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdDismissedFullScreenContent() {
//                                navHome2()
//                            }
//                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                                navHome()
//                            }
//                            override fun onAdShowedFullScreenContent() {
//                                isClickAds = true
//                                navHome()
//                                check = true
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdClicked() {
//                                super.onAdClicked()
//                                isClickAds = true
//                            }
//
//                        }
//                }
//
//            })
//        IronSource.init(activity, "1267d4f45", IronSource.AD_UNIT.REWARDED_VIDEO)
//        IronSource.setRewardedVideoListener(object : RewardedVideoListener {
//            override fun onRewardedVideoAdOpened() {
//                if (progressCustoom != null) {
//                    progressCustoom.setProgress(100)
//                }
//                navHome()
//            }
//
//            override fun onRewardedVideoAdClosed() {
//                if (progressCustoom != null) {
//                    progressCustoom.setProgress(100)
//                }
//                navHome2()
//            }
//
//            override fun onRewardedVideoAvailabilityChanged(p0: Boolean) {
//                if(p0){
//                    IronSource.showRewardedVideo("DefaultRewardedVideo")
//                }
//
//                Log.d("dat123","xx")
//            }
//
//            override fun onRewardedVideoAdStarted() {
//                Log.d("dat123","vv")
//            }
//
//            override fun onRewardedVideoAdEnded() {
//                if (progressCustoom != null) {
//                    progressCustoom.setProgress(100)
//                }
//                navHome()
//            }
//
//            override fun onRewardedVideoAdRewarded(p0: Placement?) {
//                if (progressCustoom != null) {
//                    progressCustoom.setProgress(100)
//                }
//                navHome()
//            }
//
//            override fun onRewardedVideoAdShowFailed(p0: IronSourceError?) {
//                if (progressCustoom != null) {
//                    progressCustoom.setProgress(100)
//                }
//                navHome()
//            }
//
//            override fun onRewardedVideoAdClicked(p0: Placement?) {
//
//            }
//
//        })

        loadCallback = object : AppOpenAdLoadCallback() {

            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad

                val fullScreenContentCallback: FullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null
                            if (progressCustoom != null) {
                                progressCustoom.setProgress(100)
                            }
                            navHome2()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            if (progressCustoom != null) {
                                progressCustoom.setProgress(100)
                            }
                            navHome()
                        }
                        override fun onAdShowedFullScreenContent() {
                            if (progressCustoom != null) {
                                progressCustoom.setProgress(100)
                            }
                            navHome()
                        }
                    }

                appOpenAd?.setFullScreenContentCallback(fullScreenContentCallback)
                if(appOpenAd != null && !check){
                    appOpenAd?.show(activity)
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                if (progressCustoom != null) {
                    progressCustoom.setProgress(100)
                }
                navHome()
            }
        }
        val request: AdRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            activity, "ca-app-pub-2051755608375377/3689469624", request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }

    private fun navHome() {
        if (progressCustoom != null) {
            progressCustoom.setProgress(100)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            handler.removeCallbacksAndMessages(null)
            handler2.removeCallbacksAndMessages(null)
            if(navController?.currentDestination?.id == R.id.splashFragment){
                navController?.navigate(R.id.action_splashFragment_to_homeFragment)
            }
        },300)
    }

    override fun onResume() {
        super.onResume()
        IronSource.onResume(activity)
    }

    override fun onPause() {
        super.onPause()
        IronSource.onPause(activity)
    }

    private fun navHome2() {
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if(event == Lifecycle.Event.ON_RESUME){
                    if (navController?.currentDestination?.id == R.id.splashFragment) {
                        navController?.navigate(R.id.action_splashFragment_to_homeFragment)
                        handler.removeCallbacksAndMessages(null)
                        handler2.removeCallbacksAndMessages(null)
                    }
                    lifecycle.removeObserver(this)
                }
            }

        })

    }
}