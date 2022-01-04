package com.chekurda.game_2048.presentation.gamefield.presenter

import androidx.lifecycle.ViewModel
import com.chekurda.game_2048.data.models.game.Game
import com.chekurda.game_2048.presentation.gamefield.delegates.SwipeDirection
import com.chekurda.game_2048.presentation.gamefield.delegates.SwipeDirection.*
import com.chekurda.game_2048.presentation.gamefield.view.IGameFragment
import com.chekurda.common.storeIn
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class GamePresenter: ViewModel(), IGamePresenter {

    private var view: IGameFragment? = null
    private val game = Game()
    private val disposer = CompositeDisposable()

    override fun attachView(view: IGameFragment) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun startNewGame() {
        game.startNewGame()
        view!!.drawField(game.getField().getCellsValues())
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

    override fun onCleared() {
        disposer.dispose()
    }
}

private typealias SwipeAction = () -> Unit

private const val STEP_DURATION = 70L