package com.chekurda.game_2048.screens.game.presentation.views.cell

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.withSave
import com.chekurda.design.custom_view_tools.utils.dp
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.cell.params.CellParams
import com.chekurda.game_2048.screens.game.presentation.views.cell.params.createCellParams
import com.chekurda.game_2048.screens.game.presentation.views.cell.utils.AutoTextSizeHelper.calculateTextSize
import org.apache.commons.lang3.StringUtils.EMPTY
import java.lang.RuntimeException

class CellView2(context: Context, attrs: AttributeSet? = null) : View(context) {

    private val textPaint = TextPaint(ANTI_ALIAS_FLAG).apply {
        isFakeBoldText = true
    }
    private var textPos = Pair(0f, 0f)

    private val backgroundPaint = Paint(ANTI_ALIAS_FLAG)
    private val backgroundRect = RectF()

    private val borderPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = getColor(context, R.color.black_overlay)
        style = Paint.Style.STROKE
        strokeWidth = context.dp(CELL_BORDER_SIZE_DP).toFloat()
    }
    private val borderRadius = context.dp(CELL_BORDER_RADIUS_DP).toFloat()

    private var params = CellParams()
    private var isGrowingRunning = false
    private var isShowingRunning = false

    var value: Int = 0
        set(value) {
            if (field == value) return
            updateView(value)
            checkAnimations(field, value)
            field = value
        }

    init {
        setWillNotDraw(false)
        if (isInEditMode) value = 16
    }

    private fun updateView(value: Int) {
        params = createCellParams(context, value)

        textPaint.run {
            textSize = params.textSize.toFloat()
            color = params.textColor
        }
        backgroundPaint.color = params.backgroundColor

        if (isLaidOut) updateText()
    }

    private fun checkAnimations(previousValue: Int, newValue: Int) {
        if (previousValue != 0 && newValue > previousValue) {
            isGrowingRunning = true
        } else if (previousValue == 0 && newValue > previousValue) {
            isShowingRunning = true
        }
        invalidate()
    }

    private fun updateText() {
        if (params.value == EMPTY || measuredWidth == 0 || measuredHeight == 0) return
        textPaint.textSize = calculateTextSize(context, params.value, measuredWidth)

        val textBounds = Rect().also {
            textPaint.getTextBounds(params.value, 0, params.value.length, it)
        }
        // Рассчет координат для отрисовки текста по центру ячейки
        val left = measuredWidth / 2f - (textBounds.width() / 2) - textBounds.left
        val top = (measuredHeight / 2f) + (textBounds.height() / 2) - textBounds.bottom

        textPos = left to top
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec = if (isInEditMode) widthMeasureSpec else heightMeasureSpec
        super.onMeasure(widthMeasureSpec, heightSpec)
        if (measuredWidth != measuredHeight) {
            throw RuntimeException("Ячейка не квадратная: width = $measuredWidth, height = $measuredHeight")
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (changed) {
            updateText()
            backgroundRect.set(
                0f,
                0f,
                right - left.toFloat(),
                bottom - top.toFloat()
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
        drawValue(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.withSave {
            drawRoundRect(backgroundRect, borderRadius, borderRadius, backgroundPaint)
            drawRoundRect(backgroundRect, borderRadius, borderRadius, borderPaint)
        }
    }

    private fun drawValue(canvas: Canvas) {
        canvas.withSave {
            drawText(params.value, textPos.first, textPos.second, textPaint)
        }
    }

    private fun animateGrowing() = Unit

    private fun animateShowing() = Unit

    override fun hasOverlappingRendering(): Boolean = false
}

private const val CELL_BORDER_SIZE_DP = 1
private const val CELL_BORDER_RADIUS_DP = 6