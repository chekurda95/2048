package com.chekurda.game_2048.screens.game.presentation.views.field.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import androidx.annotation.Px
import com.chekurda.design.custom_view_tools.utils.dp
import org.apache.commons.lang3.StringUtils
import kotlin.math.roundToInt

/**
 * Вспомогательный класс для автоматического определения размера текста.
 */
internal object AutoTextSizeHelper {

    @Px
    private var containerSize: Int = 0

    /**
     * Закэшированные значения размеров текста для различного количества символов.
     *
     * Важно: в слачае с цифрами ячеек это самый оптимальный вариант кэширования,
     * но с обычными строками будут расхождения из-за разной ширины символов, отличных от цифр.
     */
    private val cachedSizes = hashMapOf<Int, Float>()

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        .apply { isFakeBoldText = true }
    private val textBounds = Rect()

    fun calculateTextSize(
        context: Context,
        value: String,
        @Px containerSize: Int
    ): Float {
        if (value == StringUtils.EMPTY || containerSize == 0) return 0f
        if (this.containerSize != containerSize) cachedSizes.clear()

        cachedSizes[value.length]?.let { cachedSize ->
            return cachedSize
        }

        val availableSpace = getAvailableSpace(containerSize, value)
        if (availableSpace < 0) return 0f

        val biggestTestSize = context.dp(availableSpace).toFloat()
        val smallestTextSize = 0f
        val sizeInfelicity = context.dp(TEXT_SIZE_INFELICITY_DP)

        updateTextBounds(value, biggestTestSize)

        fun binarySearchSize(minTextSize: Float, maxTextSize: Float): Float {
            if (minTextSize > maxTextSize) return minTextSize

            val mid = minTextSize + (maxTextSize - minTextSize) / 2f

            updateTextBounds(value, mid)
            val textWidth = textBounds.width()
            val textHeight = textBounds.height()

            return when {
                textWidth in (availableSpace - sizeInfelicity)..availableSpace
                        && textHeight <= availableSpace ->
                    mid
                textHeight in (availableSpace - sizeInfelicity)..availableSpace
                        && textWidth <= availableSpace ->
                    mid
                textWidth < availableSpace && textHeight < availableSpace ->
                    binarySearchSize(mid + 1, maxTextSize)
                else ->
                    binarySearchSize(minTextSize, mid - 1)
            }
        }

        return binarySearchSize(smallestTextSize, biggestTestSize).also { resultSize ->
            cachedSizes[value.length] = resultSize
        }
    }

    private fun updateTextBounds(value: String, textSize: Float) {
        textPaint.textSize = textSize
        textPaint.getTextBounds(value, 0, value.length, textBounds)
    }

    private fun getAvailableSpace(containerSize: Int, value: String): Int =
        when (value.length) {
            1 -> containerSize * 4 / 9f
            2 -> containerSize * 5 / 9f
            3 -> containerSize * 13 / 18f
            else -> containerSize * 15 / 18f
        }.roundToInt()
}

private const val TEXT_SIZE_INFELICITY_DP = 3