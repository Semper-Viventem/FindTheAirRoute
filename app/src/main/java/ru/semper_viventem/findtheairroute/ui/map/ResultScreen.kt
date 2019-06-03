package ru.semper_viventem.findtheairroute.ui.map

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import kotlinx.android.synthetic.main.screen_result.view.*
import org.koin.android.ext.android.getKoin
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.ui.UIModule
import ru.semper_viventem.findtheairroute.ui.common.MapScreen


class ResultScreen : MapScreen<ResultPm>() {

    companion object {
        private const val AIRPLANE_ANIMATION_DURATION = 7000L
        private const val MARKER_RES_ID = R.drawable.ic_plane
        private const val MARKER_ROTATION = -90

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
        routeAnimationDelegate = RouteAnimationDelegate(
            context = context!!,
            imageRes = MARKER_RES_ID,
            animationDuration = AIRPLANE_ANIMATION_DURATION,
            markerRotation = MARKER_ROTATION
        )
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
        with(routeAnimationDelegate!!) {
            val progress = animationPosition to animationEnd
            progress passTo presentationModel.saveProgress
            destroy()
        }
        routeAnimationDelegate = null
        super.onDestroy()
    }

    override fun onInitMap(googleMap: GoogleMap) {
        // do nothing
    }

    override fun onBindPresentationModel(view: View, pm: ResultPm) {
        // do nothing
    }

    override fun onBindMapPresentationModel(pm: ResultPm, googleMap: GoogleMap) {

        pm.state bindTo { state ->
            if (!routeAnimationDelegate!!.inProgress()) {

                routeAnimationDelegate!!.start(
                    googleMap = googleMap,
                    fromCity = state.from,
                    toCity = state.to,
                    begin = state.startPosition,
                    end = state.endPosition
                )
            }
        }
    }
}