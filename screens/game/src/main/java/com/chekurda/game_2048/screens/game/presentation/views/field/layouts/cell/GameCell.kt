package com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint.Style.STROKE
import android.graphics.Rect
import android.graphics.RectF
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat.getColor
import com.chekurda.common.half
import com.chekurda.design.custom_view_tools.utils.AntiPaint
import com.chekurda.design.custom_view_tools.utils.AntiTextPaint
import com.chekurda.design.custom_view_tools.utils.copy
import com.chekurda.design.custom_view_tools.utils.scale
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.cellShowingDuration
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.GameFieldObject
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.AutoTextSizeHelper.calculateTextSize
import org.apache.commons.lang3.StringUtils
import java.lang.RuntimeException

internal class GameCell(context: Context) : GameFieldObject(context) {

    private var params = CellParams()

    private val textPaint = AntiTextPaint().apply {
        isFakeBoldText = true
    }
    private var textPos = 0f to 0f

    private val borderRadius = resources.getDimensionPixelSize(R.dimen.cell_corner_radius).toFloat()

    private val paints = listOf(textPaint, backgroundPaint)

    @IntRange(from = 0, to = MAX_ALPHA.toLong())
    private var alpha: Int = 0
        set(value) {
            field = value
            paints.forEach {
                it.alpha = value
            }
        }

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

    private val showingAnimation = ShowingAnimation(cellShowingDuration)

    init {
        isVisible = false
    }

    fun animateShowing() {
        showingAnimation.start()
    }

    override fun update(deltaTime: Int) {
        if (showingAnimation.isRunning) {
            showingAnimation.update(deltaTime)
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
        val left = rect.width().half - textBounds.width().half - textBounds.left
        val top = rect.height().half + textBounds.height().half - textBounds.bottom

        textPos = left to top
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
        canvas.drawRoundRect(rect, borderRadius, borderRadius, backgroundPaint)
    }

    private fun drawValue(canvas: Canvas) {
        canvas.drawText(
            params.value,
            rect.left + textPos.first,
            rect.top + textPos.second,
            textPaint
        )
    }

    private inner class ShowingAnimation(
        private val duration: Int,
        private val interpolator: Interpolator = LinearInterpolator(),
        private val scale: Float = 0.5f
    ) {

        private var animationTime = 0
        private var isStarted = false

        private var textSize = 0f
        private var endRect = RectF()
        private var startRect = rect

        var isRunning: Boolean = false
            private set

        private fun init() {
            animationTime = 0
            textSize = textPaint.textSize
            endRect = this@GameCell.rect.copy()
            startRect = endRect.copy().scale(scale)
        }

        fun start() {
            isRunning = true
            init()
        }

        fun update(deltaTime: Int) {
            animationTime += if (isStarted) deltaTime else 0
            val progress = minOf(animationTime.toFloat() / duration, 1f)
            val interpolation = interpolator.getInterpolation(progress)

            textPaint.textSize = getInterpolatedValue(textSize * scale, textSize, interpolation)
            rect.set(
                RectF(
                    getInterpolatedValue(startRect.left, endRect.left, interpolation),
                    getInterpolatedValue(startRect.top, endRect.top, interpolation),
                    getInterpolatedValue(startRect.right, endRect.right, interpolation),
                    getInterpolatedValue(startRect.bottom, endRect.bottom, interpolation)
                )
            )
            updateTextPosition()
            if (!isStarted) isVisible = true

            if (progress == 1f) {
                isRunning = false
                isStarted = false
                updateText()
            } else if (progress == 0f) {
                isStarted = true
            }
        }

        fun cancel() {
            if (!isRunning) return
            update(duration)
        }

        private fun getInterpolatedValue(fromValue: Float, toValue: Float, interpolation: Float) =
            fromValue + (toValue - fromValue) * interpolation
    }
}

private const val MAX_ALPHA = 255