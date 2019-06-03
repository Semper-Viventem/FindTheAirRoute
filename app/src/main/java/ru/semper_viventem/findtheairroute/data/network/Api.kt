package ru.semper_viventem.findtheairroute.data.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("autocomplete")
    fun getCities(@Query("term") query: String, @Query("lang") lang: String): Single<CitiesResponse>
}