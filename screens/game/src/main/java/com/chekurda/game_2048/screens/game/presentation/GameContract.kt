package com.chekurda.game_2048.screens.game.presentation

import com.chekurda.common.base_fragment.BasePresenter
import com.chekurda.game_2048.screens.game.presentation.delegates.SwipeListener

internal interface GameFragmentView {

    fun drawField(listOfCellsValues: List<Int>)

    fun animateField(movedList: List<Int>)
}

internal interface GamePresenter : BasePresenter<GameFragmentView>, SwipeListener {

    fun startNewGame()
}