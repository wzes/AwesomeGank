package com.xuantang.awesomegank.model

data class NoImageArticleResponse(
    var error: Boolean,
    var results: ArrayList<ArticleModel>
) {
    data class ArticleModel(
        var desc: String,
        var publishedAt: String,
        var url: String,
        var _id: String,
        var used: Boolean,
        var who: String
    )
}