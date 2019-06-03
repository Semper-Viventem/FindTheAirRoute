package ru.semper_viventem.findtheairroute.ui.map

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import androidx.core.animation.addListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import ru.semper_viventem.findtheairroute.R
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
    var animationPosition: Float = START_ANIMATION
    var animationEnd: Float = END_ANIMATION

    fun start(googleMap: GoogleMap, from: LatLng, to: LatLng, begin: Float = START_ANIMATION, end: Float = END_ANIMATION) {
        latLngInterpolator = LatLngBezierInterpolator(from, to)

        val firstDuration = (animationDuration * Math.abs(end - begin)).toLong()
        val polyline = drawRoute(googleMap)
        val airplane = drawAirplane(googleMap, polyline)
        startAnimation(airplane, firstDuration, begin, end)
    }

    fun resume() {
        markerAnimation?.resume()
    }

    fun pause() {
        markerAnimation?.pause()
    }

    fun destroy() {
        killAnimation()
        latLngInterpolator = null
    }

    fun inProgress() = markerAnimation?.isStarted == true

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
}