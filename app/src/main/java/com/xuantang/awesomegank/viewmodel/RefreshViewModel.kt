package com.xuantang.awesomegank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xuantang.awesomegank.lifecycle.RxViewModel

open class RefreshViewModel : RxViewModel() {
    private val refreshState: MutableLiveData<Int> = MutableLiveData()
    private val rxError: MutableLiveData<Throwable> = MutableLiveData()
    fun getRefreshState(): LiveData<Int> = refreshState

    fun setRefresh(state: Int) {
        refreshState.value = state
    }
}