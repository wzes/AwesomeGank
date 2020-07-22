package com.xuantang.awesomegank.model

data class Data(var error: Boolean,
                var results: ArrayList<Results>) {
    data class Results(var desc: String,
                       var publishedAt: String,
                       var url: String,
                       var used: Boolean,
                       var who: String)
}