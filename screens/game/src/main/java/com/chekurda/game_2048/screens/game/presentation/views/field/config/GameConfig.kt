package com.chekurda.game_2048.screens.game.presentation.views.field.config

internal object GameConfig {

    var gameFPS = DEFAULT_GAME_FPS
        private set

    var gameFieldRowSize = DEFAULT_GAME_FIELD_ROW_SIZE
        private set

    var cellShowingDuration = DEFAULT_CELL_SHOWING_DURATION_MS
        private set
}

private const val DEFAULT_GAME_FPS = 60
private const val DEFAULT_GAME_FIELD_ROW_SIZE = 4
private const val DEFAULT_CELL_SHOWING_DURATION_MS = 240