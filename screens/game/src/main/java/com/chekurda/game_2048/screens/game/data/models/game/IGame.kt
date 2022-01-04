package com.chekurda.game_2048.screens.game.data.models.game

import com.chekurda.game_2048.screens.game.data.models.game_field.GameField

interface IGame {

    fun startNewGame()

    fun resumeGame()

    fun gameIsOver()

    fun getField(): GameField
}