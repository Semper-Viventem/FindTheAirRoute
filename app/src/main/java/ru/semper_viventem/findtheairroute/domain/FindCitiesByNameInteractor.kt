package ru.semper_viventem.findtheairroute.domain

class FindCitiesByNameInteractor(
    private val citiesGateway: CitiesGateway
) {

    fun execute(query: String) = citiesGateway.findCity(query)
}