package com.example.the_2048.data.models.game_field

import com.example.the_2048.data.models.cell.Cell

interface IGameField{

    fun getCell(x: Int, y: Int): Cell

    fun getCellsStringList(): List<String>

    fun getMovedList(): List<Int>

    fun addNewFolder()
}