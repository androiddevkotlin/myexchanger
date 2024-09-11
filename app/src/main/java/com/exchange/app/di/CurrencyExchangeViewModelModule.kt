package com.exchange.app.di

import androidx.lifecycle.ViewModel
import com.exchange.app.core.di.ViewModelKey
import com.exchange.app.presentation.ExchangeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CurrencyExchangeViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ExchangeViewModel::class)

    fun provideCurrencyExchangeViewModel(currencyExchangeViewModel: ExchangeViewModel): ViewModel
}
