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
import com.chekurda.game_2048.screens.game.presentation.views.config.GameConfig.gameFPS
import com.chekurda.game_2048.screens.game.presentation.views.config.GameConfig.gameFieldRowSize
import com.chekurda.game_2048.screens.game.presentation.views.field.domain.SwipeController
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.GameBoard
import com.chekurda.game_2048.screens.game.presentation.views.field.layouts.cell.GameCell
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.FieldRandomHelper.getRandomItem
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.FieldRandomHelper.randomValue
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeDirection
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeListener
import io.reactivex.disposables.CompositeDisposable
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentHashMap

internal class GameFieldView(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context),
    DrawingLayout,
    GameControllerConnector,
    SwipeListener,
    SwipeController.FieldInfo {

    private val board = GameBoard(context)
    private val allPositionList: List<Int> = mutableListOf<Int>().apply {
        for (position in 0 until gameFieldRowSize * gameFieldRowSize) add(position)
    }

    override val cells = ConcurrentHashMap<Int, GameCell>()

    override fun getRectForPosition(position: Int): RectF =
        board.getRectForCell(position)

    private var gameController: GameController? = null
    private var isSubscribed = false

    private val gameState: GameState
        get() = gameController?.state ?: INIT

    private var missedGameState: GameState? = null

    private val swipeController = SwipeController(this).apply {
        swipeFinishedListener = ::onSwipeFinished
    }

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
            addNewRandomCell()
            addNewRandomCell()

            requireGameController().gameIsReady()
        } else {
            missedGameState = START_NEW_GAME
        }
    }

    /**
     * Добавляет новую случайную ячейку на поле и возвращает true.
     * Если все ячейки заняты - возвращает false.
     */
    private fun addNewRandomCell(): Boolean =
        getRandomItem(getEmptyPositions())?.let { emptyPosition ->
            addNewCell(emptyPosition, randomValue)
            true
        } ?: false

    /**
     * Добавляет новую ячейку на поле на позицию [position] со значением [value].
     */
    private fun addNewCell(position: Int, value: Int) {
        cells[position] = GameCell(context).apply {
            this.value = value
            setRect(board.getRectForCell(position))
            animateShowing()
        }
    }

    private fun getEmptyPositions(): List<Int> =
        allPositionList.toMutableList().apply {
            cells.forEach { remove(it.key) }
        }

    override fun onSwipe(direction: SwipeDirection) {
        swipeController.onSwipe(direction)
    }

    private fun onSwipeFinished(isChanged: Boolean) {
        if (isChanged) addNewRandomCell()
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