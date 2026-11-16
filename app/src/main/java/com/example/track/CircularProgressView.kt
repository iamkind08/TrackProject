package com.example.track

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var animatedProgress: Float = 0f
    private var targetProgress: Float = 0f
    private var animator: ValueAnimator? = null

    private var progressColor: Int = Color.parseColor("#FBC02D")
    private var trackColor: Int = Color.parseColor("#33FFFFFF")
    private var strokeWidthPx: Float = 24f
    private var textColor: Int = Color.WHITE
    private var textSizePx: Float = 64f

    private val rectF = RectF()
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    init {
        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.CircularProgressView)
            progressColor = ta.getColor(R.styleable.CircularProgressView_cpvProgressColor, progressColor)
            trackColor    = ta.getColor(R.styleable.CircularProgressView_cpvTrackColor, trackColor)
            strokeWidthPx = ta.getDimension(R.styleable.CircularProgressView_cpvStrokeWidth, strokeWidthPx)
            textColor     = ta.getColor(R.styleable.CircularProgressView_cpvTextColor, textColor)
            textSizePx    = ta.getDimension(R.styleable.CircularProgressView_cpvTextSize, textSizePx)
            ta.recycle()
        }
        arcPaint.strokeWidth = strokeWidthPx
        textPaint.color = textColor
        textPaint.textSize = textSizePx
    }

    fun setProgress(value: Float, animate: Boolean = true) {
        val clamped = value.coerceIn(0f, 100f)
        if (clamped == targetProgress) return
        targetProgress = clamped

        if (!animate) {
            animatedProgress = clamped
            invalidate()
            return
        }

        animator?.cancel()
        animator = ValueAnimator.ofFloat(animatedProgress, clamped).apply {
            duration = 700
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                animatedProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val inset = strokeWidthPx / 2f + 4f
        rectF.set(inset, inset, width - inset, height - inset)

        arcPaint.color = trackColor
        canvas.drawArc(rectF, 0f, 360f, false, arcPaint)

        arcPaint.color = progressColor
        val sweep = animatedProgress / 100f * 360f
        if (sweep > 0f) {
            canvas.drawArc(rectF, -90f, sweep, false, arcPaint)
        }

        val text = "${animatedProgress.toInt()}%"
        val centerY = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(text, width / 2f, centerY, textPaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}