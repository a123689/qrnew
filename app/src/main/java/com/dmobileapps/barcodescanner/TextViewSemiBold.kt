package com.dmobileapps.barcodescanner

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

@SuppressLint("AppCompatCustomView","CustomViewStyleable")
class TextViewSemiBold : TextView {
    constructor(context: Context?) : super(context) {
        this.typeface = TypeFaceUtil.instant!!.semibold
        this.setTextColor(ColorUtil.instant!!.colorText)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        this.typeface = TypeFaceUtil.instant!!.semibold
        initAttr(attrs)
    }

    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        this.typeface = TypeFaceUtil.instant!!.semibold
        initAttr(attrs)
    }


    private fun initAttr(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.text_style,
            0,
            0
        )
        val textColorDefault = ta.getBoolean(R.styleable.text_style_textColorDefault, true)
        if (textColorDefault) {
            this.setTextColor(ColorUtil.instant!!.colorText)
        }

        ta.recycle()
    }
}