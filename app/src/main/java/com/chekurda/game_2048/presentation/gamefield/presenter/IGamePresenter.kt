package com.chekurda.game_2048.presentation.gamefield.presenter

import com.chekurda.game_2048.presentation.gamefield.delegates.SwipeListener
import com.chekurda.game_2048.presentation.gamefield.view.IGameFragment

interface IGamePresenter : SwipeListener {

    fun startNewGame()

    fun attachView(view: IGameFragment)

    fun detachView()
}