package com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint.Style.STROKE
import android.graphics.Rect
import androidx.core.content.ContextCompat.getColor
import com.chekurda.common.half
import com.chekurda.design.custom_view_tools.utils.AntiPaint
import com.chekurda.design.custom_view_tools.utils.AntiTextPaint
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.GameFieldObject
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.AutoTextSizeHelper.calculateTextSize
import org.apache.commons.lang3.StringUtils
import java.lang.RuntimeException

internal class GameCell(context: Context) : GameFieldObject(context) {

    private val textPaint = AntiTextPaint().apply {
        isFakeBoldText = true
    }
    private var textPos = 0f to 0f

    private val borderPaint = AntiPaint().apply {
        color = getColor(context, R.color.cell_border_color)
        style = STROKE
        strokeWidth = resources.getDimensionPixelSize(R.dimen.cell_stroke_width).toFloat()
    }
    private val borderRadius = resources.getDimensionPixelSize(R.dimen.cell_corner_radius).toFloat()

    private var params = CellParams()
    private var isGrowingRunning = false
    private var isShowingRunning = false

    var value: Int = 0
        set(value) {
            if (field == value) return
            updateCell(value)
            checkAnimations(field, value)
            field = value
        }

    private fun updateCell(value: Int) {
        params = createCellParams(context, value)

        textPaint.run {
            textSize = params.textSize.toFloat()
            color = params.textColor
        }
        backgroundPaint.color = params.backgroundColor

        updateText()
    }

    private fun checkAnimations(previousValue: Int, newValue: Int) {
        if (previousValue != 0 && newValue > previousValue) {
            isGrowingRunning = true
        } else if (previousValue == 0 && newValue > previousValue) {
            isShowingRunning = true
        }
    }

    private fun updateText() {
        if (params.value == StringUtils.EMPTY || width == 0 || height == 0) return
        textPaint.textSize = calculateTextSize(context, params.value, width)

        val textBounds = Rect().also {
            textPaint.getTextBounds(params.value, 0, params.value.length, it)
        }
        // Рассчет координат для отрисовки текста по центру ячейки
        val left = width.half - textBounds.width().half - textBounds.left
        val top = height.half + textBounds.height().half - textBounds.bottom

        textPos = position.x + left to position.y + top
    }

    override fun setResolution(width: Int, height: Int) {
        super.setResolution(width, height)
        if (width != height) {
            throw RuntimeException("Ячейка не квадратная: width = $width, height = $height")
        }
    }

    override fun onSizeChanged(width: Int, height: Int) {
        updateText()
    }

    override fun draw(canvas: Canvas) {
        if (value == 0 || width == 0 || height == 0) return
        drawBackground(canvas)
        drawValue(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.run {
            drawRoundRect(rect, borderRadius, borderRadius, backgroundPaint)
            drawRoundRect(rect, borderRadius, borderRadius, borderPaint)
        }
    }

    private fun drawValue(canvas: Canvas) {
        canvas.drawText(params.value, textPos.first, textPos.second, textPaint)
    }
}