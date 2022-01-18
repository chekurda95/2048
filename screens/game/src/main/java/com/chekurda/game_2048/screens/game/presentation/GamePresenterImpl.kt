package com.chekurda.game_2048.screens.game.presentation

import com.chekurda.common.base_fragment.BasePresenterImpl
import com.chekurda.game_2048.screens.game.data.models.game.Game
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeDirection
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeDirection.*
import com.chekurda.common.storeIn
import com.chekurda.game_2048.screens.game.data.models.game.GameState.INIT
import com.chekurda.game_2048.screens.game.domain.GameController
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

internal class GamePresenterImpl : BasePresenterImpl<GameFragmentView>(), GamePresenter {

    private val gameController = GameController()

    private val game = Game()
    private val disposer = CompositeDisposable()

    override fun attachView(view: GameFragmentView) {
        super.attachView(view)
        view.attachGameController(gameController)

        tryStartNewGame()
    }

    private fun tryStartNewGame() {
        if (gameController.state == INIT) {
            startNewGame()
        }
    }

    override fun startNewGame() {
        gameController.startNewGame()
        /*game.startNewGame()
        view!!.drawField(game.getField().getCellsValues())*/
    }

    override fun onSwipe(direction: SwipeDirection) {
        val swipeAction = when (direction) {
            UP -> game::upSwipe
            DOWN -> game::downSwipe
            LEFT -> game::leftSwipe
            RIGHT -> game::rightSwipe
        }
        performSwipeAction(swipeAction)
    }

    private fun performSwipeAction(swipeAction: SwipeAction) {
        Observable.interval(0, STEP_DURATION, TimeUnit.MILLISECONDS)
            .take(4)
            .doOnNext { swipeAction.invoke() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { view!!.animateField(game.getField().getMovedList()) }
            .subscribe { view!!.drawField(game.getField().getCellsValues()) }
            .storeIn(disposer)
    }

    override fun onDestroy() {
        disposer.dispose()
    }
}

private typealias SwipeAction = () -> Unit

private const val STEP_DURATION = 70L