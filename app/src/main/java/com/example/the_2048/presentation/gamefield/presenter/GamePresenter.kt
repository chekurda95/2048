package com.example.the_2048.presentation.gamefield.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.the_2048.data.models.game.Game
import com.example.the_2048.presentation.gamefield.view.IGameFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

const val duration = 70L

@InjectViewState
class GamePresenter: MvpPresenter<IGameFragment>(), IGamePresenter {

    private val game = Game()
    private val disposer = CompositeDisposable()

    override fun startNewGame() {
        game.startNewGame()
        viewState.drawField(game.getField().getCellsStringList())
    }

    override fun leftSwipe() {
        disposer.add(Observable.interval(0, duration, TimeUnit.MILLISECONDS)
            .take(4)
            .doOnNext{game.leftSwipe()}
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {viewState.animateField(game.getField().getMovedList())}
            .subscribe{viewState.drawField(game.getField().getCellsStringList())})
    }

    override fun rightSwipe() {
        disposer.add(Observable.interval(0, duration, TimeUnit.MILLISECONDS)
            .take(4)
            .doOnNext{game.rightSwipe()}
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {viewState.animateField(game.getField().getMovedList())}
            .subscribe{viewState.drawField(game.getField().getCellsStringList())})

    }

    override fun upSwipe() {
        disposer.add(Observable.interval(0, duration, TimeUnit.MILLISECONDS)
            .take(4)
            .doOnNext{game.upSwipe()}
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {viewState.animateField(game.getField().getMovedList())}
            .subscribe{viewState.drawField(game.getField().getCellsStringList())})
    }

    override fun downSwipe() {
        disposer.add(Observable.interval(0, duration, TimeUnit.MILLISECONDS)
            .take(4)
            .doOnNext{game.downSwipe()}
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {viewState.animateField(game.getField().getMovedList())}
            .subscribe{viewState.drawField(game.getField().getCellsStringList())})
    }

    override fun onDestroy() {
        disposer.dispose()
    }
}