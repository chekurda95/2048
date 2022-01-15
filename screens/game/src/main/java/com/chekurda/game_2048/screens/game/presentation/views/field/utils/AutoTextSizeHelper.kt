package com.chekurda.game_2048.screens.game.presentation.views.field.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import androidx.annotation.Px
import com.chekurda.design.custom_view_tools.utils.dp
import org.apache.commons.lang3.StringUtils

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
        @Px containerSize: Int,
        textPadding: Int = context.dp(DEFAULT_TEXT_PADDING_DP)
    ): Float {
        if (value == StringUtils.EMPTY || containerSize == 0) return 0f
        if (this.containerSize != containerSize) cachedSizes.clear()

        cachedSizes[value.length]?.let { cachedSize ->
            return cachedSize
        }

        val availableSpace = containerSize - textPadding * 2
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
}

private const val DEFAULT_TEXT_PADDING_DP = 15
private const val TEXT_SIZE_INFELICITY_DP = 3