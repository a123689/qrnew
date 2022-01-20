package com.dmobileapps.barcodescanner.feature.tabs

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.dmobileapps.barcodescanner.AppOpenManager
import com.dmobileapps.barcodescanner.BuildConfig
import com.dmobileapps.barcodescanner.R
import com.dmobileapps.barcodescanner.extension.applySystemWindowInsets
import com.dmobileapps.barcodescanner.feature.BaseActivity
import com.dmobileapps.barcodescanner.feature.tabs.create.CreateBarcodeFragment
import com.dmobileapps.barcodescanner.feature.tabs.history.BarcodeHistoryFragment
import com.dmobileapps.barcodescanner.feature.tabs.scan.ScanBarcodeFromCameraFragment
import com.dmobileapps.barcodescanner.feature.tabs.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_bottom_tabs.*

class BottomTabsActivity : BaseActivity() {

    companion object {
        private const val ACTION_CREATE_BARCODE = "${BuildConfig.APPLICATION_ID}.CREATE_BARCODE"
        private const val ACTION_HISTORY = "${BuildConfig.APPLICATION_ID}.HISTORY"
    }

    private var appOpenManager: AppOpenManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_tabs)

        // AdsController.Companion.init(this, true, idAdmob, PACKET_NAME, jsonFile, getLifecycle());
//        MobileAds.initialize(
//                this,
//                new OnInitializationCompleteListener() {
//                    @Override
//                    public void onInitializationComplete(InitializationStatus initializationStatus) {
//                        Log.d("LOG_TAG", initializationStatus.toString());
//                    }
//                });

//        appOpenManager =
//            AppOpenManager(application, object : AppOpenManager.CallBackShowViewLoadingData {
//                override fun onShowView() {
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        tvLoading.visibility = View.VISIBLE
//                    }, 200)
//                }
//
//                override fun onDimissView() {
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        tvLoading.visibility = View.GONE
//                    }, 2000)
//                }
//            })

    }

}