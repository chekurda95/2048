package com.chekurda.game_2048.screens.game.presentation.views.cell.utils

import android.content.Context
import android.graphics.Rect
import android.text.TextPaint
import android.util.Log
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

        val maxTextSize = context.dp(availableSpace).toFloat()
        val minTextSize = 1f
        val oneStep = context.dp(1)

        val infelicity = context.dp(CELL_TEXT_SIZE_INFELICITY_DP)
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = maxTextSize.toFloat()
            isFakeBoldText = true
        }
        val textBounds = Rect().also {
            textPaint.getTextBounds(value, 0, value.length, it)
        }

        var iterationsCount = 0

        Log.e("TAGTAG", "availableSpace $availableSpace")

        fun binarySearchSize(smallerTextSize: Float, biggerTextSize: Float): Float {

            iterationsCount++
            Log.e("TAGTAG", "iterationsCount = $iterationsCount")

            if (smallerTextSize < biggerTextSize) {
                val mid = smallerTextSize + (biggerTextSize - smallerTextSize) / 2f

                textPaint.textSize = mid
                textPaint.getTextBounds(value, 0, value.length, textBounds)
                val textWidth = textBounds.width()
                val textHeight = textBounds.height()
                Log.e("TAGTAG", "width = ${textBounds.width()}, height = ${textBounds.height()}")

                if ((textWidth in (availableSpace - infelicity)..availableSpace && textHeight <= availableSpace)
                    || (textHeight in (availableSpace - infelicity)..availableSpace && textWidth <= availableSpace)) {
                    return mid
                }

                return if (textWidth <= availableSpace && textHeight <= availableSpace) {
                    binarySearchSize(mid + oneStep, biggerTextSize)
                } else {
                    binarySearchSize(smallerTextSize, mid - oneStep)
                }
            }

            return minTextSize
        }

        return binarySearchSize(minTextSize, maxTextSize).also {
            Log.e("TAGTAG", "resultTextSize = $it, textWidth = ${textBounds.width()}, textHeight = ${textBounds.height()}, iterations count = $iterationsCount")
        }
    }
}

private const val CELL_TEXT_PADDING_DP = 15
private const val CELL_TEXT_SIZE_INFELICITY_DP = 3