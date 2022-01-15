package com.chekurda.game_2048.screens.game.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.chekurda.common.half
import com.chekurda.design.custom_view_tools.utils.MeasureSpecUtils
import com.chekurda.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import com.chekurda.design.custom_view_tools.utils.dp
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.domain.GameController
import com.chekurda.game_2048.screens.game.domain.GameController.GameControllerConnector
import com.chekurda.game_2048.screens.game.presentation.views.field.GameFieldView

internal class GameView(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context), GameControllerConnector {

    private val backgroundRect = Rect()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.game_screen_background)
    }

    private val fieldView = GameFieldView(context)
    private val fieldPadding = context.dp(10)

    private val startNewGameButton = Button(context).apply {
        text = "Start new game"
        setTextColor(Color.BLACK)
        setBackgroundColor(Color.YELLOW)

        setPadding(dp(10))
    }
    private val startNewGamePos = dp(20) to dp(20)

    private lateinit var gameController: GameController

    init {
        setWillNotDraw(false)
        addView(fieldView)
        addView(startNewGameButton)

        startNewGameButton.setOnClickListener {
            gameController.startNewGame()
        }
    }

    override fun attachGameController(controller: GameController) {
        gameController = controller
        fieldView.attachGameController(controller)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val fieldSize = measuredWidth - fieldPadding * 2
        fieldView.measure(fieldSize, fieldSize)
        startNewGameButton.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
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

        startNewGameButton.layout(
            startNewGamePos.first,
            startNewGamePos.second,
            startNewGamePos.first + startNewGameButton.measuredWidth,
            startNewGamePos.second + startNewGameButton.measuredHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (background == null) {
            canvas.drawRect(backgroundRect, backgroundPaint)
        }
    }
}