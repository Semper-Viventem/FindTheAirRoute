package ru.semper_viventem.findtheairroute.domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class City(
    @SerializedName("id") val id: Int,
    @SerializedName("city") val cityName: String,
    @SerializedName("country") val countryName: String,
    @SerializedName("fullname") val fullName: String,
    @SerializedName("location") val location: Location,
    @SerializedName("iata") val iata: List<String>
) : Serializable {

    fun getShortName(): String {
        return if (iata.isNotEmpty()) {
            iata.first()
        } else {
            cityName
        }
    }
}