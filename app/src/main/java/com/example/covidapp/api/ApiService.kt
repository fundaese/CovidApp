package com.example.covidapp.api

import com.example.covidapp.model.CovidList
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("summary")
    suspend fun getCovidList(): Response<CovidList>

}