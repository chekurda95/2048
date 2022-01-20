package com.chekurda.game_2048.screens.game.presentation.views.field.domain

import android.graphics.RectF
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.gameFieldRowSize
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell.GameCell
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeDirection
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeListener
import java.util.concurrent.ConcurrentHashMap

internal class SwipeController(private val cellsHolder: CellsProvider) : SwipeListener {

    interface CellsProvider {

        val cells: ConcurrentHashMap<Int, GameCell>

        fun getRectForPosition(position: Int): RectF
    }

    var swipeFinishedListener: ((Boolean) -> Unit)? = null

    private var isRunning: Boolean = false
    private var isChanged: Boolean = false
    private var swipeDirection: SwipeDirection? = null
    private val movingCells: MutableList<MovingCell> = mutableListOf()

    override fun onSwipe(direction: SwipeDirection) {
        swipeDirection = direction
        updateMovingCells()
    }

    fun update(deltaTimeMs: Int) {
        if (!isRunning) return
        swipeDirection?.let { onSwipeRunning(it, deltaTimeMs) }
    }

    fun stopMoving() {
        swipeDirection = null
    }

    private fun updateMovingCells() {
        movingCells.clear()

        val isHorizontalSwipe = swipeDirection == SwipeDirection.LEFT || swipeDirection == SwipeDirection.RIGHT
        val (rowRange, columnRange) = when (swipeDirection) {
            SwipeDirection.LEFT -> (0 until gameFieldRowSize) to (0 until gameFieldRowSize)
            SwipeDirection.UP -> (0 until gameFieldRowSize) to (0 until gameFieldRowSize)
            SwipeDirection.RIGHT -> (0 until gameFieldRowSize) to (gameFieldRowSize - 1 downTo 0)
            SwipeDirection.DOWN -> (gameFieldRowSize - 1 downTo 0) to (0 until gameFieldRowSize)
            else -> IntRange(0, 0) to IntRange(0, 0)
        }

        val emptyPositionList = mutableListOf<Int>()
        var previousCell: MovingCell? = null

        fun handlePosition(row: Int, column: Int) {
            val position = getPosition(row, column)
            val cellOnPosition = cellsHolder.cells[position]

            if (cellOnPosition == null) {
                emptyPositionList.add(position)
                return
            }

            val sumWith = previousCell
                ?.takeIf { it.cell.value == cellOnPosition.value && it.sumWithCell == null }
                ?.cell
            val endPosition = emptyPositionList
                .firstOrNull()
                ?.also {
                    if (sumWith == null) emptyPositionList.remove(it)
                    emptyPositionList.add(position)
                } ?: position
            if (sumWith != null) emptyPositionList.add(endPosition)
            val endPoint = cellsHolder.getRectForPosition(endPosition)

            val movingCell = MovingCell(
                cellOnPosition,
                position,
                endPosition,
                endPoint,
                sumWith
            )
            movingCells.add(movingCell)
            previousCell = movingCell
        }

        if (isHorizontalSwipe) {
            for (row in rowRange) {
                for (column in columnRange) handlePosition(row, column)
                emptyPositionList.clear()
                previousCell = null
            }
        } else {
            for (column in columnRange) {
                for (row in rowRange) handlePosition(row, column)
                emptyPositionList.clear()
                previousCell = null
            }
        }

        isRunning = true
    }

    private fun onSwipeRunning(swipeDirection: SwipeDirection, deltaTimeMs: Int) {
        val moveDelta = deltaTimeMs / 150f * 800
        val finishedCells = mutableListOf<MovingCell>()
        var isFieldChanged = false
        movingCells.forEach { movingCell ->
            val (dx, dy) = when (swipeDirection) {
                SwipeDirection.LEFT -> maxOf(-moveDelta, movingCell.endPoint.left - movingCell.cell.left) to 0f
                SwipeDirection.UP -> 0f to maxOf(-moveDelta, movingCell.endPoint.top - movingCell.cell.top)
                SwipeDirection.RIGHT -> minOf(moveDelta, movingCell.endPoint.right - movingCell.cell.right) to 0f
                SwipeDirection.DOWN -> 0f to minOf(moveDelta, movingCell.endPoint.bottom - movingCell.cell.bottom)
            }

            if (dx != 0f || dy != 0f) {
                movingCell.cell.translate(
                    minOf(dx, moveDelta),
                    minOf(dy, moveDelta)
                )
                isFieldChanged = true
            } else {
                cellsHolder.cells.remove(movingCell.position)

                val sumCell = movingCell.sumWithCell
                if (sumCell == null) {
                    cellsHolder.cells[movingCell.endPosition] = movingCell.cell
                } else {
                    sumCell.animateSum()
                }

                finishedCells.add(movingCell)
            }
        }
        movingCells.removeAll(finishedCells)

        if (isFieldChanged) {
            isChanged = true
        } else {
            this.swipeDirection = null
            isRunning = false
            swipeFinishedListener?.invoke(isChanged)
            isChanged = false
        }
    }

    private fun getPosition(row: Int, column: Int) =
        gameFieldRowSize * row + column

    private data class MovingCell(
        val cell: GameCell,
        val position: Int,
        val endPosition: Int,
        val endPoint: RectF,
        var sumWithCell: GameCell? = null
    )
}