package com.chekurda.game_2048.screens.game.presentation.views.cell

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.chekurda.game_2048.screens.game.R
import org.apache.commons.lang3.StringUtils.EMPTY

enum class CellParams(
    val value: String,
    val textSize: Float,
    @ColorInt val textColor: Int,
    @DrawableRes val backgroundRes: Int
) {
    C_0(V_0, TEXT_SIZE_1_NUMBER, R.color.cell_text_color_gray, R.drawable.rectangle),
    C_2(V_2, TEXT_SIZE_1_NUMBER, R.color.cell_text_color_gray, R.drawable.rectangle2),
    C_4(V_4, TEXT_SIZE_1_NUMBER, R.color.cell_text_color_white, R.drawable.rectangle4),
    C_8(V_8, TEXT_SIZE_1_NUMBER, R.color.cell_text_color_white, R.drawable.rectangle8),
    C_16(V_16, TEXT_SIZE_2_NUMBERS, R.color.cell_text_color_white, R.drawable.rectangle16),
    C_32(V_32, TEXT_SIZE_2_NUMBERS, R.color.cell_text_color_white, R.drawable.rectangle32),
    C_64(V_64, TEXT_SIZE_2_NUMBERS, R.color.cell_text_color_white, R.drawable.rectangle64),
    C_128(V_128, TEXT_SIZE_3_NUMBERS, R.color.cell_text_color_white, R.drawable.rectangle128),
    C_256(V_256, TEXT_SIZE_3_NUMBERS, R.color.cell_text_color_white, R.drawable.rectangle256),
    C_512(V_512, TEXT_SIZE_3_NUMBERS, R.color.cell_text_color_white, R.drawable.rectangle512),
    C_1024(V_1024, TEXT_SIZE_4_NUMBERS, R.color.cell_text_color_white, R.drawable.rectangle1024),
    C_2048(V_2048, TEXT_SIZE_4_NUMBERS, R.color.cell_text_color_white, R.drawable.rectangle1024)
}

private const val V_0 = EMPTY
private const val V_2 = "2"
private const val V_4 = "4"
private const val V_8 = "8"
private const val V_16 = "16"
private const val V_32 = "32"
private const val V_64 = "64"
private const val V_128 = "128"
private const val V_256 = "256"
private const val V_512 = "512"
private const val V_1024 = "1024"
private const val V_2048 = "2048"

private const val TEXT_SIZE_1_NUMBER = 38f
private const val TEXT_SIZE_2_NUMBERS = 36f
private const val TEXT_SIZE_3_NUMBERS = 32f
private const val TEXT_SIZE_4_NUMBERS = 25f