package com.chekurda.game_2048.screens.game.presentation.views.cell.params

import android.content.Context
import android.graphics.Color.TRANSPARENT
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat.getColor
import com.chekurda.design.custom_view_tools.utils.dp
import org.apache.commons.lang3.StringUtils.EMPTY

internal data class CellParams(
    val value: String = EMPTY,
    val textSize: Int = 0,
    @ColorInt val textColor: Int = TRANSPARENT,
    @ColorInt val backgroundColor: Int = TRANSPARENT
)

internal fun createCellParams(context: Context, value: Int): CellParams {
    val textSizeDp = context.dp(CellTextSize.getTextSize(value))
    val colors = CellColors.getColors(value)

    return CellParams(
        value = value.toString(),
        textSize = textSizeDp,
        textColor = getColor(context, colors.textColor),
        backgroundColor = getColor(context, colors.backgroundColor)
    )
}