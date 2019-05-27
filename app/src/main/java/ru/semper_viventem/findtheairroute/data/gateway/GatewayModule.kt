package ru.semper_viventem.findtheairroute.data.gateway

import org.koin.dsl.bind
import org.koin.dsl.module
import ru.semper_viventem.findtheairroute.domain.CitiesGateway

object GatewayModule {
    val module = module {
        single { CitiesGatewayImpl(get()) } bind CitiesGateway::class
    }
}