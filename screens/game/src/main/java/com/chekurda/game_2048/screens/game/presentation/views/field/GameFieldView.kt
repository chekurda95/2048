package com.chekurda.game_2048.screens.game.presentation.views.field

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.FieldMainLayout
import java.lang.RuntimeException

internal class GameFieldView(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context) {

    private val canvasLayout = FieldMainLayout(context, holder)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = if (!isInEditMode) MeasureSpec.getSize(heightMeasureSpec) else width
        setMeasuredDimension(width, height)

        if (measuredWidth != measuredHeight) {
            throw RuntimeException("Поле не квадратное: width = $measuredWidth, height = $measuredHeight")
        }
    }
}