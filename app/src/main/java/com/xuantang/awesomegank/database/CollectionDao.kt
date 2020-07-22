package com.xuantang.awesomegank.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collection ORDER BY id DESC")
    fun getCollections(): Flowable<List<Collection>>

    @Query("SELECT * FROM collection WHERE url = :url")
    fun checkIsCollected(url: String): Single<Collection>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCollection(collection: Collection)

    @Query("delete from collection where url = :url")
    fun delete(url: String)
}