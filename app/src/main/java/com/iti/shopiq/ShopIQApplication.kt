package com.iti.shopiq

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import com.iti.data.di.dataModule
import com.iti.domain.di.domainModule
import com.iti.presentation.di.presentationModule
import com.iti.shopiq.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ShopIQApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ShopIQApplication)
            modules(
                appModule,
                domainModule,
                dataModule,
                presentationModule
            )
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}
