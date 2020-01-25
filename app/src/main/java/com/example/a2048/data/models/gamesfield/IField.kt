package com.example.a2048.data.models.gamesfield

import com.example.a2048.data.models.cell.Cell

interface IField{

    fun getCell(x: Int, y: Int): Cell

    fun getCellsStringList(): List<String>

    fun getMovedList(): List<Int>

    fun addNewFolder()
}