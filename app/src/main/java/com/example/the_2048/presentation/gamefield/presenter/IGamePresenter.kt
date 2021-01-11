package com.example.the_2048.presentation.gamefield.presenter

import com.example.the_2048.presentation.gamefield.delegates.SwipeListener

interface IGamePresenter : SwipeListener {

    fun startNewGame()
}