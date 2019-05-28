package ru.semper_viventem.findtheairroute.extensions

import com.google.android.gms.maps.model.LatLng
import ru.semper_viventem.findtheairroute.domain.Location

fun Location.toLatLng() = LatLng(lat, lon)

fun LatLng.distanceTo(latLng: LatLng): Float {
    val from = android.location.Location("").apply {
        this.latitude = this@distanceTo.latitude
        this.longitude = this@distanceTo.longitude
    }
    val to = android.location.Location("").apply {
        this.latitude = latLng.latitude
        this.longitude = latLng.longitude
    }

    return from.distanceTo(to)
}