package ru.semper_viventem.findtheairroute.ui.map

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Color
import androidx.core.animation.addListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.ui.common.LatLngBezierInterpolator

class RouteAnimationDelegate(
    private val resources: Resources,
    private val imageRes: Int,
    private val animationDuration: Long
) {

    private var airplaneAnimator: Animator? = null

    fun start(googleMap: GoogleMap, from: LatLng, to: LatLng) {
        val polyline = drawRoute(googleMap, from, to)
        startAnimation(googleMap, polyline)
    }

    fun resume() {
        airplaneAnimator?.resume()
    }

    fun pause() {
        airplaneAnimator?.pause()
    }

    fun stop() {
        airplaneAnimator?.cancel()
        airplaneAnimator = null
    }

    fun inProgress() = airplaneAnimator?.isStarted == true

    private fun drawRoute(map: GoogleMap, from: LatLng, to: LatLng): Polyline {
        val strokeWidth = resources.getDimensionPixelOffset(R.dimen.route_stroke_width).toFloat()
        val strokeInterval = resources.getDimensionPixelOffset(R.dimen.route_stroke_interval).toFloat()
        val polyline = PolylineOptions()
            .add(*getBezierCurvePoints(from, to).toTypedArray())
            .width(strokeWidth)
            .jointType(JointType.ROUND)
            .color(Color.GRAY)
            .pattern(listOf(Gap(strokeInterval), Dash(strokeInterval)))

        return map.addPolyline(polyline)
    }

    private fun startAnimation(map: GoogleMap, polyline: Polyline) {
        val points = polyline.points

        val airplane = map.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(imageRes))
                .position(points.first())
                .anchor(1F, 0.5F)
                .flat(true)
        )

        animateRoute(airplane, points, false)
    }

    private fun animateRoute(airplane: Marker, points: List<LatLng>, isReverse: Boolean) {
        val interpolator = LatLngBezierInterpolator(points.first(), points.last())

        val begin = if (isReverse) 0F else 1F
        val end = if (isReverse) 1F else 0F

        airplaneAnimator = ValueAnimator.ofFloat(begin, end).apply {
            duration = animationDuration
            addUpdateListener {
                val v = it.animatedValue as Float
                val currentPosition = airplane.position
                val nextPosition = interpolator.interpolate(v.toDouble())
                handleTact(airplane, currentPosition, nextPosition, it.animatedValue as Float)
            }
            addListener(onEnd = {
                animateRoute(airplane, points, isReverse.not())
            })
            start()
        }
    }

    private fun handleTact(marker: Marker, startPosition: LatLng, endPosition: LatLng, value: Float) {
        val lng = value * endPosition.longitude + (1 - value) * startPosition.longitude
        val lat = value * endPosition.latitude + (1 - value) * startPosition.latitude
        val newPos = LatLng(lat, lng)
        marker.position = newPos
        marker.rotation = getBearing(marker.position, endPosition)
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

    private fun getBezierCurvePoints(from: LatLng, to: LatLng): List<LatLng> {
        val points = mutableListOf<LatLng>()
        val interpolator = LatLngBezierInterpolator(from, to)
        var t = 0.0
        while (t < 1.000001) {
            points.add(interpolator.interpolate(t))
            t += 0.01F
        }
        return points
    }
}