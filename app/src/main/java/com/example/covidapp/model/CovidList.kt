package com.example.covidapp.model

data class CovidList(
    val Countries: List<Country>,
    val Date: String,
    val Global: Global,
    val ID: String,
    val Message: String
)