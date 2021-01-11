package com.example.the_2048.data.models.game

import com.example.the_2048.data.models.cell.Cell
import com.example.the_2048.data.models.game_field.GameField
import java.util.*

class Game : IGame, IMove {

    private lateinit var field: GameField

    companion object {
        var counter = 0
    }

    override fun getField() = field

    override fun startNewGame() {
        field = GameField()
        field.addNewFolder()
    }

    override fun resumeGame() {
        //load List from base
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
        counter++
        if (counter == 4 && field.isMoved) {
            counter = 0
            field.isMoved = false
            field.addNewFolder()
        } else if(counter == 4){
            counter = 0
            field.getMovedList().clear()
        }
    }
}
