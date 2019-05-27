package ru.semper_viventem.findtheairroute.data.gateway

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.semper_viventem.findtheairroute.data.network.Api
import ru.semper_viventem.findtheairroute.domain.CitiesGateway
import ru.semper_viventem.findtheairroute.domain.City

class CitiesGatewayImpl(
    private val api: Api
) : CitiesGateway {

    companion object {
        private const val DEFAULT_LOCALE = "ru" // todo use locale from device
    }

    override fun findCity(query: String): Single<List<City>> {
        return api.getCities(query,
            DEFAULT_LOCALE
        )
            .subscribeOn(Schedulers.io())
            .map { it.cities }
    }
}