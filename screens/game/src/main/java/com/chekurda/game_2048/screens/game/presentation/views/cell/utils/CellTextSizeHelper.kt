package com.chekurda.game_2048.screens.game.presentation.views.cell.utils

import android.content.Context
import android.graphics.Rect
import android.text.TextPaint
import androidx.annotation.Px
import com.chekurda.design.custom_view_tools.utils.dp
import org.apache.commons.lang3.StringUtils

object CellTextSizeHelper {

    @Px private var cellSize: Int = 0
    private val cacheSizes = hashMapOf<String, Int>()

    fun calculateTextSize(context: Context, value: String, @Px cellSize: Int): Float {
        if (value == StringUtils.EMPTY || cellSize == 0) return 0f
        if (this.cellSize != cellSize) cacheSizes.clear()

        val minPadding = context.dp(CELL_TEXT_PADDING_DP)
        val availableSpace = cellSize - minPadding * 2

        var textSizeDp = CELL_DEFAULT_TEXT_SIZE_DP
        val infelicity = context.dp(CELL_TEXT_SIZE_INFELICITY_DP)
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = context.dp(textSizeDp).toFloat()
            isFakeBoldText = true
        }
        val textBounds = Rect().also {
            textPaint.getTextBounds(value, 0, value.length, it)
        }

        var resultTextSize: Float? = null

        do {
            if ((textBounds.width() in (availableSpace - infelicity)..(availableSpace + infelicity) && textBounds.height() <= availableSpace)
                || (textBounds.height() in (availableSpace - infelicity)..(availableSpace + infelicity) && textBounds.width() <= availableSpace)) {
                resultTextSize = textSizeDp
                break
            }
            if (textBounds.width() <= availableSpace && textBounds.height() <= availableSpace) {
                textSizeDp *= 1.5f
                textPaint.textSize = textSizeDp
                textPaint.getTextBounds(value, 0, value.length, textBounds)
            } else {
                textSizeDp *= 0.75f
                textPaint.textSize = textSizeDp
                textPaint.getTextBounds(value, 0, value.length, textBounds)
            }
        } while (resultTextSize == null)

        return resultTextSize!!
    }
}

private const val CELL_TEXT_PADDING_DP = 15
private const val CELL_TEXT_SIZE_INFELICITY_DP = 2
private const val CELL_DEFAULT_TEXT_SIZE_DP = 20f