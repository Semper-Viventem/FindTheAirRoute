package ru.semper_viventem.findtheairroute.ui.map

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import androidx.core.animation.addListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.ui.common.LatLngBezierInterpolator

class RouteAnimationDelegate(
    private val context: Context,
    private val imageRes: Int,
    private val animationDuration: Long
) {

    private var airplaneAnimator: Animator? = null
    private var latLngInterpolator: LatLngBezierInterpolator? = null

    fun start(googleMap: GoogleMap, from: LatLng, to: LatLng) {
        latLngInterpolator = LatLngBezierInterpolator(from, to)

        val polyline = drawRoute(googleMap)
        val airplane = drawAirplane(googleMap, polyline)
        startAnimation(airplane)
    }

    fun resume() {
        airplaneAnimator?.resume()
    }

    fun pause() {
        airplaneAnimator?.pause()
    }

    fun stop() {
        airplaneAnimator?.end()
        airplaneAnimator?.cancel()
        latLngInterpolator = null
        airplaneAnimator = null
    }

    fun inProgress() = airplaneAnimator?.isStarted == true

    private fun drawRoute(map: GoogleMap): Polyline {
        val strokeWidth = context.resources.getDimensionPixelOffset(R.dimen.route_stroke_width).toFloat()
        val strokeInterval = context.resources.getDimensionPixelOffset(R.dimen.route_stroke_interval).toFloat()
        val polyline = PolylineOptions()
            .add(*getBezierCurvePoints().toTypedArray())
            .width(strokeWidth)
            .jointType(JointType.ROUND)
            .geodesic(true)
            .color(Color.GRAY)
            .pattern(listOf(Gap(strokeInterval), Dot()))

        return map.addPolyline(polyline)
    }

    private fun drawAirplane(map: GoogleMap, polyline: Polyline): Marker {
        val points = polyline.points

        return map.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(imageRes))
                .position(points.first())
                .anchor(0.5F, 0.5F)
                .flat(true)
        )
    }

    private fun startAnimation(airplane: Marker, begin: Float = 0F, end: Float = 1F) {

        airplaneAnimator = ValueAnimator.ofFloat(begin, end).apply {
            duration = animationDuration
            addUpdateListener {
                val v = it.animatedValue as Float
                val nextPosition = latLngInterpolator!!.interpolate(v.toDouble())

                airplane.rotation = getBearing(airplane.position, nextPosition)
                airplane.position = nextPosition
            }
            addListener(onEnd = {
                startAnimation(airplane, end, begin)
            })
            start()
        }
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

    private fun getBezierCurvePoints(): List<LatLng> {
        val points = mutableListOf<LatLng>()
        var t = 0.0
        while (t < 1.000001) {
            points.add(latLngInterpolator!!.interpolate(t))
            t += 0.01F
        }
        return points
    }
}