package com.chekurda.game_2048.screens.game.presentation.views.field.layouts

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.RectF

/**
 * Общая реализация объектов игрового поля
 */
internal abstract class GameFieldObject(
    protected val context: Context
) {

    protected val resources: Resources = context.resources
    protected var rect = RectF()

    protected val width: Int
        get() = rect.width().toInt()

    protected val height: Int
        get() = rect.height().toInt()

    open fun setResolution(width: Int, height: Int) {
        rect.set(
            0f,
            0f,
            width.toFloat(),
            height.toFloat()
        )
    }

    abstract fun draw(canvas: Canvas)
}