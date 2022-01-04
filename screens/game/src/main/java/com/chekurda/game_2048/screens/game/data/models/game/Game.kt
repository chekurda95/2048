package com.chekurda.game_2048.screens.game.data.models.game

import com.chekurda.game_2048.screens.game.data.models.cell.Cell
import com.chekurda.game_2048.screens.game.data.models.game_field.GameField
import java.util.*

class Game : IGame, IMove {

    private lateinit var field: GameField

    companion object {
        @Volatile var moveStep = 0
    }

    override fun getField() = field

    override fun startNewGame() {
        field = GameField()
        field.addNewFolder()
    }

    override fun resumeGame() {
        //load List from DataBase
        val list = ArrayList<Cell>()
        field = GameField(list)
    }

    override fun gameIsOver() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rightSwipe() {
        for (y in 0..3) {
            for (x in 3 downTo 1) {
                field.swipeOrMoveCells(
                    field.getCell(x, y),
                    field.getCell(x - 1, y)
                )
            }
        }
        checkCounter()
    }

    override fun leftSwipe() {
        for (y in 0..3) {
            for (x in 0..2) {
                field.swipeOrMoveCells(
                    field.getCell(x, y),
                    field.getCell(x + 1, y)
                )
            }
        }
        checkCounter()
    }

    override fun upSwipe() {
        for (x in 0..3) {
            for (y in 0..2) {
                field.swipeOrMoveCells(
                    field.getCell(x, y),
                    field.getCell(x, y+1)
                )
            }
        }
        checkCounter()
    }

    override fun downSwipe() {
        for (x in 0..3) {
            for (y in 3 downTo 1) {
                field.swipeOrMoveCells(
                    field.getCell(x, y),
                    field.getCell(x, y-1)
                )
            }
        }
        checkCounter()
    }

    private fun checkCounter(){
        moveStep++
        if (moveStep == 4 && field.isMoved) {
            moveStep = 0
            field.isMoved = false
            field.addNewFolder()
        } else if(moveStep == 4){
            moveStep = 0
            field.getMovedList().clear()
        }
    }
}
