package com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint.Style.STROKE
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat.getColor
import com.chekurda.common.half
import com.chekurda.design.custom_view_tools.utils.AntiPaint
import com.chekurda.design.custom_view_tools.utils.AntiTextPaint
import com.chekurda.design.custom_view_tools.utils.copy
import com.chekurda.design.custom_view_tools.utils.scale
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.CELL_SHOWING_DURATION_MS
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

    private val paints = listOf(textPaint, borderPaint, backgroundPaint)

    @IntRange(from = 0, to = MAX_ALPHA.toLong())
    private var alpha: Int = 0
        set(value) {
            field = value
            paints.forEach {
                it.alpha = value
            }
        }

    private var isShowingAnimation = false
    private var isShowingRunning = false
    private var showingAnimationTime = 0
    private var realTextSize = 0f
    private var realRect = RectF()
    private var showingStartRect = realRect
    private var animationInterpolator = LinearInterpolator()

    private var params = CellParams()

    var value: Int = 0
        set(value) {
            if (field == value) return
            field = value

            updateCell(value)
        }

    var isVisible: Boolean = false
        set(value) {
            field = value
            alpha = if (value) MAX_ALPHA else 0
        }

    init {
        isVisible = false
    }

    fun animateShowing() {
        isShowingAnimation = true
        showingAnimationTime = 0
        realTextSize = textPaint.textSize
        realRect = rect.copy()
        showingStartRect = realRect.copy().scale(0.5f)
    }

    override fun update(deltaTime: Int) {
        if (isShowingAnimation) {
            Log.e("TAGTAG", "delta $deltaTime")
            showingAnimationTime += if (isShowingRunning) deltaTime else 0
            val progress = minOf(showingAnimationTime.toFloat() / CELL_SHOWING_DURATION_MS, 1f)
            val interpolation = animationInterpolator.getInterpolation(progress)

            alpha = getInterpolatedValue(0.7f * MAX_ALPHA, MAX_ALPHA.toFloat(), interpolation).toInt()
            val rect = RectF(
                getInterpolatedValue(showingStartRect.left, realRect.left, interpolation),
                getInterpolatedValue(showingStartRect.top, realRect.top, interpolation),
                getInterpolatedValue(showingStartRect.right, realRect.right, interpolation),
                getInterpolatedValue(showingStartRect.bottom, realRect.bottom, interpolation)
            )
            Log.e("TAGTAG", "$rect")
            setRect(rect)

            isShowingRunning = true

            if (progress == 1f) {
                isShowingAnimation = false
                isShowingRunning = false
            }
        }
    }

    private fun updateCell(value: Int) {
        params = createCellParams(context, value)

        textPaint.run {
            textSize = params.textSize.toFloat()
            color = params.textColor
        }
        backgroundPaint.color = params.backgroundColor
        alpha = alpha

        updateText()
    }

    private fun updateText() {
        if (params.value == StringUtils.EMPTY || width == 0 || height == 0) return

        textPaint.textSize = calculateTextSize(context, params.value, width)
        updateTextPosition()
    }

    private fun updateTextPosition() {
        val textBounds = Rect().also {
            textPaint.getTextBounds(params.value, 0, params.value.length, it)
        }
        // Рассчет координат для отрисовки текста по центру ячейки
        val left = width.half - textBounds.width().half - textBounds.left
        val top = height.half + textBounds.height().half - textBounds.bottom

        textPos = left.toFloat() to top.toFloat()
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
        canvas.drawText(
            params.value,
            position.x + textPos.first,
            position.y + textPos.second,
            textPaint
        )
    }

    private fun getInterpolatedValue(fromValue: Float, toValue: Float, interpolation: Float) =
        fromValue + (toValue - fromValue) * interpolation
}

private const val MAX_ALPHA = 255