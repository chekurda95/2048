package com.chekurda.game_2048.screens.game.presentation.views.field

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
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
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.gameFPS
import com.chekurda.game_2048.screens.game.presentation.views.field.config.GameConfig.gameFieldRowSize
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.GameBoard
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell.GameCell
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.FieldRandomHelper.getRandomItem
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.FieldRandomHelper.randomValue
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeDirection
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeListener
import io.reactivex.disposables.CompositeDisposable
import java.lang.RuntimeException

internal class GameFieldView(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context),
    DrawingLayout,
    GameControllerConnector,
    SwipeListener,
    SwipeController.CellsHolder {

    private val board = GameBoard(context)
    private val allPositionList: List<Int> = mutableListOf<Int>().apply {
        for (position in 0 until gameFieldRowSize * gameFieldRowSize) add(position)
    }

    override val cells = HashMap<Int, GameCell>()

    override fun getRectOfPosition(position: Int): RectF =
        board.getRectForCell(position)

    private var gameController: GameController? = null
    private var isSubscribed = false

    private val gameState: GameState
        get() = gameController?.state ?: INIT

    private var missedGameState: GameState? = null

    private val swipeController = SwipeController(this)

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
        swipeController.update(deltaTimeMs)
        cells.forEach { it.value.update(deltaTimeMs) }
    }

    override fun drawLayout(canvas: Canvas) {
        board.draw(canvas)
        cells.forEach {
            it.value.draw(canvas)
        }
    }

    private fun startNewGame() {
        swipeController.stopMoving()

        if (board.isReady) {
            cells.clear()
            addNewCell()
            addNewCell()

            requireGameController().gameIsReady()
        } else {
            missedGameState = START_NEW_GAME
        }
    }

    /**
     * Добавляет новую ячейку на поле и возвращает true.
     * Если все ячейки заняты - возвращает false.
     */
    private fun addNewCell(): Boolean =
        getRandomItem(getEmptyPositions())?.let { emptyPosition ->
            cells[emptyPosition] = GameCell(context).apply {
                value = randomValue
                setRect(board.getRectForCell(emptyPosition))
                animateShowing()
            }
            true
        } ?: false

    private fun getEmptyPositions(): List<Int> =
        allPositionList.toMutableList().apply {
            cells.forEach { remove(it.key) }
        }

    private fun getCell(x: Int, y: Int) {
        cells[gameFieldRowSize * y + x]
    }

    override fun onSwipe(direction: SwipeDirection) {
        swipeController.onSwipe(direction)
    }

    override fun attachGameController(controller: GameController) {
        gameController = controller
        subscribeOnState()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        subscribeOnState()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposer.dispose()
    }

    private fun subscribeOnState() {
        if (!isSubscribed) {
            gameController?.stateObservable
                ?.subscribe(::onGameStateChanged)
                ?.storeIn(disposer)
        }
    }

    private fun requireGameController(): GameController =
        gameController!!

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
                fps = gameFPS
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