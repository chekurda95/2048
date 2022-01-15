package com.chekurda.game_2048.screens.game.domain

import com.chekurda.game_2048.screens.game.data.models.game.GameState
import com.chekurda.game_2048.screens.game.data.models.game.GameState.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

internal class GameController {

    private val stateSubject = BehaviorSubject.createDefault(INIT)

    val stateObservable: Observable<GameState> = stateSubject.distinctUntilChanged()

    val state: GameState
        get() = stateSubject.value!!

    fun startNewGame() {
        stateSubject.onNext(START_NEW_GAME)
    }

    fun gameIsReady() {
        stateSubject.onNext(PLAYING)
    }

    fun onFieldChanged() = Unit

    interface GameControllerConnector {

        fun attachGameController(controller: GameController)
    }
}