package ru.semper_viventem.findtheairroute.domain

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("id") val id: Int,
    @SerializedName("city") val cityName: String,
    @SerializedName("country") val countryName: String,
    @SerializedName("fullname") val fullName: String,
    @SerializedName("location") val location: Location
)