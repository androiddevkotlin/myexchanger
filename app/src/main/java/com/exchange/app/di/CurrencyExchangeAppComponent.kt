package com.exchange.app.di

import android.content.Context
import com.exchange.app.core.di.ViewModelFactoryModule
import com.exchange.app.presentation.ExchangeFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        ViewModelFactoryModule::class,
        CurrencyExchangeNetworkModule::class,
        CurrencyExchangeDataModule::class,
        CurrencyExchangeViewModelModule::class,
    ]
)
interface CurrencyExchangeAppComponent {

    fun injectFragment(fragment: ExchangeFragment)

    @Component.Factory
    interface AppComponentFactory {
        fun create(@BindsInstance @ApplicationContext context: Context): CurrencyExchangeAppComponent
    }
}
