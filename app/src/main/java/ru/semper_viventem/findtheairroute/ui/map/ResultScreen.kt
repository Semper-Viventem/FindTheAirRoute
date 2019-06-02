package ru.semper_viventem.findtheairroute.ui.map

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.screen_result.view.*
import org.koin.android.ext.android.getKoin
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.domain.Location
import ru.semper_viventem.findtheairroute.extensions.toLatLng
import ru.semper_viventem.findtheairroute.ui.UIModule
import ru.semper_viventem.findtheairroute.ui.common.MapScreen


class ResultScreen : MapScreen<ResultPm>() {

    companion object {
        private const val AIRPLANE_ANIMATION_DURATION = 7000L

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
    private var routeAnimationDelegate: RouteAnimationDelegate? = null

    override fun provideMapFragmentId(view: View): MapView = view.map

    override fun providePresentationModel(): ResultPm = getKoin()
        .apply {
            setProperty(UIModule.PROPERTY_FROM_CITY, arguments!!.getSerializable(ARG_FROM_CITY))
            setProperty(UIModule.PROPERTY_TO_CITY, arguments!!.getSerializable(ARG_TO_CITY))
        }
        .get()

    override fun onInitView(view: View, savedInstanceState: Bundle?) {
        super.onInitView(view, savedInstanceState)
        routeAnimationDelegate = RouteAnimationDelegate(resources, R.drawable.ic_plane, AIRPLANE_ANIMATION_DURATION)
    }

    override fun onResume() {
        super.onResume()
        routeAnimationDelegate?.resume()
    }

    override fun onPause() {
        routeAnimationDelegate?.pause()
        super.onPause()
    }

    override fun onDestroy() {
        routeAnimationDelegate?.stop()
        super.onDestroy()
    }

    override fun onInitMap(googleMap: GoogleMap) {
        // do nothing
    }

    override fun onBindPresentationModel(view: View, pm: ResultPm) {
        // do nothing
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

            if (!routeAnimationDelegate!!.inProgress()) {
                routeAnimationDelegate!!.start(googleMap, fromCity.location.toLatLng(), toCity.location.toLatLng())
            }
        }
    }

    private fun addMarker(map: GoogleMap, location: Location, title: String): Marker {
        val marker = MarkerOptions()
            .position(location.toLatLng())
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            .title(title)

        return map.addMarker(marker)
    }
}