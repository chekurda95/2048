package com.example.a2048.data.models.game

import com.example.a2048.data.models.gamesfield.Field

interface IGame {

    fun startNewGame()

    fun resumeGame()

    fun gameIsOver()

    fun getField(): Field
}