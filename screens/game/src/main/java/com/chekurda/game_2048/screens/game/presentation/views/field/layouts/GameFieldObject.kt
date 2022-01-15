package com.chekurda.game_2048.screens.game.presentation.views.field.layouts

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.RectF
import androidx.annotation.CallSuper
import com.chekurda.design.custom_view_tools.utils.AntiPaint

/**
 * Общая реализация объектов игрового поля
 */
internal abstract class GameFieldObject(
    protected val context: Context
) {

    protected val resources: Resources = context.resources
    protected val backgroundPaint = AntiPaint()
    protected var rect = RectF()

    protected var width: Int = 0
        private set

    protected var height: Int = 0
        private set

    var position = Position()
        set(value) {
            field = value
            updateRect()
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