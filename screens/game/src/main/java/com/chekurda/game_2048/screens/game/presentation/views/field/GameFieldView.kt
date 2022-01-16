package com.chekurda.game_2048.screens.game.presentation.views.field

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.chekurda.common.storeIn
import com.chekurda.common.surface.SurfaceDrawingThread
import com.chekurda.common.surface.SurfaceLayout
import com.chekurda.common.surface.SurfaceLayout.DrawingLayout
import com.chekurda.game_2048.screens.game.data.models.game.GameState
import com.chekurda.game_2048.screens.game.data.models.game.GameState.INIT
import com.chekurda.game_2048.screens.game.data.models.game.GameState.START_NEW_GAME
import com.chekurda.game_2048.screens.game.domain.GameController
import com.chekurda.game_2048.screens.game.domain.GameController.GameControllerConnector
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.GAME_FIELD_ROW_SIZE
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.GameBoard
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell.GameCell
import io.reactivex.disposables.CompositeDisposable
import java.lang.Math.random
import java.lang.RuntimeException

internal class GameFieldView(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context),
    DrawingLayout,
    GameControllerConnector {

    private val board = GameBoard(context)
    private val cells = HashMap<Int, GameCell>()
    private val allPositionList: List<Int> = mutableListOf<Int>().apply {
        for (position in 0 until GAME_FIELD_ROW_SIZE * GAME_FIELD_ROW_SIZE) add(position)
    }

    private var gameController: GameController? = null
    private var isSubscribed = false

    private val gameState: GameState
        get() = gameController?.state ?: INIT

    private var missedGameState: GameState? = null

    private var disposer = CompositeDisposable()
        get() {
            if (field.isDisposed) {
                field = CompositeDisposable()
            }
            return field
        }

    init {
        holder.addCallback(SurfaceCallback())
    }

    private fun onGameStateChanged(state: GameState) {
        when (state) {
            START_NEW_GAME -> startNewGame()
            else -> Unit
        }
    }

    override fun update(deltaTimeMs: Int) {
        cells.forEach { it.value.update(deltaTimeMs) }
    }

    override fun drawLayout(canvas: Canvas) {
        board.draw(canvas)
        cells.forEach {
            it.value.draw(canvas)
        }
    }

    private fun startNewGame() {
        if (board.isReady) {
            cells.clear()
            addNewCell()

            requireGameController().gameIsReady()
        } else {
            missedGameState = START_NEW_GAME
        }
    }

    private fun addNewCell() {
        val emptyPositions = allPositionList.toMutableList()
        cells.forEach {
            emptyPositions.remove(it.key)
        }
        if (emptyPositions.isNotEmpty()) {
            val randomPosition = ((random() * 100).toInt() % emptyPositions.size)
            val randomValue = ((random() * 100).toInt() % 2 + 1) * 2

            cells[randomPosition] = GameCell(context).apply {
                value = randomValue
                setRect(board.getRectForCell(randomPosition))
                animateShowing()
            }
        }
    }

    private fun subscribeOnState() {
        if (!isSubscribed) {
            gameController?.stateObservable
                ?.subscribe(::onGameStateChanged)
                ?.storeIn(disposer)
        }
    }

    override fun attachGameController(controller: GameController) {
        gameController = controller
        subscribeOnState()
    }

    private fun requireGameController(): GameController =
        gameController!!

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        subscribeOnState()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposer.dispose()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = if (!isInEditMode) MeasureSpec.getSize(heightMeasureSpec) else width
        setMeasuredDimension(width, height)

        if (measuredWidth != measuredHeight) {
            throw RuntimeException("Поле не квадратное: width = $measuredWidth, height = $measuredHeight")
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
            board.setResolution(width, height)
            missedGameState?.let(::onGameStateChanged)
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            thread.interrupt()
        }
    }
}