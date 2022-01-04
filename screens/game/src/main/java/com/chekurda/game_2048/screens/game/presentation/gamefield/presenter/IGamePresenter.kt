package com.chekurda.game_2048.screens.game.presentation.gamefield.presenter

import com.chekurda.game_2048.screens.game.presentation.gamefield.delegates.SwipeListener
import com.chekurda.game_2048.screens.game.presentation.gamefield.view.IGameFragment

interface IGamePresenter : SwipeListener {

    fun startNewGame()

    fun attachView(view: IGameFragment)

    fun detachView()
}