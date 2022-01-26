package com.chekurda.game_2048.screens.game.presentation.views.experimental

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.graphics.ColorUtils
import com.chekurda.design.custom_view_tools.utils.dp
import kotlin.math.min

internal class ClockDrawable(
    private val context: Context
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintAlpha = 255
    private var colorAlpha = 255
    private val startTime: Long
    private var color = Color.BLACK

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = context.dp(1).toFloat()
        startTime = System.currentTimeMillis()
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val r = min(bounds.width(), bounds.height())
        canvas.drawCircle(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),
            (r shr 1) - context.dp(0.5f).toFloat(),
            paint
        )
        val currentTime = System.currentTimeMillis()
        val rotateTime = 1500f
        val rotateHourTime = rotateTime * 3
        canvas.save()
        canvas.rotate(
            360 * ((currentTime - startTime) % rotateTime) / rotateTime,
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat()
        )
        canvas.drawLine(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),
            bounds.centerX().toFloat(),
            bounds.centerY() - context.dp(3).toFloat(),
            paint
        )
        canvas.restore()
        canvas.save()
        canvas.rotate(
            360 * ((currentTime - startTime) % rotateHourTime) / rotateHourTime,
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat()
        )
        canvas.drawLine(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),
            bounds.centerX() + context.dp(2.3f).toFloat(),
            bounds.centerY().toFloat(),
            paint
        )
        canvas.restore()
    }

    fun setColor(color: Int) {
        if (color != this.color) {
            colorAlpha = Color.alpha(color)
            paint.color = ColorUtils.setAlphaComponent(
                color,
                (paintAlpha * (colorAlpha / 255f)).toInt()
            )
        }
        this.color = color
    }

    override fun getIntrinsicHeight(): Int =
        context.dp(12)

    override fun getIntrinsicWidth(): Int =
        context.dp(12)

    override fun setAlpha(i: Int) {
        if (paintAlpha != i) {
            paintAlpha = i
            paint.alpha = (paintAlpha * (colorAlpha / 255f)).toInt()
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun getOpacity(): Int =
        PixelFormat.TRANSPARENT
}