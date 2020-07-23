package com.xuantang.awesomegank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xuantang.awesomegank.extentions.dispatchDefault
import com.xuantang.awesomegank.lifecycle.RxViewModel
import com.xuantang.awesomegank.model.ArticleResponse
import com.xuantang.awesomegank.service.DataService
import io.reactivex.rxkotlin.subscribeBy

open class FuliViewModel : RxViewModel() {
    private val fuliData: MutableLiveData<ArrayList<ArticleResponse.ArticleModel>> = MutableLiveData()

    private val rxError: MutableLiveData<Throwable> = MutableLiveData()
    fun getFuliData(): LiveData<ArrayList<ArticleResponse.ArticleModel>> = fuliData
    fun getError(): LiveData<Throwable> = rxError

    private fun getFuli(page: Int, refresh: Boolean = false) {
        DataService.getFuli(10, page)
            .dispatchDefault()
            .subscribeBy(
                onNext = {
                    if (refresh || fuliData.value == null) {
                        fuliData.value = it.data
                    } else {
                        fuliData.value?.addAll(it.data)
                        fuliData.value = fuliData.value
                    }
                },
                onError = {
                    rxError.value = it
                    it.printStackTrace()
                }
            )
            .addDispose()
    }

    fun fetch(page: Int, refresh: Boolean = false) {
        getFuli(page, refresh)
    }
}