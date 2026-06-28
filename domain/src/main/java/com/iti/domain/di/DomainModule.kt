package com.iti.domain.di

import com.iti.domain.usecases.auth.LoginAsGuestUseCase
import com.iti.domain.usecases.auth.LoginUseCase
import com.iti.domain.usecases.auth.LoginWithFacebookUseCase
import com.iti.domain.usecases.auth.LoginWithGoogleUseCase
import com.iti.domain.usecases.auth.RegisterUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { LoginWithGoogleUseCase(get()) }
    factory { LoginWithFacebookUseCase(get()) }
    factory { LoginAsGuestUseCase(get()) }
    factory { RegisterUseCase(get()) }
}
