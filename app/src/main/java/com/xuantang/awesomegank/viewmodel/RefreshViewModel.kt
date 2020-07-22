package com.xuantang.awesomegank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xuantang.awesomegank.extentions.dispatchDefault
import com.xuantang.awesomegank.extentions.yes
import com.xuantang.awesomegank.lifecycle.RxViewModel
import com.xuantang.awesomegank.model.Data
import com.xuantang.awesomegank.service.DataService
import io.reactivex.rxkotlin.subscribeBy

open class RefreshViewModel : RxViewModel() {
    private val refreshState: MutableLiveData<Int> = MutableLiveData()
    private val rxError: MutableLiveData<Throwable> = MutableLiveData()
    fun getRefreshState(): LiveData<Int> = refreshState

    fun setRefresh(state: Int) {
        refreshState.value = state
    }
}