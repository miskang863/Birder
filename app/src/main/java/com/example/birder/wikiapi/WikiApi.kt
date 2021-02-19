package com.example.birder.wikiapi

import com.example.birder.wikiJson.BirdImageJson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object WikiApi {
    private const val BASE_URL = "https://en.wikipedia.org/w/"

    interface Service {
        @GET("api.php")
        suspend fun bird(
            @Query("action") action: String,
            @Query("format") format: String,
            @Query("formatversion") formatversion: Int,
            @Query("prop") prop: String,
            @Query("piprop") piprop: String,
            @Query("titles") titles: String
        ): BirdImageJson

    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: Service = retrofit.create(Service::class.java)

}