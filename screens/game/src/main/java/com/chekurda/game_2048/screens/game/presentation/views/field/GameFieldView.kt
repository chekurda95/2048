package com.chekurda.game_2048.screens.game.presentation.views.field

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat.getColor
import com.chekurda.common.surface.SurfaceDrawingThread
import com.chekurda.common.surface.SurfaceLayout
import com.chekurda.common.surface.SurfaceLayout.DrawingLayout
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.GAME_FIELD_ROW_SIZE
import java.lang.RuntimeException
import kotlin.math.roundToInt

internal class GameFieldView(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context), DrawingLayout {

    private val backgroundRect = RectF()
    private val backgroundColors = arrayOf(
        getColor(context, R.color.game_screen_background),
        getColor(context, R.color.game_field_background)
    )
    private val backgroundPaint = Paint(ANTI_ALIAS_FLAG)
    private val backgroundCornerRadius = resources.getDimensionPixelOffset(R.dimen.field_corner_radius).toFloat()

    private val cellSeparatorWidth = resources.getDimensionPixelOffset(R.dimen.field_cell_separator_width)
    private val cellsPositions = HashMap<Int, RectF>()
    private val cellBackgroundPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = getColor(context, R.color.empty_cell_background_color)
    }
    private val cellCornerRadius = resources.getDimensionPixelOffset(R.dimen.cell_corner_radius).toFloat()

    init {
        holder.addCallback(SurfaceCallback())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = if (!isInEditMode) MeasureSpec.getSize(heightMeasureSpec) else width
        setMeasuredDimension(width, height)

        if (measuredWidth != measuredHeight) {
            throw RuntimeException("Поле не квадратное: width = $measuredWidth, height = $measuredHeight")
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        layoutCells()
    }

    private fun calculateCellSize(): Int {
        val separatorsSumWidth = cellSeparatorWidth * (GAME_FIELD_ROW_SIZE + 1)
        val cellSize = (measuredWidth - separatorsSumWidth) / GAME_FIELD_ROW_SIZE.toFloat()
        return maxOf(cellSize, 0f).roundToInt()
    }

    private fun layoutCells() {
        cellsPositions.clear()
        val cellSize = calculateCellSize()
        var previousCellRect = RectF(
            0f,
            cellSeparatorWidth.toFloat(),
            0f,
            cellSeparatorWidth + cellSize.toFloat()
        )
        for (row in 0 until GAME_FIELD_ROW_SIZE) {
            for (column in 0 until GAME_FIELD_ROW_SIZE) {
                val left = previousCellRect.right + cellSeparatorWidth
                val cellRect = RectF(
                    left,
                    previousCellRect.top,
                    left + cellSize,
                    previousCellRect.bottom
                )
                cellsPositions[column + row * GAME_FIELD_ROW_SIZE] = cellRect
                previousCellRect = RectF(cellRect)
            }

            val firstRectInRow = cellsPositions[row * GAME_FIELD_ROW_SIZE]!!
            val newPreviousTop = firstRectInRow.bottom + cellSeparatorWidth
            previousCellRect.set(0f, newPreviousTop, 0f, newPreviousTop + cellSize)
        }
    }

    private fun setResolution(width: Int, height: Int) {
        backgroundRect.set(
            0f,
            0f,
            width.toFloat(),
            height.toFloat()
        )
    }

    override fun update(deltaTimeMs: Int) = Unit

    override fun drawLayout(canvas: Canvas) {
        drawBackground(canvas)
        drawEmptyCells(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(
            backgroundRect,
            backgroundPaint.apply { color = backgroundColors[0] }
        )
        canvas.drawRoundRect(
            backgroundRect,
            backgroundCornerRadius,
            backgroundCornerRadius,
            backgroundPaint.apply { color = backgroundColors[1] }
        )
    }

    private fun drawEmptyCells(canvas: Canvas) {
        cellsPositions.forEach {
            canvas.drawRoundRect(it.value, cellCornerRadius, cellCornerRadius, cellBackgroundPaint)
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {

        private lateinit var thread: Thread

        override fun surfaceCreated(holder: SurfaceHolder) {
            thread = SurfaceDrawingThread(
                surfaceLayout = SurfaceLayout(this@GameFieldView, holder),
                fps = GameConfig.GAME_FPS
            ).apply { start() }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            setResolution(width, height)
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            thread.interrupt()
        }
    }
}