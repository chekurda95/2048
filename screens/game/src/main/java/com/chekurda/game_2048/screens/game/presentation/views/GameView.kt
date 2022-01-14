package com.chekurda.game_2048.screens.game.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.chekurda.common.half
import com.chekurda.design.custom_view_tools.utils.dp
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.field.GameFieldView

internal class GameView(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context) {

    private val backgroundRect = Rect()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.game_screen_background)
    }

    private val fieldView = GameFieldView(context)
    private val fieldPadding = context.dp(10)

    init {
        setWillNotDraw(false)
        addView(fieldView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val fieldSize = measuredWidth - fieldPadding * 2
        fieldView.measure(fieldSize, fieldSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        getDrawingRect(backgroundRect)

        val fieldTop = (measuredHeight - fieldView.measuredHeight).half
        val fieldLeft = (measuredWidth - fieldView.measuredWidth).half
        fieldView.layout(
            fieldLeft,
            fieldTop,
            fieldLeft + fieldView.measuredWidth,
            fieldTop + fieldView.measuredHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (background == null) {
            canvas.drawRect(backgroundRect, backgroundPaint)
        }
    }
}