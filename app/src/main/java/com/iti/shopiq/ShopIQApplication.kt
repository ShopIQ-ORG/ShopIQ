package com.iti.shopiq

import android.app.Application
import com.iti.shopiq.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ShopIQApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ShopIQApplication)
            modules(appModules)
        }
    }
}
