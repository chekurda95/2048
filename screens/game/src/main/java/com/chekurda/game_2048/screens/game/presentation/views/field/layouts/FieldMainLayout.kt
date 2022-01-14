package com.chekurda.game_2048.screens.game.presentation.views.field.layouts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.SurfaceHolder
import androidx.core.content.ContextCompat
import com.chekurda.common.surface.SurfaceDrawingThread
import com.chekurda.common.surface.SurfaceLayout
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig

class FieldMainLayout(
    private val context: Context,
    surfaceHolder: SurfaceHolder
) : SurfaceLayout.DrawingLayout {

    private val backgroundRect = Rect()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.game_screen_background)
    }

    init {
        surfaceHolder.addCallback(FieldSurfaceCallback(surfaceHolder))
    }

    private fun setResolution(width: Int, height: Int) {
        backgroundRect.set(
            0,
            0,
            width,
            height
        )
    }

    override fun update(deltaTimeMs: Int) = Unit

    override fun draw(canvas: Canvas) {
        canvas.drawRect(backgroundRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.RED })
        Log.e("TAGTAG", "draw")
    }

    private inner class FieldSurfaceCallback(
        private val surfaceHolder: SurfaceHolder
    ) : SurfaceHolder.Callback {

        private lateinit var thread: Thread

        override fun surfaceCreated(holder: SurfaceHolder) {
            thread = SurfaceDrawingThread(
                surfaceLayout = SurfaceLayout(this@FieldMainLayout, surfaceHolder),
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