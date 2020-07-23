package com.xuantang.awesomegank.service

import com.xuantang.awesomegank.model.Banner
import com.xuantang.awesomegank.model.Daily
import com.xuantang.awesomegank.model.ArticleResponse
import com.xuantang.awesomegank.service.utils.retrofit
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface DataApi {
    @GET("v2/data/category/GanHuo/type/{type}/page/{page}/count/{count}")
    fun getDataOfType(
        @Path("type") type: String,
        @Path("count") count: Int,
        @Path("page") page: Int
    ): Observable<ArticleResponse>


    @GET("v2/data/category/Girl/type/Girl/page/{page}/count/{count}")
    fun getFuli(@Path("count") count: Int,
                @Path("page") page: Int): Observable<ArticleResponse>

    @GET("v2/banners")
    fun getBanners() : Observable<Banner>

    @GET("day/{year}/{month}/{day}")
    fun getDaily(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int
    ): Observable<Daily>

    @GET("today")
    fun getToday(): Observable<Daily>
}


object DataService : DataApi by retrofit.create(DataApi::class.java)