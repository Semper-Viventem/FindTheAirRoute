package ru.semper_viventem.findtheairroute.domain

import io.reactivex.Single

class FindCitiesByNameInteractor {

    // TODO implement it
    fun execute(): Single<List<City>> {
        return Single.just(
            (0..10).map {
                City(
                    cityName = "Moscow - $it",
                    countryName = "Russia",
                    fullName = "Moscow $it, Russia"
                )
            }
        )
    }
}