package com.iti.shopiq.di

import com.iti.data.di.dataModule
import com.iti.domain.di.domainModule
import com.iti.presentation.di.presentationModule
import org.koin.dsl.module

val appModule = module {

}

val appModules = listOf(
    appModule,
    domainModule,
    dataModule,
    presentationModule
)
