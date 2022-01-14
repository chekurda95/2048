package com.chekurda.common.surface

import androidx.annotation.IntRange

class SurfaceDrawingThread(
    private val surfaceLayout: SurfaceLayout,
    @IntRange(from = ONE_FPS, to = MAX_FPS) fps: Int = MAX_FPS.toInt()
) : Thread() {

    private val drawingHelper = FPSHelper(fps)
    private var previousTimeMs = 0L
    private var isRunning = false

    override fun run() {
        super.run()

        previousTimeMs = System.currentTimeMillis()
        while (isRunning) {
            surfaceLayout.update((System.currentTimeMillis() - previousTimeMs).toInt())
            if (drawingHelper.isTimeToDraw) {
                surfaceLayout.performDrawing()
            }
        }
    }

    override fun start() {
        super.start()
        isRunning = true
    }

    override fun interrupt() {
        super.interrupt()
        isRunning = false
    }
}