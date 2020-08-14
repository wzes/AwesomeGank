package com.xuantang.awesomegank.model

data class HotResponse(
    val category: String,
    val data: ArrayList<HotItemModel>,
    val hot: String,
    val status: Int
) {
    data class HotItemModel(
        val _id: String,
        val author: String,
        val category: String,
        val createdAt: String,
        val desc: String,
        val images: List<String>,
        val likeCounts: Int,
        val publishedAt: String,
        val stars: Int,
        val title: String,
        val type: String,
        val url: String,
        val views: Int
    )
}