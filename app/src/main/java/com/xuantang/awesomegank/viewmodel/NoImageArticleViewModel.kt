package com.xuantang.awesomegank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xuantang.basemodule.extentions.dispatchDefault
import com.xuantang.awesomegank.lifecycle.RxViewModel
import com.xuantang.awesomegank.model.NoImageArticleResponse
import com.xuantang.awesomegank.service.DataService
import io.reactivex.rxkotlin.subscribeBy

open class NoImageArticleViewModel : RxViewModel() {
    private val articleData: MutableLiveData<ArrayList<NoImageArticleResponse.ArticleModel>> = MutableLiveData()

    private val rxError: MutableLiveData<Throwable> = MutableLiveData()
    fun getArticleData(): LiveData<ArrayList<NoImageArticleResponse.ArticleModel>> = articleData
    fun getError(): LiveData<Throwable> = rxError

    private fun getArticle(category: String, page: Int, refresh: Boolean = false) {
        DataService.getNoImageDataOfType(category, 10, page)
            .dispatchDefault()
            .subscribeBy(
                onNext = {
                    if (refresh || articleData.value == null) {
                        articleData.value = it.results
                    } else {
                        articleData.value?.addAll(it.results)
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

    fun fetch(category: String, page: Int, refresh: Boolean = false) {
        getArticle(category, page, refresh)
    }
}