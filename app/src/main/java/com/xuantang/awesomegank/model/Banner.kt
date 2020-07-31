package com.xuantang.awesomegank.model

data class Banner(
    var status: Int,
    var data: List<Item>
) {
    data class Item(
        var image: String,
        var title: String,
        var url: String
    )
}