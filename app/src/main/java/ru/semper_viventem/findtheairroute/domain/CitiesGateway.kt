package ru.semper_viventem.findtheairroute.domain

import io.reactivex.Single

interface CitiesGateway {

    fun findCity(query: String): Single<List<City>>
}