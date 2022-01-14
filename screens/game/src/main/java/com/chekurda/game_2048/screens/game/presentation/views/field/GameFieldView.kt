package com.chekurda.game_2048.screens.game.presentation.views.field

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import com.chekurda.common.surface.SurfaceDrawingThread
import com.chekurda.common.surface.SurfaceLayout
import com.chekurda.common.surface.SurfaceLayout.DrawingLayout
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig
import java.lang.RuntimeException

internal class GameFieldView(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context), DrawingLayout {

    private val backgroundRect = RectF()
    private val backgroundColors = arrayOf(
        getColor(context, R.color.game_screen_background),
        getColor(context, R.color.game_field_background)
    )
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundCornerRadius = context.resources
        .getDimensionPixelOffset(R.dimen.field_corner_radius).toFloat()

    init {
        holder.addCallback(SurfaceCallback())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = if (!isInEditMode) MeasureSpec.getSize(heightMeasureSpec) else width
        setMeasuredDimension(width, height)

        if (measuredWidth != measuredHeight) {
            throw RuntimeException("Поле не квадратное: width = $measuredWidth, height = $measuredHeight")
        }
    }

    private fun setResolution(width: Int, height: Int) {
        backgroundRect.set(
            0f,
            0f,
            width.toFloat(),
            height.toFloat()
        )
    }

    override fun update(deltaTimeMs: Int) = Unit

    override fun drawLayout(canvas: Canvas) {
        canvas.drawRect(
            backgroundRect,
            backgroundPaint.apply { color = backgroundColors[0] }
        )
        canvas.drawRoundRect(
            backgroundRect,
            backgroundCornerRadius,
            backgroundCornerRadius,
            backgroundPaint.apply { color = backgroundColors[1] }
        )

        Log.e("TAGTAG", "draw")
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {

        private lateinit var thread: Thread

        override fun surfaceCreated(holder: SurfaceHolder) {
            thread = SurfaceDrawingThread(
                surfaceLayout = SurfaceLayout(this@GameFieldView, holder),
                fps = GameConfig.GAME_FPS
            ).apply { start() }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            setResolution(width, height)
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            thread.interrupt()
        }
    }
}