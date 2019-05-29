package ru.semper_viventem.findtheairroute

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.android.gms.maps.MapsInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.semper_viventem.findtheairroute.data.gateway.GatewayModule
import ru.semper_viventem.findtheairroute.data.network.NetworkModule
import ru.semper_viventem.findtheairroute.domain.InteractorModule
import ru.semper_viventem.findtheairroute.ui.UIModule
import timber.log.Timber

class TheApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initDI()
        initLog()
        initStetho()
        initMaps()
    }

    private fun initMaps() {
        MapsInitializer.initialize(this)
    }

    private fun initDI() {
        startKoin {
            androidContext(this@TheApplication)
            androidLogger()
            modules(
                UIModule.module,
                GatewayModule.module,
                InteractorModule.module,
                NetworkModule.module
            )
        }
    }

    private fun initLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
}