package com.xuantang.awesomegank.model

data class NoImageArticleResponse(var page: Int,
                           var data: ArrayList<ArticleModel>) {
    data class ArticleModel(var desc: String,
                            var publishedAt: String,
                            var url: String,
                            var used: Boolean,
                            var who: String)
}