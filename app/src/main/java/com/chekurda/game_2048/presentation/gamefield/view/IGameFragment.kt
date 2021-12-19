package com.chekurda.game_2048.presentation.gamefield.view

interface IGameFragment {

    fun drawField(listOfCellsValues: List<Int>)

    fun animateField(movedList: List<Int>)
}