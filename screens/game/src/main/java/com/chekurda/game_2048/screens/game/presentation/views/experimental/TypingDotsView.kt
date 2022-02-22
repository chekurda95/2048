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

/**
 * View для отображения анимируемых точек.
 * @see DotsParams
 *
 * @author vv.chekurda
 */
internal class TypingDotsView(context: Context) : View(context) {

    constructor(context: Context, attrs: AttributeSet? = null) : this(context)

    /**
     * Параметры анимируемых точек.
     *
     * @property durationMs общее время продолжительности анимации всех точек.
     * @property count количество точек.
     * @property size размер точек в px.
     * @property spacing отстыпы между точками в px.
     * @property color цвет точек.
     */
    data class DotsParams(
        val durationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
        val count: Int = DEFAULT_DOTS_COUNT,
        @Px val size: Int = DEFAULT_DOTS_SIZE_PX,
        @Px val spacing: Int = size,
        @ColorInt val color: Int = Color.GRAY
    ) {
        val oneStepDurationMs: Int by lazy { durationMs / (count + 1) }
        val dotRadius: Float by lazy { size / 2f }
    }

    /**
     * Установить/получить параметры с настройками анимируемых точек.
     * @see DotsParams
     */
    var params = DotsParams()
        set(value) {
            val isChanged = field != value
            field = value

            if (isChanged) {
                clearSteps()
                safeRequestLayout()
            }
        }

    /**
     * Основная краска, которой рисуются отображаемые точки.
     */
    private val paint = Paint().apply {
        isAntiAlias = true
        color = params.color
    }

    /**
     * Вспомогательная краска для отрисовки появления или исчезновения точек.
     */
    private val fadePaint = Paint().apply {
        isAntiAlias = true
        color = params.color
    }

    /**
     * Время обновления шага анимации в мс.
     */
    private var stepUpdateTimeMs = 0L

    /**
     * Номер самого последнего шага анимации.
     */
    private val lastStep: Int
        get() = params.count

    /**
     * Текущий шаг анимации.
     */
    private var step = 0
        set(value) {
            field = value % (lastStep + 1)
        }

    /**
     * Центр первой точки, относительно которого происходит выравнивание всех точек.
     */
    private var firstDotCenter = 0f to 0f

    /**
     * Сбросить все шаги анимации к исходному состоянию.
     */
    private fun clearSteps() {
        step = 0
        stepUpdateTimeMs = System.currentTimeMillis()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == VISIBLE) clearSteps()
    }

    override fun getSuggestedMinimumWidth(): Int =
        paddingStart + with(params) { size * count + spacing * (count - 1) } + paddingEnd

    override fun getSuggestedMinimumHeight(): Int =
        paddingTop + params.size + paddingBottom

    override fun getBaseline(): Int =
        (firstDotCenter.second + params.dotRadius).toInt()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        firstDotCenter = paddingStart.toFloat() + params.dotRadius to measuredHeight - paddingBottom - params.dotRadius
    }

    override fun onDraw(canvas: Canvas) {
        if (!isVisible) return

        val currentTime = System.currentTimeMillis()
        val interpolation = minOf((currentTime - stepUpdateTimeMs) / params.oneStepDurationMs.toFloat(), 1f)
        val interpolationForStep = if (step != lastStep) interpolation else 1f - interpolation
        fadePaint.alpha = (interpolationForStep * MAX_ALPHA).toInt()

        repeat(params.count) { dotIndex ->
            val dotPaint = when {
                // Появление точки или исчезновение всех точек
                dotIndex == step || step == lastStep -> fadePaint
                // Точка просто отображается без анимаций
                dotIndex <= step -> paint
                // Для остальных очередь не дошла - не рисуем
                else -> return@repeat
            }
            val dotHorizontalCenter = firstDotCenter.first + params.size * dotIndex + params.spacing * dotIndex
            canvas.drawCircle(dotHorizontalCenter, firstDotCenter.second, params.dotRadius, dotPaint)
        }

        if (currentTime - stepUpdateTimeMs >= params.oneStepDurationMs) {
            step++
            stepUpdateTimeMs = currentTime
        }
        invalidate()
    }

    override fun hasOverlappingRendering(): Boolean = false
}

/**
 * Максимальная alpha у краски.
 */
private const val MAX_ALPHA = 255

/**
 * Стандартное количество анимируемых точек.
 */
private const val DEFAULT_DOTS_COUNT = 3

/**
 * Стандартная продолжительность анимации всех точек.
 */
private const val DEFAULT_ANIMATION_DURATION_MS = 1000

/**
 * Стандартный размер точек в px.
 */
private const val DEFAULT_DOTS_SIZE_PX = 50