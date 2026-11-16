package com.example.track

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class FireworksView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        val color: Int,
        val radius: Float,
        var life: Float = 1f
    )

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val colors = intArrayOf(
        Color.parseColor("#FBC02D"),
        Color.parseColor("#EC407A"),
        Color.parseColor("#4CAF50"),
        Color.parseColor("#42A5F5"),
        Color.parseColor("#AB47BC"),
        Color.WHITE
    )

    private var animator: ValueAnimator? = null

    fun launch(burstCount: Int = 3) {
        val cx = width / 2f
        val cy = height / 3f

        repeat(burstCount) { i ->
            val offsetX = if (burstCount > 1) (i - burstCount / 2f) * width / 4f else 0f
            spawnBurst(cx + offsetX, cy + Random.nextFloat() * height / 4f, 40)
        }

        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2200
            interpolator = LinearInterpolator()
            addUpdateListener {
                updateParticles()
                invalidate()
            }
            start()
        }
    }

    private fun spawnBurst(x: Float, y: Float, count: Int) {
        repeat(count) {
            val angle = Random.nextFloat() * 2 * Math.PI
            val speed = 6f + Random.nextFloat() * 10f
            particles += Particle(
                x = x,
                y = y,
                vx = (cos(angle) * speed).toFloat(),
                vy = (sin(angle) * speed).toFloat(),
                color = colors.random(),
                radius = 4f + Random.nextFloat() * 5f
            )
        }
    }

    private fun updateParticles() {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.x += p.vx
            p.y += p.vy
            p.vy += 0.25f
            p.vx *= 0.98f
            p.vy *= 0.98f
            p.life -= 0.015f
            if (p.life <= 0f) iterator.remove()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (p in particles) {
            paint.color = p.color
            paint.alpha = (p.life * 255).toInt().coerceIn(0, 255)
            canvas.drawCircle(p.x, p.y, p.radius, paint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}