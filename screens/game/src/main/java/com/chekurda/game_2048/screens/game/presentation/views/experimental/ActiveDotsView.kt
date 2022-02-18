package com.chekurda.game_2048.screens.game.presentation.views.experimental

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.view.isVisible
import com.chekurda.design.custom_view_tools.utils.safeRequestLayout

class ActiveDotsView(context: Context) : View(context) {

    constructor(context: Context, attrs: AttributeSet? = null) : this(context)

    data class Params(
        val durationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
        val dotsCount: Int = DEFAULT_DOTS_COUNT,
        @Px val dotSize: Int = DEFAULT_DOTS_SIZE_PX,
        @Px val dotSpacing: Int = dotSize,
        @ColorInt val color: Int = Color.GRAY
    ) {
        val oneStepDurationMs: Int by lazy { durationMs / (dotsCount + 1) }
        val dotRadius: Float by lazy { dotSize / 2f }
    }

    var params = Params()
        set(value) {
            val isChanged = field != value
            field = value

            updateStepsState()
            if (isChanged) safeRequestLayout()
        }

    private val paint = Paint().apply {
        isAntiAlias = true
        color = params.color
    }

    private val fadePaint = Paint().apply {
        isAntiAlias = true
        color = params.color
    }

    private var stepUpdateTime = 0L
    private val lastStep: Int
        get() = params.dotsCount
    private var step = 0
        set(value) {
            field = value % (lastStep + 1)
        }

    private var firstDotCenter = 0f to 0f

    private fun updateStepsState() {
        step = 0
        stepUpdateTime = System.currentTimeMillis()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == VISIBLE) updateStepsState()
    }

    override fun getSuggestedMinimumWidth(): Int =
        paddingStart + with(params) { dotSize * dotsCount + dotSpacing * (dotsCount - 1) } + paddingEnd

    override fun getSuggestedMinimumHeight(): Int =
        paddingTop + params.dotSize + paddingBottom

    override fun getBaseline(): Int =
        firstDotCenter.second.toInt()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        firstDotCenter = paddingStart.toFloat() + params.dotRadius to measuredHeight - paddingBottom - params.dotRadius
    }

    override fun onDraw(canvas: Canvas) {
        if (!isVisible) return

        val currentTime = System.currentTimeMillis()
        val interpolation = minOf((currentTime - stepUpdateTime) / params.oneStepDurationMs.toFloat(), 1f)
        val interpolationForStep = if (step != lastStep) interpolation else 1f - interpolation
        fadePaint.alpha = (interpolationForStep * MAX_ALPHA).toInt()

        repeat(params.dotsCount) { dotIndex ->
            val dotPaint = when {
                // Появление точки или исчезновение всех точек
                dotIndex == step || step == lastStep -> fadePaint
                // Точка просто отображается без анимаций
                dotIndex <= step -> paint
                // Для остальных очередь не дошла - не рисуем
                else -> return@repeat
            }
            val dotHorizontalCenter = firstDotCenter.first + params.dotSize * dotIndex + params.dotSpacing * dotIndex
            canvas.drawCircle(dotHorizontalCenter, firstDotCenter.second, params.dotRadius, dotPaint)
        }

        if (currentTime - stepUpdateTime >= params.oneStepDurationMs) {
            step++
            stepUpdateTime = currentTime
        }
        invalidate()
    }

    override fun hasOverlappingRendering(): Boolean = false
}

private const val MAX_ALPHA = 255
private const val DEFAULT_DOTS_COUNT = 3
private const val DEFAULT_ANIMATION_DURATION_MS = 1000
private const val DEFAULT_DOTS_SIZE_PX = 50