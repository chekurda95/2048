package com.example.the_2048.presentation.gamefield.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.the_2048.data.models.game.Game
import com.example.the_2048.presentation.gamefield.delegates.SwipeDirection
import com.example.the_2048.presentation.gamefield.delegates.SwipeDirection.*
import com.example.the_2048.presentation.gamefield.view.IGameFragment
import com.example.the_2048.utils.storeIn
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

private const val duration = 70L

@InjectViewState
class GamePresenter: MvpPresenter<IGameFragment>(), IGamePresenter {

    private val game = Game()
    private val disposer = CompositeDisposable()

    override fun startNewGame() {
        game.startNewGame()
        viewState.drawField(game.getField().getCellsValues())
    }

    override fun onSwipe(direction: SwipeDirection) {
        val swipeAction = when (direction) {
            UP    -> game::upSwipe
            DOWN  -> game::downSwipe
            LEFT  -> game::leftSwipe
            RIGHT -> game::rightSwipe
        }
        performSwipeAction(swipeAction)
    }

    private fun performSwipeAction(swipeAction: SwipeAction) {
        Observable.interval(0, duration, TimeUnit.MILLISECONDS)
            .take(4)
            .doOnNext { swipeAction.invoke() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { viewState.animateField(game.getField().getMovedList()) }
            .subscribe { viewState.drawField(game.getField().getCellsValues()) }
            .storeIn(disposer)
    }

    override fun onDestroy() {
        disposer.dispose()
    }
}

private typealias SwipeAction = () -> Unit