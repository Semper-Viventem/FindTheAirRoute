package ru.semper_viventem.findtheairroute.ui.map

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Color
import androidx.core.animation.addListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.extensions.distanceTo
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
                .anchor(0.5F, 0.5F)
        )

        val intervalsDuration = getIntervalsDuration(points, animationDuration)

        animateNextInterval(airplane, points, intervalsDuration, 0)
    }

    private fun animateNextInterval(airplane: Marker, points: List<LatLng>, intervalDurations: List<Long>, intervalNumber: Int) {

        airplaneAnimator?.cancel()
        airplaneAnimator = null

        if (intervalNumber == points.lastIndex) return

        val startPosition = points[intervalNumber]
        val endPosition = points[intervalNumber + 1]
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