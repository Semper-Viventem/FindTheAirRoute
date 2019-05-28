package ru.semper_viventem.findtheairroute.ui.map

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.animation.addListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.screen_result.view.*
import org.koin.android.ext.android.getKoin
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.domain.Location
import ru.semper_viventem.findtheairroute.extensions.distanceTo
import ru.semper_viventem.findtheairroute.extensions.toLatLng
import ru.semper_viventem.findtheairroute.ui.UIModule
import ru.semper_viventem.findtheairroute.ui.common.MapScreen


class ResultScreen : MapScreen<ResultPm>() {

    companion object {
        private const val ARG_FROM_CITY = "from_city"
        private const val ARG_TO_CITY = "to_city"
        fun newInstance(fromCity: City, toCity: City) = ResultScreen().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_FROM_CITY, fromCity)
                putSerializable(ARG_TO_CITY, toCity)
            }
        }
    }

    override val layoutRes: Int = R.layout.screen_result

    override fun provideMapFragmentId(view: View): MapView = view.map
    private var airplaneAnimator: Animator? = null

    override fun providePresentationModel(): ResultPm = getKoin()
        .apply {
            setProperty(UIModule.PROPERTY_FROM_CITY, arguments!!.getSerializable(ARG_FROM_CITY))
            setProperty(UIModule.PROPERTY_TO_CITY, arguments!!.getSerializable(ARG_TO_CITY))
        }
        .get()

    override fun onInitMap(googleMap: GoogleMap) {
        with(googleMap.uiSettings) {
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
        }
    }

    override fun onBindPresentationModel(view: View, pm: ResultPm) {
        // TODO
    }

    override fun onBindMapPresentationModel(pm: ResultPm, googleMap: GoogleMap) {
        pm.points bindTo { (fromCity, toCity) ->
            val markerFrom = addMarker(googleMap, fromCity.location, fromCity.cityName)
            val markerTo = addMarker(googleMap, toCity.location, toCity.cityName)

            val bounds = LatLngBounds.Builder()
                .include(markerFrom.position)
                .include(markerTo.position)
                .build()

            val cameraOffset = resources.getDimensionPixelOffset(R.dimen.normal_gap)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, cameraOffset))

            val route = drawRoute(googleMap, fromCity.location.toLatLng(), toCity.location.toLatLng())
            drawAnimation(googleMap, route)
        }
    }

    private fun addMarker(map: GoogleMap, location: Location, title: String): Marker {
        val marker = MarkerOptions()
            .position(location.toLatLng())
            .title(title)

        return map.addMarker(marker)
    }

    private fun drawRoute(map: GoogleMap, from: LatLng, to: LatLng): Polyline {
        val strokeWidth = resources.getDimensionPixelOffset(R.dimen.route_stroke_width).toFloat()
        val strokeInterval = resources.getDimensionPixelOffset(R.dimen.route_stroke_interval).toFloat()
        val polyline = PolylineOptions()
            .add(from, LatLng(0.0, 0.0), to)
            .width(strokeWidth)
            .jointType(JointType.ROUND)
            .color(Color.GRAY)
            .pattern(listOf(Gap(strokeInterval), Dash(strokeInterval)))

        return map.addPolyline(polyline)
    }

    private fun drawAnimation(map: GoogleMap, polyline: Polyline) {
        val points = polyline.points

        val airplane = map.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_plane))
                .position(points.first())
                .anchor(0.5F, 0.5F)
        )

        val intervalsDuration = getIntervalsDuration(points, 5000)

        animateNextInterval(airplane, points, intervalsDuration, 0)
    }

    private fun animateNextInterval(airplane: Marker, points: List<LatLng>, intervalDurations: List<Long>, intervalNumber: Int) {

        if (intervalNumber == points.lastIndex) return

        val startPosition = points[intervalNumber]
        val endPosition = points[intervalNumber + 1]

        airplaneAnimator?.cancel()
        airplaneAnimator = null
        airplaneAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            duration = intervalDurations[intervalNumber]
            addUpdateListener {
                handleTact(airplane, startPosition, endPosition, it.animatedValue as Float)
            }
            addListener(onEnd = {
                animateNextInterval(airplane, points, intervalDurations, intervalNumber + 1)
            })
            start()
        }
    }

    private fun handleTact(marker: Marker, startPosition: LatLng, endPosition: LatLng, v: Float) {
        val lng = v * endPosition.longitude + (1 - v) * startPosition.longitude
        val lat = v * endPosition.latitude + (1 - v) * startPosition.latitude
        val newPos = LatLng(lat, lng)
        marker.position = newPos
        marker.rotation = getBearing(marker.position, endPosition)
    }

    private fun getIntervalsDuration(points: List<LatLng>, fullDuration: Long): List<Long> {
        val distancies = mutableListOf<Float>()
        points.forEachIndexed { index, latLng ->
            if (index != points.lastIndex) {
                distancies.add(latLng.distanceTo(points[index + 1]))
            }
        }

        val total = distancies.sum()

        val result = mutableListOf<Long>()

        distancies.forEach { part ->
            val weight = fullDuration / total * part
            result.add(weight.toLong())
        }

        return result
    }

    private fun getBearing(begin: LatLng, end: LatLng): Float {
        val lat = Math.abs(begin.latitude - end.latitude)
        val lng = Math.abs(begin.longitude - end.longitude)

        val bearing = if (begin.latitude < end.latitude && begin.longitude < end.longitude) {
            Math.toDegrees(Math.atan(lng / lat)).toFloat()
        } else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) {
            (90 - Math.toDegrees(Math.atan(lng / lat)) + 90).toFloat()
        } else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) {
            (Math.toDegrees(Math.atan(lng / lat)) + 180).toFloat()
        } else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) {
            (90 - Math.toDegrees(Math.atan(lng / lat)) + 270).toFloat()
        } else 0F

        return bearing - 90
    }
}