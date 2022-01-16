package com.chekurda.common.surface

import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder

class SurfaceLayout(
    private val drawingLayout: DrawingLayout,
    private val surfaceHolder: SurfaceHolder
) {

    fun update(deltaTimeMs: Int) {
        drawingLayout.update(deltaTimeMs)
    }

    private var prevDrawTime = 0L

    fun performDrawing() {
        val canvas = surfaceHolder.lockCanvas()
        try {
            synchronized(surfaceHolder) {
                val delta = System.currentTimeMillis() - prevDrawTime
                prevDrawTime = System.currentTimeMillis()
                Log.e("TAGTAG", "draw delta = $delta")
                drawingLayout.drawLayout(canvas)
            }
        } catch (ex: Exception) {
            // Nothing
        } finally {
            canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
        }
    }

    interface DrawingLayout {

        fun update(deltaTimeMs: Int)

        fun drawLayout(canvas: Canvas)
    }
}