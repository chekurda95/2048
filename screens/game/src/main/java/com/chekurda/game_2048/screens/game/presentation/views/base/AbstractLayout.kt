package com.chekurda.game_2048.screens.game.presentation.views.base

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.RectF
import androidx.annotation.CallSuper
import com.chekurda.design.custom_view_tools.utils.AntiPaint

/**
 * Абстрактаная реализация плоской разметки.
 */
internal abstract class AbstractLayout(
    protected val context: Context
) {

    protected val resources: Resources = context.resources
    protected val backgroundPaint = AntiPaint()
    protected var rect = RectF()
        private set

    var width: Int = 0
        private set

    var height: Int = 0
        private set

    val left: Float
        get() = rect.left

    val top: Float
        get() = rect.top

    val right: Float
        get() = rect.right

    val bottom: Float
        get() = rect.bottom

    var position = Position()
        set(value) {
            field = value
            updateRect()
        }

    fun setRect(rectF: RectF) {
        if (rectF == rect) return
        position = Position(rectF.left, rectF.top)
        setResolution(rectF.width().toInt(), rectF.height().toInt())
    }

    @CallSuper
    open fun setResolution(width: Int, height: Int) {
        val isChanged = this.width != width || this.height != height

        this.width = width
        this.height = height
        updateRect()

        if (isChanged) onSizeChanged(width, height)
    }

    protected open fun onSizeChanged(width: Int, height: Int) = Unit

    fun translate(x: Float = 0f, y: Float = 0f) {
        if (x == 0f && y == 0f) return
        position = Position(position.x + x, position.y + y)
    }

    open fun update(deltaTime: Int) = Unit

    abstract fun draw(canvas: Canvas)

    private fun updateRect() {
        with(position) {
            rect.set(
                x,
                y,
                x + width,
                y + width
            )
        }
    }

    data class Position(val x: Float = 0f, val y: Float = 0f)
}