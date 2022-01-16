package com.chekurda.game_2048.screens.game.presentation.views.field.layouts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.content.ContextCompat.getColor
import com.chekurda.design.custom_view_tools.utils.AntiPaint
import com.chekurda.design.custom_view_tools.utils.copy
import com.chekurda.design.custom_view_tools.utils.toFloat
import com.chekurda.design.custom_view_tools.utils.update
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.gameFieldRowSize
import java.lang.RuntimeException
import kotlin.math.roundToInt

internal class GameBoard(context: Context) : GameFieldObject(context) {

    private val backgroundColors = arrayOf(
        getColor(context, R.color.game_screen_background),
        getColor(context, R.color.game_field_background)
    )
    private val backgroundCornerRadius = resources.getDimensionPixelOffset(R.dimen.field_corner_radius).toFloat()

    private val cellSeparatorWidth = resources.getDimensionPixelOffset(R.dimen.field_cell_separator_width)
    private val cellCornerRadius = resources.getDimensionPixelOffset(R.dimen.cell_corner_radius).toFloat()
    private val cellBackgroundPaint = AntiPaint().apply {
        color = getColor(context, R.color.empty_cell_background_color)
    }

    private val cellsPositions = HashMap<Int, RectF>()

    var isReady: Boolean = false

    fun getRectForCell(number: Int): RectF =
        requireCellRect(number)

    override fun onSizeChanged(width: Int, height: Int) {
        placeCells()
    }

    private fun placeCells() {
        cellsPositions.clear()

        val cellSize = getCellSize()
        var previousCellRect = Rect().update(
            top = cellSeparatorWidth,
            bottom = cellSeparatorWidth + cellSize
        ).toFloat()

        for (row in 0 until gameFieldRowSize) {
            for (column in 0 until gameFieldRowSize) {
                val left = previousCellRect.right + cellSeparatorWidth
                val cellRect = previousCellRect.copy().update(
                    left = left,
                    right = left + cellSize
                )
                cellsPositions[column + row * gameFieldRowSize] = cellRect
                previousCellRect = cellRect.copy()
            }

            fun changePreviousRectForNewRow() {
                val firstRectInRow = cellsPositions[row * gameFieldRowSize]!!
                val newPreviousTop = firstRectInRow.bottom + cellSeparatorWidth
                previousCellRect = RectF().update(
                    top = newPreviousTop,
                    bottom = newPreviousTop + cellSize
                )
            }

            changePreviousRectForNewRow()
        }

        isReady = true
    }

    private fun getCellSize(): Int {
        val separatorsSumWidth = cellSeparatorWidth * (gameFieldRowSize + 1)
        val cellSize = (width - separatorsSumWidth) / gameFieldRowSize.toFloat()
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

    private fun requireCellRect(number: Int): RectF =
        cellsPositions[number].let {
            it ?: throw RuntimeException("Запрошены координаты ячейки для несуществующей позиции: cellNumber = $number")
        }
}