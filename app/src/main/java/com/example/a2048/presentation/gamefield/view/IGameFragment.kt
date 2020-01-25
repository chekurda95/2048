package com.example.a2048.presentation.gamefield.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface IGameFragment: MvpView{

    fun drawField(listOfCellsValues: List<String>)

    fun animateField(movedList: List<Int>)
}