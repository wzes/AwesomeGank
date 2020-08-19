package com.xuantang.awesomegank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xuantang.awesomegank.extentions.dispatchDefault
import com.xuantang.awesomegank.lifecycle.RxViewModel
import com.xuantang.awesomegank.model.Banner
import com.xuantang.awesomegank.service.DataService
import io.reactivex.rxkotlin.subscribeBy

open class BannerViewModel : RxViewModel() {
    private val bannerData: MutableLiveData<List<Banner.Item>> = MutableLiveData()
    private val rxError: MutableLiveData<Throwable> = MutableLiveData()


    fun getData(): LiveData<List<Banner.Item>> {
        getBanner()
        return bannerData
    }

    fun getError(): LiveData<Throwable> = rxError

    private fun getBanner() {
        DataService.getBanners()
            .dispatchDefault()
            .subscribeBy(
                onNext = {
                    bannerData.value = it.data
                },
                onError = {
                    rxError.value = it
                    it.printStackTrace()
                }
            )
            .addDispose()
    }
}