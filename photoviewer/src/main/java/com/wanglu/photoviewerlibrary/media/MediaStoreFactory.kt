package com.wanglu.photoviewerlibrary.media

import java.util.*
import kotlin.collections.HashMap

object MediaStoreFactory {
    private val mMediaStore: HashMap<String, List<MediaModel>> = HashMap()

    fun setPayload(medias: List<MediaModel>): String {
        val mediaModelKey = UUID.randomUUID().toString()
        mMediaStore[mediaModelKey] = medias
        return mediaModelKey
    }

    fun getPayload(key: String): List<MediaModel> {
        return mMediaStore[key]!!
    }

    fun clearPayload(key: String?) {
        if (key == null) {
            return
        }
        mMediaStore.remove(key)
    }
}