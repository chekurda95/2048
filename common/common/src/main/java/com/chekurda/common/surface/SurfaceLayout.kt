package com.chekurda.common.surface

import android.graphics.Canvas
import android.view.SurfaceHolder

class SurfaceLayout(
    private val drawingLayout: DrawingLayout,
    private val surfaceHolder: SurfaceHolder
) {

    fun update(deltaTimeMs: Int) {
        drawingLayout.update(deltaTimeMs)
    }

    fun performDrawing() {
        val canvas = surfaceHolder.lockCanvas()
        try {
            synchronized(surfaceHolder) {
                drawingLayout.draw(canvas)
            }
        } catch (ex: Exception) {
            // Nothing
        } finally {
            canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
        }
    }

    interface DrawingLayout {

        fun update(deltaTimeMs: Int)

        fun draw(canvas: Canvas)
    }
}