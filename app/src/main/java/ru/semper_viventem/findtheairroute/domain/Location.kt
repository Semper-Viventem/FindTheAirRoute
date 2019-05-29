package ru.semper_viventem.findtheairroute.domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
): Serializable