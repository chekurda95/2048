package com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe

internal interface SwipeListener {

    fun onSwipe(direction: SwipeDirection)
}

internal enum class SwipeDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
}