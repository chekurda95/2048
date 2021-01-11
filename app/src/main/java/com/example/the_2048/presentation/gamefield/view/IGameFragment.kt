package com.example.the_2048.presentation.gamefield.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface IGameFragment: MvpView{

    fun drawField(listOfCellsValues: List<Int>)

    fun animateField(movedList: List<Int>)
}