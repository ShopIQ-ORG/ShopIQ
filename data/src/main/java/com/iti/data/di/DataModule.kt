package com.iti.data.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.iti.data.repository.UserPreferencesRepositoryImpl
import com.iti.domain.repository.UserPreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().preferencesDataStoreFile("shopiq_preferences") }
        )
    }

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(get())
    }
}
