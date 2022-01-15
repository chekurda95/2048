package com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.chekurda.game_2048.screens.game.R
import java.lang.RuntimeException

/**
 * Размер текста для ячейки игрового поля.
 */
internal enum class CellTextSize(private val dp: Int) {
    ONE_NUMBER(38),
    TWO_NUMBERS(36),
    THREE_NUMBERS(32),
    FOUR_NUMBERS(25);

    companion object {
        fun getTextSize(value: Int): Int =
            when (val length = value.toString().length) {
                1 -> ONE_NUMBER.dp
                2 -> TWO_NUMBERS.dp
                3 -> THREE_NUMBERS.dp
                4 -> FOUR_NUMBERS.dp
                else -> {
                    val count = length - 4
                    val textSize = FOUR_NUMBERS.dp - 5 * count
                    maxOf(textSize, MIN_TEXT_SIZE)
                }
            }

        private const val MIN_TEXT_SIZE = 5
    }
}

/**
 * Цвета ячейки игрового поля.
 */
internal enum class CellColors(
    @ColorRes val textColor: Int,
    @ColorRes val backgroundColor: Int
) {
    V0(Color.TRANSPARENT, Color.TRANSPARENT),
    V2(R.color.cell_text_color_gray, R.color.cell_background_color_2),
    V4(R.color.cell_text_color_gray, R.color.cell_background_color_4),
    V8(R.color.cell_text_color_white, R.color.cell_background_color_8),
    V16(R.color.cell_text_color_white, R.color.cell_background_color_16),
    V32(R.color.cell_text_color_white, R.color.cell_background_color_32),
    V64(R.color.cell_text_color_white, R.color.cell_background_color_64),
    V128(R.color.cell_text_color_white, R.color.cell_background_color_128),
    V256(R.color.cell_text_color_white, R.color.cell_background_color_256),
    V512(R.color.cell_text_color_white, R.color.cell_background_color_512),
    V1024(R.color.cell_text_color_white, R.color.cell_background_color_1024),
    V2048(R.color.cell_text_color_white, R.color.cell_background_color_1024);

    companion object {
        fun getColors(value: Int): CellColors =
            when {
                value == 0 -> V0
                value % 2048 == 0 -> V2048
                value % 1024 == 0 -> V1024
                value % 512 == 0 -> V512
                value % 256 == 0 -> V256
                value % 128 == 0 -> V128
                value % 64 == 0 -> V64
                value % 32 == 0 -> V32
                value % 16 == 0 -> V16
                value % 8 == 0 -> V8
                value % 4 == 0 -> V4
                value % 2 == 0 -> V2
                else -> throw RuntimeException("Нечетное число оказалось внутри алгоритма")
            }
    }
}