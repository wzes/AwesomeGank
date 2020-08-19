package com.xuantang.awesomegank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xuantang.awesomegank.AppContext
import com.xuantang.awesomegank.database.CollectionDao
import com.xuantang.awesomegank.extentions.dispatchDefault
import com.xuantang.awesomegank.lifecycle.RxViewModel
import io.reactivex.rxkotlin.subscribeBy
import com.xuantang.awesomegank.database.Collection
import com.xuantang.awesomegank.database.CollectionDataBase

open class CollectionViewModel : RxViewModel() {
    private val collections: MutableLiveData<List<Collection>> = MutableLiveData()
    private val rxError = MutableLiveData<Throwable>()
    private val dataSource: CollectionDao = CollectionDataBase.getInstance(AppContext).collectionDao()

    fun getData(): LiveData<List<Collection>> {
        getSource()
        return collections
    }

    fun getError(): LiveData<Throwable> = rxError

    private fun getSource() {
        dataSource.getCollections()
            .dispatchDefault()
            .subscribeBy(
                onNext = {
                    collections.value = it
                },
                onError = {
                    rxError.value = it
                }
            )
    }
}