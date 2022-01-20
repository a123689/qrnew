package com.dmobileapps.barcodescanner;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;


public class AppOpenManager implements LifecycleObserver,Application.ActivityLifecycleCallbacks{
    private static final String LOG_TAG = "LOG_TAG";
    private static final String AD_UNIT_ID = "ca-app-pub-2051755608375377/5344985624";
    private AppOpenAd appOpenAd = null;
    private static boolean isShowingAd = false;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private long loadTime = 0;
    private final Application myApplication;
    private Activity currentActivity;
    /** Constructor */
    CallBackShowViewLoadingData callBackShowViewLoadingData;
    public AppOpenManager(Application myApplication, CallBackShowViewLoadingData callBackShowViewLoadingData) {
        this.callBackShowViewLoadingData = callBackShowViewLoadingData;
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        Log.d(LOG_TAG, "onStart1");
    }

    /** Creates and returns ad request. */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /** Utility method that checks if ad exists and can be shown. */
    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        showAdIfAvailable();
        Log.d(LOG_TAG, "onStartOK");
    }
    public void fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            Log.d(LOG_TAG,"123");
            return;
        }

        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        AppOpenManager.this.appOpenAd = ad;

                        AppOpenManager.this.loadTime = (new Date()).getTime();
                    }
                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Handle the error.
                    }
                };
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            Log.d(LOG_TAG, "Will show ad.");
            if(!AppConstant.INSTANCE.isClickAds()){
                FullScreenContentCallback fullScreenContentCallback =
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                callBackShowViewLoadingData.onDimissView();
                                // Set the reference to null so isAdAvailable() returns false.
                                AppOpenManager.this.appOpenAd = null;
                                isShowingAd = false;
                                fetchAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {}

                            @Override
                            public void onAdShowedFullScreenContent() {
                                callBackShowViewLoadingData.onShowView();
                                isShowingAd = true;
                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                AppConstant.INSTANCE.setClickAds(true);
                            }
                        };

                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                appOpenAd.show(currentActivity);
            }else {
                AppConstant.INSTANCE.setClickAds(false);
            }

        } else {
            Log.d(LOG_TAG, "Can not show ad"+isAdAvailable());
            fetchAd();
        }
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }

    public interface CallBackShowViewLoadingData{
        void onShowView();
        void onDimissView();
    }
}
