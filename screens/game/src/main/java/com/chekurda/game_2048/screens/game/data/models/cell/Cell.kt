package com.chekurda.game_2048.screens.game.data.models.cell

internal data class Cell(
    val position: Int,
    @Volatile var value: Int = 0,
    @Volatile var isSum: Boolean = false
)
