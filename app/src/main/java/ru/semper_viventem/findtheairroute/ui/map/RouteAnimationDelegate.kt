package ru.semper_viventem.findtheairroute.ui.map

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.animation.addListener
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.chip.ChipDrawable
import com.google.maps.android.SphericalUtil
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.extensions.toLatLng
import ru.semper_viventem.findtheairroute.ui.common.LatLngBezierInterpolator


class RouteAnimationDelegate(
    private val context: Context,
    private val imageRes: Int,
    private val animationDuration: Long,
    private val markerRotation: Int = DEFAULT_MARKER_ROTATION
) {

    companion object {
        private const val START_ANIMATION = 0F
        private const val END_ANIMATION = 1F
        private const val DEFAULT_MARKER_ROTATION = 0
    }

    private var markerAnimation: Animator? = null
    private var latLngInterpolator: LatLngBezierInterpolator? = null

    private var airplaneMarker: Marker? = null
    private var fromCityMarker: Marker? = null
    private var toCityMarker: Marker? = null

    private var polyline: Polyline? = null

    var animationPosition: Float = START_ANIMATION
    var animationEnd: Float = END_ANIMATION

    fun start(
        googleMap: GoogleMap,
        fromCity: City,
        toCity: City,
        begin: Float = START_ANIMATION,
        end: Float = END_ANIMATION
    ) {

        val from = fromCity.location.toLatLng()
        val to = toCity.location.toLatLng()

        initMapState(googleMap, fromCity, toCity)
        latLngInterpolator = LatLngBezierInterpolator(from, to)

        val firstDuration = (animationDuration * Math.abs(end - begin)).toLong()
        polyline = drawRoute(googleMap)
        airplaneMarker = drawAirplane(googleMap, polyline!!)
        startAnimation(airplaneMarker!!, firstDuration, begin, end)
    }

    fun resume() {
        markerAnimation?.resume()
    }

    fun pause() {
        markerAnimation?.pause()
    }

    fun destroy() {
        killAnimation()

        airplaneMarker?.remove()
        fromCityMarker?.remove()
        toCityMarker?.remove()
        polyline?.remove()

        airplaneMarker = null
        fromCityMarker = null
        toCityMarker = null
        polyline = null

        latLngInterpolator = null
    }

    fun inProgress() = markerAnimation?.isStarted == true

    private fun initMapState(googleMap: GoogleMap, fromCity: City, toCity: City) {
        fromCityMarker = addMarker(googleMap, fromCity.location.toLatLng(), fromCity.getShortName())
        toCityMarker = addMarker(googleMap, toCity.location.toLatLng(), toCity.getShortName())

        val bounds = LatLngBounds.Builder()
            .include(fromCityMarker!!.position)
            .include(toCityMarker!!.position)
            .build()

        val cameraOffset = context.resources.getDimensionPixelOffset(R.dimen.big_gap)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, cameraOffset))
    }

    private fun drawRoute(googleMap: GoogleMap): Polyline {
        val strokeWidth = context.resources.getDimensionPixelOffset(R.dimen.route_stroke_width).toFloat()
        val strokeInterval = context.resources.getDimensionPixelOffset(R.dimen.route_stroke_interval).toFloat()
        val polylineOptions = PolylineOptions()
            .add(*getBezierCurvePoints().toTypedArray())
            .width(strokeWidth)
            .jointType(JointType.ROUND)
            .geodesic(true)
            .color(Color.GRAY)
            .pattern(listOf(Gap(strokeInterval), Dot()))

        return googleMap.addPolyline(polylineOptions)
    }

    private fun drawAirplane(googleMap: GoogleMap, polyline: Polyline): Marker {
        val points = polyline.points

        return googleMap.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(imageRes))
                .position(points.first())
                .anchor(0.5F, 0.5F)
                .flat(true)
        )
    }

    private fun addMarker(googleMap: GoogleMap, location: LatLng, title: String): Marker {
        val marker = MarkerOptions()
            .position(location)
            .icon(getCityMarker(title))
            .flat(true)
            .anchor(0.5F, 0.5F)

        return googleMap.addMarker(marker)
    }

    private fun startAnimation(airplane: Marker, animationDuration: Long, begin: Float = START_ANIMATION, end: Float = END_ANIMATION) {
        animationEnd = end

        killAnimation()
        markerAnimation = ValueAnimator.ofFloat(begin, end).apply {
            duration = animationDuration
            addUpdateListener {
                val v = it.animatedValue as Float
                animationPosition = v

                if (latLngInterpolator == null) return@addUpdateListener
                val nextPosition = latLngInterpolator!!.interpolate(v.toDouble())

                airplane.rotation = angleFromCoordinate(airplane.position, nextPosition)
                airplane.position = nextPosition
            }
            addListener(onEnd = {
                startAnimation(airplane, this@RouteAnimationDelegate.animationDuration, end, 1 - end)
            })
            start()
        }
    }

    private fun killAnimation() {
        markerAnimation?.removeAllListeners()
        markerAnimation?.cancel()
        markerAnimation = null
    }

    private fun angleFromCoordinate(begin: LatLng, end: LatLng): Float {
        return SphericalUtil.computeHeading(begin, end).toFloat() + markerRotation
    }

    private fun getBezierCurvePoints(): List<LatLng> {
        val points = mutableListOf<LatLng>()
        var t = 0.0
        while (t < 1.000001) {
            points.add(latLngInterpolator!!.interpolate(t))
            t += 0.01F
        }
        return points
    }

    private fun getCityMarker(name: String): BitmapDescriptor {
        val drawable = ChipDrawable.createFromResource(context, R.xml.city_chip_drawable)
        drawable.setText(name)

        val bitmap = drawable.toBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}