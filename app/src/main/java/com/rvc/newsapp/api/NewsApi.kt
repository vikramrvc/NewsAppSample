package com.rvc.newsapp.api

import com.rvc.newsapp.model.NewsResponse
import com.rvc.newsapp.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getHeadlines(

        @Query("country")
        country:String ="us",
        @Query("page")
        pageNum:Int = 1,
        @Query("apiKey")
        apiKey:String =Constants.API_KEY
    ):Response<NewsResponse>

    @GET("v2/everything")
    suspend fun search(
        @Query("q")
        searchQuery:String,
        @Query("page")
        pageNum:Int = 1,
        @Query("apiKey")
        apiKey:String =Constants.API_KEY
    ): Response<NewsResponse>
}