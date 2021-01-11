package com.example.the_2048.data.models.game_field

import com.example.the_2048.data.models.cell.Cell

interface IGameField{

    fun getCell(x: Int, y: Int): Cell

    fun getCellsValues(): List<Int>

    fun getMovedList(): List<Int>

    fun addNewFolder()
}