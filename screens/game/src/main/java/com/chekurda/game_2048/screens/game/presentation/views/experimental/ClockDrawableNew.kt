package com.chekurda.game_2048.screens.game.presentation.views.experimental

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.core.graphics.withRotation
import com.chekurda.design.custom_view_tools.utils.dp

internal class ClockDrawableNew(context: Context) : Drawable() {

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private var hourDurationTimeMs: Int = DEFAULT_ROTATION_TIME_MS
    private var minuteDurationTimeMs: Int = (DEFAULT_ROTATION_TIME_MS * DEFAULT_MINUTE_ROTATION_RATIO).toInt()
    private var startTime = System.currentTimeMillis()

    @get:Px
    var size: Int
        get() = minOf(bounds.width(), bounds.height())
        set(value) {
            setBounds(
                bounds.left,
                bounds.top,
                bounds.left + value,
                bounds.top + value
            )
        }

    init {
        size = context.dp(DEFAULT_SIZE_DP)
    }

    fun setRotationDuration(
        hourRotationMs: Int = DEFAULT_ROTATION_TIME_MS,
        minuteRotationMs: Int = (hourRotationMs * DEFAULT_MINUTE_ROTATION_RATIO).toInt()
    ) {
        hourDurationTimeMs = hourRotationMs
        minuteDurationTimeMs = minuteRotationMs
    }

    fun setColor(@ColorInt color: Int) {
        if (color == paint.color) return
        paint.color = color
    }

    override fun setAlpha(@IntRange(from = 0, to = PAINT_MAX_ALPHA.toLong()) alpha: Int) {
        if (alpha == paint.alpha) return
        paint.alpha = alpha
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean =
        super.setVisible(visible, restart).also {
            if (restart) startTime = System.currentTimeMillis()
        }

    override fun onBoundsChange(bounds: Rect?) {
        paint.strokeWidth = size * STROKE_WIDTH_RATIO
    }

    override fun draw(canvas: Canvas) {
        val (centerX, centerY) = bounds.centerX().toFloat() to bounds.centerY().toFloat()
        val diameter = size
        val currentTime = System.currentTimeMillis()

        with(canvas) {
            drawCircle(
                centerX,
                centerY,
                diameter / 2f,
                paint
            )
            drawArrow(
                currentTime = currentTime,
                rotateTime = minuteDurationTimeMs,
                centerX = centerX,
                centerY = centerY,
                stopY = centerY - diameter * MINUTE_ARROW_WIDTH_RATIO
            )
            drawArrow(
                currentTime = currentTime,
                rotateTime = hourDurationTimeMs,
                centerX = centerX,
                centerY = centerY,
                stopX = centerY + diameter * HOUR_ARROW_WIDTH_RATIO
            )
        }
    }

    override fun getIntrinsicHeight(): Int =
        bounds.width()

    override fun getIntrinsicWidth(): Int =
        bounds.height()

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun getOpacity(): Int =
        PixelFormat.TRANSPARENT

    private fun Canvas.drawArrow(
        currentTime: Long,
        rotateTime: Int,
        centerX: Float,
        centerY: Float,
        stopX: Float = centerX,
        stopY: Float = centerY
    ) {
        withRotation(
            degrees = CIRCLE_DEGREES * ((currentTime - startTime) % rotateTime) / rotateTime.toFloat(),
            pivotX = centerX,
            pivotY = centerY
        ) {
            drawLine(
                centerX,
                centerY,
                stopX,
                stopY,
                paint
            )
        }
    }
}

private const val PAINT_MAX_ALPHA = 255
private const val DEFAULT_SIZE_DP = 12
private const val DEFAULT_ROTATION_TIME_MS = 4500
private const val DEFAULT_MINUTE_ROTATION_RATIO = 1 / 3f
private const val STROKE_WIDTH_RATIO = 1 / 10f
private const val MINUTE_ARROW_WIDTH_RATIO = 1 / 3.75f
private const val HOUR_ARROW_WIDTH_RATIO = 1 / 5f
private const val CIRCLE_DEGREES = 360