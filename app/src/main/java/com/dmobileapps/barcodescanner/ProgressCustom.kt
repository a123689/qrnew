package com.dmobileapps.barcodescanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class ProgressCustom @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF(0f, 0f, 0f, 0f)
    private val rectProgress = RectF(0f, 0f, 0f, 0f)
    private val rectText = Rect()
    private var progress = 0
    private var max = 0
    private var position = 0f
    private var checkWidth = false
    private var i = 0f

    init {
        linePaint.color = ContextCompat.getColor(context, R.color.color_background_progress)
        linePaint.strokeWidth = 2f
        linePaint.style = Paint.Style.FILL_AND_STROKE
        linePaint.strokeJoin = Paint.Join.ROUND

        progressPaint.color = ContextCompat.getColor(context, R.color.color_progress)
        progressPaint.strokeWidth = 2f
        progressPaint.style = Paint.Style.FILL_AND_STROKE
        progressPaint.strokeJoin = Paint.Join.ROUND



    }

    fun setMax(max: Int) {
        this.max = max
    }

    fun setProgress(progress: Int) {
        this.progress = progress

        if (width > 0) {
            position = ((progress * width) / max).toFloat()
        }
        invalidate()
    }

    private fun dpFromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        rect.top = 5f
        rect.bottom = 12f
        rect.left = 2f
        rect.right = width.toFloat() - 2f
        rectProgress.top = 5f
        rectProgress.bottom = 12f
        rectProgress.left = 2f
        rectProgress.right = position - 2f


        canvas?.drawRoundRect(rect, dpFromPx(context, 100f), dpFromPx(context, 100f), linePaint)
        canvas?.drawRoundRect(
            rectProgress,
            dpFromPx(context, 100f),
            dpFromPx(context, 100f),
            progressPaint
        )



    }

}
