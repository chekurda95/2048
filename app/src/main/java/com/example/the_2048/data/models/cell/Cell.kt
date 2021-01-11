package com.example.the_2048.data.models.cell

data class Cell(val position: Int, @Volatile var value: Int = 0, @Volatile var isSum: Boolean = false)
