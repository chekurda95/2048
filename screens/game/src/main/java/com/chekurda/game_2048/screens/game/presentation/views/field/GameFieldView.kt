package com.chekurda.game_2048.screens.game.presentation.views.field

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.chekurda.common.surface.SurfaceDrawingThread
import com.chekurda.common.surface.SurfaceLayout
import com.chekurda.common.surface.SurfaceLayout.DrawingLayout
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.GAME_FIELD_ROW_SIZE
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.GameBoard
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell.GameCell
import java.lang.Math.random
import java.lang.RuntimeException

internal class GameFieldView(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context), DrawingLayout {

    private val board = GameBoard(context)
    private val cells = HashMap<Int, GameCell>()

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

    override fun update(deltaTimeMs: Int) = Unit

    override fun drawLayout(canvas: Canvas) {
        board.draw(canvas)
        cells.forEach {
            it.value.draw(canvas)
        }
    }

    fun startNewGame() {
        cells.clear()
        addNewCell()
    }

    private fun addNewCell() {
        val emptyPositions = allPositionList.toMutableList()
        cells.forEach {
            emptyPositions.remove(it.key)
        }
        if (emptyPositions.isNotEmpty()) {
            val randomPosition = ((random() * 100).toInt() % emptyPositions.size)
            val randomValue = ((random() * 100).toInt() % 2 + 1) * 2

            cells[randomPosition] = GameCell(context).apply {
                value = randomValue
                setRect(board.getRectForCell(randomPosition))
            }
        }
    }

    private val allPositionList: List<Int> = mutableListOf<Int>().apply {
        for (i in 0 until GAME_FIELD_ROW_SIZE * GAME_FIELD_ROW_SIZE) {
            add(i)
        }
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
            board.setResolution(width, height)
            startNewGame()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            thread.interrupt()
        }
    }
}