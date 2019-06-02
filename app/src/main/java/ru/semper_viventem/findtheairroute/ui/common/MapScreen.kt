package ru.semper_viventem.findtheairroute.ui.common

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback


abstract class MapScreen<PM> : Screen<PM>(), OnMapReadyCallback where PM : MapScreenPm {

    protected abstract fun provideMapFragmentId(view: View): MapView

    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null

    abstract fun onInitMap(googleMap: GoogleMap)

    /**
     * Use for views initialisation.
     */
    override fun onInitView(view: View, savedInstanceState: Bundle?) {
        mapView = provideMapFragmentId(view)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        onInitMap(googleMap!!)
        true passTo presentationModel.mapReadyAction.consumer
    }

    abstract fun onBindMapPresentationModel(pm: PM, googleMap: GoogleMap)

    final override fun onBindPresentationModel(pm: PM) {
        onBindPresentationModel(view!!, pm)
        pm.mapReady.observable
            .filter { it }
            .bindTo { onBindMapPresentationModel(pm, googleMap!!) }
    }

    abstract fun onBindPresentationModel(view: View, pm: PM)

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        mapView = null
        googleMap = null
        super.onDestroy()
    }
}