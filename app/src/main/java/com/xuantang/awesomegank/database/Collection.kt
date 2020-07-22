package com.xuantang.awesomegank.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = TABLE_NAME, indices = [Index(value = ["url"], unique = true)])
data class Collection(
    val title: String,
    val url: String,
    val time: String,
    var cover: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}

const val TABLE_NAME = "collection"