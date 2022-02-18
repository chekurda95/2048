package com.chekurda.game_2048.screens.game.presentation.views.experimental

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import com.chekurda.design.custom_view_tools.utils.sp

class ActiveEllipsisView(context: Context) : View(context) {

    constructor(context: Context, attrs: AttributeSet? = null) : this(context)

    private val dotsCount = 3
    private val dotsColor = Color.BLACK
    private val duration = DURATION
    private val dotRadius = sp(10).toFloat()
    private val dotSpacing = dotRadius * 2

    private var lastUpdateTime = 0L
    private var isRunning = false

    private val lastStep = dotsCount
    private var step = 0
        set(value) {
            field = value % (lastStep + 1)
        }

    private val dotPaint = TextPaint().apply {
        isAntiAlias = true
        color = dotsColor
    }

    private val fadePaint = TextPaint().apply {
        isAntiAlias = true
        color = dotsColor
    }

    init {
        if (isVisible) isRunning = true
        minimumHeight = dotRadius.toInt() * 2
        minimumWidth = (dotsCount * dotRadius * 2 + dotSpacing * (dotsCount - 1)).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isInEditMode) {
            setMeasuredDimension(minimumWidth, minimumHeight)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == VISIBLE) {
            isRunning = true
            lastUpdateTime = System.currentTimeMillis()
            step = 0
        } else {
            isRunning = false
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isVisible || !isRunning) return

        val time = System.currentTimeMillis()
        if (time - lastUpdateTime >= STEP_START_DELAY) {
            val interpolation = minOf((time - lastUpdateTime - STEP_START_DELAY) / FADE_DURATION, 1f)
            val resultInterpolation = if (step == lastStep) {
                1f - interpolation
            } else interpolation
            fadePaint.alpha = (resultInterpolation * 255).toInt()
        } else if (step != lastStep) {
            fadePaint.alpha = 0
        }

        repeat(dotsCount) { dot ->
            val dotCenter = dotRadius * (1 + dot * 2) + dotSpacing * dot
            val paint = if (step == lastStep || dot == step) {
                fadePaint
            } else {
                dotPaint.apply {
                    alpha = if (dot > step) 0 else 255
                }
            }
            canvas.drawCircle(dotCenter, dotRadius, dotRadius, paint)
        }

        if (time - lastUpdateTime >= duration) {
            step++
            lastUpdateTime = time
        }
        invalidate()
    }

    override fun hasOverlappingRendering(): Boolean = false
}

private const val DURATION = 250f
private const val STEP_START_DELAY = 0f
private const val FADE_DURATION = DURATION - STEP_START_DELAY