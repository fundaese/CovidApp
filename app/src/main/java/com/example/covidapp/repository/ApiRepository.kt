package com.example.covidapp.repository

import com.example.covidapp.api.ApiService
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getCovidListFromRepo() = apiService.getCovidList()
}