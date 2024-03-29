package com.chekurda.game_2048.screens.game.presentation.views.config

internal object GameConfig {

    var gameFPS = DEFAULT_GAME_FPS
        private set

    var gameFieldRowSize = DEFAULT_GAME_FIELD_ROW_SIZE
        private set

    var cellMovementDuration = DEFAULT_CELL_MOVEMENT_DURATION_MS
        private set

    var cellShowingDuration = DEFAULT_CELL_SHOWING_DURATION_MS
        private set

    var cellSumDuration = DEFAULT_CELL_SUM_DURATION_MS
        private set
}

private const val DEFAULT_GAME_FPS = 60
private const val DEFAULT_GAME_FIELD_ROW_SIZE = 4
private const val DEFAULT_CELL_MOVEMENT_DURATION_MS = 140
private const val DEFAULT_CELL_SHOWING_DURATION_MS = 200
private const val DEFAULT_CELL_SUM_DURATION_MS = 200