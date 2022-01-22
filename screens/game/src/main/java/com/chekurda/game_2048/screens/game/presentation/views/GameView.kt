package com.chekurda.game_2048.screens.game.presentation.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.updatePadding
import com.chekurda.common.half
import com.chekurda.design.custom_view_tools.utils.MeasureSpecUtils
import com.chekurda.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import com.chekurda.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import com.chekurda.design.custom_view_tools.utils.dp
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.domain.GameController
import com.chekurda.game_2048.screens.game.domain.GameController.GameControllerConnector
import com.chekurda.game_2048.screens.game.presentation.views.field.GameFieldView
import com.chekurda.game_2048.screens.game.presentation.views.base.AbstractLayout.Position
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell.GameCell
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeHelper
import com.chekurda.game_2048.screens.game.presentation.views.score.ScoreLayout

internal class GameView(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context),
    GameControllerConnector {

    private val screenPadding = context.dp(10)

    private val backgroundRect = Rect()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(context, R.color.game_screen_background)
    }

    private val fieldView = GameFieldView(context)
    private val swipeHelper = SwipeHelper(context, fieldView)

    private val designCell = GameCell(context).apply {
        value = 2048
        isVisible = true
    }

    private val scoreLayout = ScoreLayout(context).apply {
        headerText = "SCORE"
        value = 4156
    }
    private val bestScoreLayout = ScoreLayout(context).apply {
        headerText = "BEST"
        value = 50276
    }
    private val scorePadding = context.dp(10)

    private val menuButton = Button(context).apply {
        text = "MENU"
        textSize = resources.getDimensionPixelSize(R.dimen.button_text_size).toFloat()
        setTextColor(getColor(context, R.color.game_button_text_color))
        background = ContextCompat.getDrawable(context, R.drawable.game_button)
        setPadding(0, 0, 0, 0)
    }
    private val newGameButton = Button(context).apply {
        text = "NEW GAME"
        textSize = resources.getDimensionPixelSize(R.dimen.button_text_size).toFloat()
        setTextColor(getColor(context, R.color.game_button_text_color))
        background = ContextCompat.getDrawable(context, R.drawable.game_button)
        setPadding(0, 0, 0, 0)
    }
    private val buttonHeight = resources.getDimensionPixelSize(R.dimen.button_height)

    private lateinit var gameController: GameController

    init {
        setWillNotDraw(false)
        addView(fieldView)
        addView(menuButton)
        addView(newGameButton)

        setOnTouchListener(swipeHelper)
        newGameButton.setOnClickListener {
            gameController.startNewGame()
        }
    }

    override fun attachGameController(controller: GameController) {
        gameController = controller
        fieldView.attachGameController(controller)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val fieldSize = measuredWidth - screenPadding * 2
        fieldView.measure(fieldSize, fieldSize)

        val designCellSize = minOf(dp(100), (measuredHeight - fieldView.measuredHeight).half)
        designCell.setResolution(designCellSize, designCellSize)

        val scoreSize = designCellSize * 5 / 6
        scoreLayout.setResolution(scoreSize, scoreSize)
        bestScoreLayout.setResolution(scoreSize, scoreSize)

        menuButton.measure(makeExactlySpec(scoreSize), makeExactlySpec(buttonHeight))
        newGameButton.measure(makeExactlySpec(scoreSize), makeExactlySpec(buttonHeight))
    }


    @SuppressLint("DrawAllocation")
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

        val designCellTop = (fieldTop - designCell.height) / 3f
        designCell.position = Position(fieldLeft.toFloat(), designCellTop)

        bestScoreLayout.position = Position(
            measuredWidth - screenPadding - bestScoreLayout.width.toFloat(),
            designCellTop
        )
        scoreLayout.position = Position(
            bestScoreLayout.left - scorePadding - scoreLayout.width,
            designCellTop
        )

        val buttonsTop = scoreLayout.bottom.toInt() + scorePadding
        menuButton.layout(
            scoreLayout.left.toInt(),
            buttonsTop,
            scoreLayout.left.toInt() + menuButton.measuredWidth,
            buttonsTop + menuButton.measuredHeight
        )

        newGameButton.layout(
            bestScoreLayout.left.toInt(),
            buttonsTop,
            bestScoreLayout.left.toInt() + newGameButton.measuredWidth,
            buttonsTop + newGameButton.measuredHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (background == null) {
            canvas.drawRect(backgroundRect, backgroundPaint)
        }
        designCell.draw(canvas)
        scoreLayout.draw(canvas)
        bestScoreLayout.draw(canvas)
    }
}