package com.xuantang.awesomegank.model

data class ArticleResponse(var page: Int,
                             var page_count: Int,
                             var total_counts: Int,
                             var status: Int,
                             var data: ArrayList<ArticleModel>) {
    data class ArticleModel(var desc: String,
                            var publishedAt: String,
                            var url: String,
                            var _id: String,
                            var used: Boolean,
                            var images: List<String>,
                            var author: String)
}