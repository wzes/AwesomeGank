package com.xuantang.awesomegank.lifecycle

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class RxViewModel : ViewModel() {
    private val disposes = CompositeDisposable()

    override fun onCleared() {
        disposes.clear()
        super.onCleared()
    }

    protected fun Disposable.addDispose() {
        disposes.add(this)
    }
}