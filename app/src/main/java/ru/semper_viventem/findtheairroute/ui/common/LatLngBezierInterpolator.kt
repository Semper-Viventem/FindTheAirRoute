package ru.semper_viventem.findtheairroute.ui.common

import com.google.android.gms.maps.model.LatLng

/**
 * Source: https://github.com/NooAn/bytheway/blob/master/app/src/main/java/ru/a1024bits/bytheway/util/LatLngInterpolator.kt
 */
class LatLngBezierInterpolator(
    private val fromLocation: LatLng,
    private val toLocation: LatLng
) {
    fun interpolate(t: Double): LatLng {
        return calculateBezierFunction(t, fromLocation, toLocation)
    }

    private fun calculateBezierFunction(t: Double, fromLocation: LatLng, toLocation: LatLng): LatLng {

        val lat1 = fromLocation.latitude
        val lat2 = toLocation.latitude

        val lon1 = fromLocation.longitude
        val lon2 = toLocation.longitude


        val angle = findArctg(lat1, lat2, lon1, lon2)
        val module = module(lat1, lat2, lon1, lon2)

        val latCentral = (lat2 + lat1) / 2
        val lonCentral = (lon2 + lon1) / 2

        val latTop = latCentral + module / 4
        val lonTop = lonCentral

        val latBottom = latCentral - module / 4
        val lonBottom = lonCentral

        val rotatedTop = rotatePoint(lonTop, latTop, lonCentral, latCentral, Math.toRadians(90 - (90 - angle)))
        val rotatedBottom = rotatePoint(lonBottom, latBottom, lonCentral, latCentral, Math.toRadians(90 - (90 - angle)))

        val point2 = LatLng(rotatedTop[1], rotatedTop[0])
        val point3 = LatLng(rotatedBottom[1], rotatedBottom[0])

        //  x == lat, y == lon
        val a1 = fromLocation.latitude * Math.pow(1 - t, 3.0)
        val b1 = 3 * point2.latitude * t * (Math.pow(t, 2.0) - 2 * t + 1)
        val c1 = 3 * point3.latitude * Math.pow(t, 2.0) * (1 - t)
        val d1 = toLocation.latitude * Math.pow(t, 3.0)
        val x = a1 + b1 + c1 + d1

        val a2 = fromLocation.longitude * Math.pow(1 - t, 3.0)
        val b2 = 3 * point2.longitude * t * (Math.pow(t, 2.0) - 2 * t + 1)
        val c2 = 3 * point3.longitude * Math.pow(t, 2.0) * (1 - t)
        val d2 = toLocation.longitude * Math.pow(t, 3.0)
        val y = a2 + b2 + c2 + d2
        return LatLng(x, y)
    }

    private fun findArctg(lat1: Double, lat2: Double, lon1: Double, lon2: Double): Double {
        val arctg = Math.atan((lat2 - lat1) / (lon2 - lon1))
        return Math.toDegrees(arctg)
    }

    /* Apply rotation matrix to the point(x,y)
    * x, y: point that should be rotated;
    * x0, y0: point that should be center of rotation
    * angle: angle of rotation
    * */
    private fun rotatePoint(x: Double, y: Double, x0: Double, y0: Double, angle: Double): Array<Double> {
        val x1 = -(y - y0) * Math.sin(angle) + Math.cos(angle) * (x - x0) + x0
        val y1 = (y - y0) * +Math.cos(angle) + Math.sin(angle) * (x - x0) + y0
        return arrayOf(x1, y1)
    }

    /*
     * length of vector
     */
    private fun module(lat1: Double, lat2: Double, lon1: Double, lon2: Double): Double {
        return Math.sqrt((lat2 - lat1) * (lat2 - lat1) + (lon2 - lon1) * (lon2 - lon1))
    }


}