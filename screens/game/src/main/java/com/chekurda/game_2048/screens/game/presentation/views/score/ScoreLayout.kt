package com.chekurda.game_2048.screens.game.presentation.views.score

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface.DEFAULT_BOLD
import androidx.core.content.ContextCompat.getColor
import com.chekurda.common.half
import com.chekurda.design.custom_view_tools.TextLayout
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.base.AbstractLayout

internal class ScoreLayout(context: Context) : AbstractLayout(context) {

    private val valueLayout = TextLayout {
        paint.apply {
            typeface = DEFAULT_BOLD
            textSize = resources.getDimensionPixelSize(R.dimen.score_value_text_size).toFloat()
            color = getColor(context, R.color.score_value_text_color)
        }
        includeFontPad = false
    }
    private val headerLayout = TextLayout {
        paint.apply {
            typeface = DEFAULT_BOLD
            textSize = resources.getDimensionPixelSize(R.dimen.score_header_text_size).toFloat()
            color = getColor(context, R.color.score_header_text_color)
        }
    }

    private val borderRadius = resources.getDimensionPixelSize(R.dimen.cell_corner_radius).toFloat()

    init {
        backgroundPaint.run {
            color = getColor(context, R.color.score_background_color)
        }
    }

    var value: Int = 0
        set(value) {
            field = value
            val isChanged = valueLayout.configure { text = value.toString() }
            if (isChanged) internalLayout()
        }

    var headerText: CharSequence
        get() = headerLayout.text
        set(value) {
            val isChanged = headerLayout.configure { text = value }
            if (isChanged) internalLayout()
        }

    override fun onSizeChanged(width: Int, height: Int) {
        super.onSizeChanged(width, height)
        internalLayout()
    }

    override fun internalLayout() {
        super.internalLayout()
        if (width == 0 || height == 0) return
        valueLayout.layout(
            position.x.toInt() + (width - valueLayout.width).half,
            position.y.toInt() + (height - valueLayout.height).half
        )

        headerLayout.layout(
            position.x.toInt() + (width - headerLayout.width).half,
            position.y.toInt() + (valueLayout.top - position.y - headerLayout.height).half.toInt()
        )
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(rect, borderRadius, borderRadius, backgroundPaint)
        headerLayout.draw(canvas)
        valueLayout.draw(canvas)
    }
}