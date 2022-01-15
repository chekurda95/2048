package com.chekurda.game_2048.screens.game.domain

import com.chekurda.game_2048.screens.game.data.models.game.GameState
import com.chekurda.game_2048.screens.game.data.models.game.GameState.INIT
import com.chekurda.game_2048.screens.game.data.models.game.GameState.START_NEW_GAME

internal class GameController {

    private val stateChangeListeners = mutableListOf<GameStateChangeListener>()

    private var state: GameState = INIT

    fun setStateListener(listener: GameStateChangeListener) {
        stateChangeListeners.add(listener)
    }

    fun startNewGame() {
        state = START_NEW_GAME
    }

    fun onFieldChanged() = Unit

    fun onDestroy() {
        stateChangeListeners.clear()
    }

    fun interface GameStateChangeListener {

        fun onStateChanged(state: GameState)
    }
}