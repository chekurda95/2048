package com.chekurda.game_2048.screens.game.presentation.views.field.layouts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig
import kotlin.math.roundToInt

internal class GameBoard(context: Context) : GameFieldObject(context) {

    private val backgroundColors = arrayOf(
        ContextCompat.getColor(context, R.color.game_screen_background),
        ContextCompat.getColor(context, R.color.game_field_background)
    )
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundCornerRadius = resources.getDimensionPixelOffset(R.dimen.field_corner_radius).toFloat()

    private val cellSeparatorWidth = resources.getDimensionPixelOffset(R.dimen.field_cell_separator_width)
    private val cellCornerRadius = resources.getDimensionPixelOffset(R.dimen.cell_corner_radius).toFloat()
    private val cellBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.empty_cell_background_color)
    }

    private val cellsPositions = HashMap<Int, RectF>()

    override fun setResolution(width: Int, height: Int) {
        super.setResolution(width, height)
        layoutCells()
    }

    private fun layoutCells() {
        cellsPositions.clear()
        val cellSize = getCellSize()
        var previousCellRect = RectF(
            0f,
            cellSeparatorWidth.toFloat(),
            0f,
            cellSeparatorWidth + cellSize.toFloat()
        )
        for (row in 0 until GameConfig.GAME_FIELD_ROW_SIZE) {
            for (column in 0 until GameConfig.GAME_FIELD_ROW_SIZE) {
                val left = previousCellRect.right + cellSeparatorWidth
                val cellRect = RectF(
                    left,
                    previousCellRect.top,
                    left + cellSize,
                    previousCellRect.bottom
                )
                cellsPositions[column + row * GameConfig.GAME_FIELD_ROW_SIZE] = cellRect
                previousCellRect = RectF(cellRect)
            }

            val firstRectInRow = cellsPositions[row * GameConfig.GAME_FIELD_ROW_SIZE]!!
            val newPreviousTop = firstRectInRow.bottom + cellSeparatorWidth
            previousCellRect.set(0f, newPreviousTop, 0f, newPreviousTop + cellSize)
        }
    }

    private fun getCellSize(): Int {
        val separatorsSumWidth = cellSeparatorWidth * (GameConfig.GAME_FIELD_ROW_SIZE + 1)
        val cellSize = (width - separatorsSumWidth) / GameConfig.GAME_FIELD_ROW_SIZE.toFloat()
        return maxOf(cellSize, 0f).roundToInt()
    }

    override fun draw(canvas: Canvas) {
        drawBackground(canvas)
        drawCells(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(
            rect,
            backgroundPaint.apply { color = backgroundColors[0] }
        )
        canvas.drawRoundRect(
            rect,
            backgroundCornerRadius,
            backgroundCornerRadius,
            backgroundPaint.apply { color = backgroundColors[1] }
        )
    }

    private fun drawCells(canvas: Canvas) {
        cellsPositions.forEach {
            canvas.drawRoundRect(it.value, cellCornerRadius, cellCornerRadius, cellBackgroundPaint)
        }
    }
}