package com.chekurda.game_2048.screens.game.presentation.gamefield.view

internal interface IGameFragment {

    fun drawField(listOfCellsValues: List<Int>)

    fun animateField(movedList: List<Int>)
}