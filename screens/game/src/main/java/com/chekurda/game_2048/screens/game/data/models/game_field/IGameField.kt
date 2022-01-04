package com.chekurda.game_2048.screens.game.data.models.game_field

import com.chekurda.game_2048.screens.game.data.models.cell.Cell

interface IGameField{

    fun getCell(x: Int, y: Int): Cell

    fun getCellsValues(): List<Int>

    fun getMovedList(): List<Int>

    fun addNewFolder()
}