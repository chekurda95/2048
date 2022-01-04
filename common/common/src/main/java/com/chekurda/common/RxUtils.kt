package com.chekurda.common

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.storeIn(disposer: CompositeDisposable) {
    disposer.add(this)
}