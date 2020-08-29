package com.xuantang.awesomegank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xuantang.basemodule.extentions.dispatchDefault
import com.xuantang.awesomegank.lifecycle.RxViewModel
import com.xuantang.awesomegank.model.ArticleResponse
import com.xuantang.awesomegank.model.HotResponse
import com.xuantang.awesomegank.service.DataService
import io.reactivex.rxkotlin.subscribeBy

open class HotViewModel : RxViewModel() {
    private val articleData: MutableLiveData<ArrayList<HotResponse.HotItemModel>> = MutableLiveData()

    private val rxError: MutableLiveData<Throwable> = MutableLiveData()
    fun getHotData(): LiveData<ArrayList<HotResponse.HotItemModel>> = articleData
    fun getError(): LiveData<Throwable> = rxError

    private fun getArticle(category: String, count: Int, refresh: Boolean = false) {
        DataService.getHotList(category, "views", count)
            .dispatchDefault()
            .subscribeBy(
                onNext = {
                    if (refresh || articleData.value == null) {
                        articleData.value = it.data
                    } else {
                        articleData.value?.addAll(it.data)
                        articleData.value = articleData.value
                    }
                },
                onError = {
                    rxError.value = it
                    it.printStackTrace()
                }
            )
            .addDispose()
    }

    fun fetch(category: String, count: Int, refresh: Boolean = false) {
        getArticle(category, count, refresh)
    }
}