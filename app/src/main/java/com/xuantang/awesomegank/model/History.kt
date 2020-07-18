package com.xuantang.awesomegank.model

import com.google.gson.annotations.SerializedName

data class History(
    val error: Boolean,
    val results: List<HistoryResult>
)

data class HistoryResult(
    @SerializedName("content")
    var cover: String?,
    val publishedAt: String,
    val title: String
)