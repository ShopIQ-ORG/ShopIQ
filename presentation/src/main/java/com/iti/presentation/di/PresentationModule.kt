package com.iti.presentation.di

import com.iti.presentation.productdetails.ProductDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { ProductDetailsViewModel(get()) }
}
