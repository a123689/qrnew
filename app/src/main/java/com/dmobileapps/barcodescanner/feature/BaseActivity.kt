package com.dmobileapps.barcodescanner.feature

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dmobileapps.barcodescanner.AppOpenManager
import com.dmobileapps.barcodescanner.di.rotationHelper
import kotlinx.android.synthetic.main.activity_bottom_tabs.*

abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        rotationHelper.lockCurrentOrientationIfNeeded(this)



    }
}