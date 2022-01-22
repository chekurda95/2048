package com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface.DEFAULT_BOLD
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.chekurda.common.half
import com.chekurda.design.custom_view_tools.utils.AntiTextPaint
import com.chekurda.design.custom_view_tools.utils.copy
import com.chekurda.design.custom_view_tools.utils.scale
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.cellShowingDuration
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.cellSumDuration
import com.chekurda.game_2048.screens.game.presentation.views.base.AbstractLayout
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.AutoTextSizeHelper.calculateTextSize
import org.apache.commons.lang3.StringUtils
import java.lang.RuntimeException

internal class GameCell(context: Context) : AbstractLayout(context) {

    private var params = CellParams()

    private val textPaint = AntiTextPaint().apply {
        typeface = DEFAULT_BOLD
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
    private val sumAnimation = SumAnimation(cellSumDuration)

    init {
        isVisible = false
    }

    fun animateShowing() {
        showingAnimation.start()
    }

    fun animateSum() {
        value *= 2
        sumAnimation.start()
    }

    override fun update(deltaTime: Int) {
        when {
            showingAnimation.isRunning -> showingAnimation.update(deltaTime)
            sumAnimation.isRunning -> sumAnimation.update(deltaTime)
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
            val timeProgress = minOf(animationTime.toFloat() / duration, 1f)
            val interpolation = interpolator.getInterpolation(timeProgress)

            setAnimationProgress(interpolation)

            if (!isStarted) isVisible = true

            if (timeProgress == 1f) {
                cancel()
            } else if (timeProgress == 0f) {
                isStarted = true
            }
        }

        fun cancel() {
            if (!isRunning) return
            isRunning = false
            isStarted = false
            setAnimationProgress(1f)
        }

        private fun setAnimationProgress(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
            textPaint.textSize = getInterpolatedValue(textSize * scale, textSize, progress)
            rect.set(
                RectF(
                    getInterpolatedValue(startRect.left, endRect.left, progress),
                    getInterpolatedValue(startRect.top, endRect.top, progress),
                    getInterpolatedValue(startRect.right, endRect.right, progress),
                    getInterpolatedValue(startRect.bottom, endRect.bottom, progress)
                )
            )
            updateTextPosition()
        }

        private fun getInterpolatedValue(fromValue: Float, toValue: Float, interpolation: Float) =
            fromValue + (toValue - fromValue) * interpolation
    }

    private inner class SumAnimation(
        private val duration: Int,
        private val interpolator: Interpolator = DecelerateInterpolator(1.2f),
        private val scale: Float = 1.2f
    ) {

        private var animationTime = 0

        private var textSize = 0f
        private var endRect = RectF()
        private var startRect = rect

        var isRunning: Boolean = false
            private set

        private fun init() {
            animationTime = 0
            textSize = textPaint.textSize
            startRect = this@GameCell.rect.copy()
            endRect = startRect.copy().scale(scale)
        }

        fun start() {
            isRunning = true
            init()
        }

        fun update(deltaTime: Int) {
            animationTime += deltaTime
            val timeProgress = animationTime.toFloat() / duration
            val animationProgress = minOf(timeProgress, 1f)
            val interpolation = interpolator.getInterpolation(animationProgress)

            setAnimationProgress(interpolation)

            if (timeProgress > 1) cancel()
        }

        fun cancel() {
            if (!isRunning) return
            isRunning = false
            setAnimationProgress(0f)
            alpha = MAX_ALPHA
        }

        private fun setAnimationProgress(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
            textPaint.textSize = getInterpolatedValue(textSize, textSize * scale, progress)
            rect.set(
                RectF(
                    getInterpolatedValue(startRect.left, endRect.left, progress),
                    getInterpolatedValue(startRect.top, endRect.top, progress),
                    getInterpolatedValue(startRect.right, endRect.right, progress),
                    getInterpolatedValue(startRect.bottom, endRect.bottom, progress)
                )
            )
            alpha = getInterpolatedValue(0.7f * MAX_ALPHA, MAX_ALPHA.toFloat(), progress).toInt()
            updateTextPosition()
        }

        private fun getInterpolatedValue(fromValue: Float, toValue: Float, interpolation: Float) =
            fromValue + (toValue - fromValue) * interpolation
    }
}

private const val MAX_ALPHA = 255