package com.chekurda.game_2048.screens.game.presentation.views.field

import android.graphics.RectF
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.gameFieldRowSize
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell.GameCell
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeDirection
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeListener

internal class SwipeController(private val cellsHolder: CellsHolder) : SwipeListener {

    interface CellsHolder {

        val cells: HashMap<Int, GameCell>

        fun getRectOfPosition(position: Int): RectF
    }

    private var movingCells: MutableList<MovingCell> = mutableListOf()

    private fun updateMovingList() {
        movingCells.clear()

        if (swipeDirection == SwipeDirection.RIGHT) {
            val cells: MutableList<MovingCell> = mutableListOf()
            for (row in 0 until gameFieldRowSize) {
                val emptyPositionList = mutableListOf<Int>()
                var previousCell: MovingCell? = null

                for (column in gameFieldRowSize - 1 downTo 0) {
                    val position = getPosition(row, column)
                    val cellOnPosition = cellsHolder.cells[position]

                    if (cellOnPosition == null) {
                        emptyPositionList.add(position)
                        continue
                    }

                    val endPosition = emptyPositionList
                        .firstOrNull()
                        ?.also {
                            emptyPositionList.remove(it)
                            emptyPositionList.add(position)
                        } ?: position
                    val endPoint = cellsHolder.getRectOfPosition(endPosition)
                    val sumWith = previousCell
                        ?.takeIf { it.cell.value == cellOnPosition.value }
                        ?.cell

                    val movingCell = MovingCell(
                        cellOnPosition,
                        position,
                        endPosition,
                        endPoint,
                        sumWith
                    )
                    cells.add(movingCell)
                    previousCell = movingCell
                }
            }
            movingCells.addAll(cells)
        }
        isRunning = true
    }


    private fun getPosition(row: Int, column: Int) =
        gameFieldRowSize * row + column

    data class MovingCell(
        val cell: GameCell,
        val position: Int,
        val endPosition: Int,
        val endPoint: RectF,
        var sumWithCell: GameCell? = null
    )

    var isRunning: Boolean = false
    var swipeDirection: SwipeDirection? = null
        private set

    override fun onSwipe(direction: SwipeDirection) {
        swipeDirection = direction
        updateMovingList()
    }

    fun update(deltaTimeMs: Int) {
        if (!isRunning) return
        swipeDirection?.let { onSwipeRunning(it, deltaTimeMs) }
    }

    fun stopMoving() {
        swipeDirection = null
    }

    private fun onSwipeRunning(swipeDirection: SwipeDirection, deltaTimeMs: Int) {
        val moveDelta = deltaTimeMs / 150f * 1000
        val finishedCells = mutableListOf<MovingCell>()
        var isChanged = false
        movingCells.forEach { movingCell ->
            when (swipeDirection) {
                SwipeDirection.UP -> Unit
                SwipeDirection.LEFT -> Unit
                SwipeDirection.RIGHT -> {
                    val dx = minOf(moveDelta, movingCell.endPoint.right - movingCell.cell.right)
                    if (dx > 0) {
                        movingCell.cell.translate(x = dx)
                        isChanged = true
                    } else {
                        cellsHolder.cells.remove(movingCell.position)

                        val sumCell = movingCell.sumWithCell
                        if (sumCell == null) {
                            cellsHolder.cells[movingCell.endPosition] = movingCell.cell
                        } else {
                            sumCell.value = sumCell.value * 2
                        }

                        finishedCells.add(movingCell)
                    }
                }
                SwipeDirection.DOWN -> Unit
            }
        }
        movingCells.removeAll(finishedCells)
        if (!isChanged) {
            this.swipeDirection = null
            isRunning = false
        }
    }

    /*private fun onSwipeRunning(swipeDirection: SwipeDirection, deltaTimeMs: Int) {
        val moveDelta = deltaTimeMs / 150f * board.height
        var isChanged = false
        when (swipeDirection) {
            SwipeDirection.UP -> {
                val maxTop = board.getRectForCell(0).top
                cells.forEach {
                    val cell = it.value
                    val dy = -minOf(cell.top - maxTop, moveDelta)
                    it.value.translate(y = dy)
                    if (!isChanged && dy <= moveDelta) isChanged = true
                }
            }
            SwipeDirection.LEFT -> Unit
            SwipeDirection.RIGHT -> Unit
            SwipeDirection.DOWN -> Unit
        }
        if (!isChanged) swipeState = null
    }

    init {
        fun findEmptyPosition(direction: SwipeDirection): Int {
            TODO()
        }

        fun moveCells() {
            TODO()
        }

        val movingList = mutableListOf<Pair<GameCell, RectF>>()

        cells.forEach {
            val number = it.key
            val cell = it.value
            val emptyPosition = findEmptyPosition(SwipeDirection.RIGHT)
            val emptyRect = board.getRectForCell(emptyPosition)

            movingList.add(cell to emptyRect)
        }



        // val cell = cell from cycle
        // val availableRect = findAvailableRect(for cellNumber, swipeDirection)
        // moveTo(availableRect)
        // onAnimationEnd {
        //    val nextCell = getCellForPosition(availablePosition + nextPos)
        //    if (nextCell.value == cell.value) {
        //        nextCell.animateSum()
        //        cell.isVisible = false
        //        removeCellFromList()
        //    }
        // }
    }

    private fun findAvailablePosition(cellNumber: Int, direction: SwipeDirection): RectF {
        return RectF()
    }

    private fun swipeCells(firstCell: Cell, secondCell: Cell) {
        if (firstCell.value == 0 && secondCell.value != 0) {
            firstCell.value = secondCell.value
            secondCell.value = 0
            if (secondCell.isSum) {
                firstCell.isSum = true
                secondCell.isSum = false
            }
            //isMoved = true
        } else if (firstCell.value != 0 && firstCell.value == secondCell.value && !secondCell.isSum && !firstCell.isSum) {
            firstCell.isSum = true
            firstCell.value *= 2
            secondCell.value = 0
            //isMoved = true
        }
    }

    private fun rightSwipe() {
        for (y in 0..3) {
            for (x in 3 downTo 1) {
                swipeCells(
                    getCell(x, y),
                )
            }
        }
    }

    private fun leftSwipe() {
        for (y in 0..3) {
            for (x in 0..2) {
                swipeCells(
                    getCell(x, y),
                    getCell(x + 1, y)
                )
            }
        }
    }

    private fun upSwipe() {
        for (x in 0..3) {
            for (y in 0..2) {
                swipeCells(
                    getCell(x, y),
                    getCell(x, y+1)
                )
            }
        }
    }

    private fun downSwipe() {
        for (x in 0..3) {
            for (y in 3 downTo 1) {
                swipeCells(
                    getCell(x, y),
                    getCell(x, y-1)
                )
            }
        }
    }*/
}