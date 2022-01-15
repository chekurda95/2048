package com.chekurda.game_2048.screens.game.presentation.views.field.layouts

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.RectF
import com.chekurda.design.custom_view_tools.utils.AntiPaint
import com.chekurda.design.custom_view_tools.utils.update

/**
 * Общая реализация объектов игрового поля
 */
internal abstract class GameFieldObject(
    protected val context: Context
) {

    protected val resources: Resources = context.resources
    protected val backgroundPaint = AntiPaint()
    protected var rect = RectF()

    protected val width: Int
        get() = rect.width().toInt()

    protected val height: Int
        get() = rect.height().toInt()

    open fun setResolution(width: Int, height: Int) {
        rect.update(
            right = width.toFloat(),
            bottom = height.toFloat()
        )
    }

    abstract fun draw(canvas: Canvas)
}