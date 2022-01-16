package com.chekurda.game_2048.screens.game.presentation.views.field.utils

/**
 * Вспомогательный класс для получения случайных значений.
 */
internal object FieldRandomHelper {

    /**
     * Возвращает рандомное число 2 или 4
     */
    val randomValue: Int
        get() = ((Math.random() * 100).toInt() % 2 + 1) * 2

    /**
     * Возвращает случайный элемент списка.
     */
    fun <T> getRandomItem(list: List<T>): T? =
        if (list.isNotEmpty()) {
            list[(Math.random() * 100).toInt() % list.size]
        } else {
            null
        }
}