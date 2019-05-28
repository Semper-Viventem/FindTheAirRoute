package ru.semper_viventem.findtheairroute.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.base.MapPmSupportFragment


abstract class MapScreen<PM> : MapPmSupportFragment<PM>(), OnMapReadyCallback, BackHandler where PM : MapPmExtension, PM : ScreenPm {

    protected abstract val screenLayout: Int

    protected abstract fun provideMapView(): Int

    abstract fun onInitMap(googleMap: GoogleMap)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(screenLayout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView(view, savedInstanceState)
    }

    /**
     * Use for views initialisation.
     */
    protected open fun onInitView(view: View, savedViewState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(provideMapView()) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        onInitMap(googleMap!!)
    }

    final override fun onBindPresentationModel(pm: PM) {
        onBindPresentationModel(view!!, pm)
    }

    abstract fun onBindPresentationModel(view: View, pm: PM)

    override fun handleBack(): Boolean {
        passTo(presentationModel.backAction.consumer)
        return true
    }
}