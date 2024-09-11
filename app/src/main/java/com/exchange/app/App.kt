package com.exchange.app

import android.app.Application
import android.content.pm.ApplicationInfo
import com.exchange.app.di.CurrencyExchangeAppComponent
import com.exchange.app.di.DaggerCurrencyExchangeAppComponent
import timber.log.Timber

class App : Application() {

    lateinit var appComponent: CurrencyExchangeAppComponent

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        appComponent = DaggerCurrencyExchangeAppComponent.factory()
            .create(this)
    }

    private fun setupTimber() {
        val isDebuggable = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        if (isDebuggable) Timber.plant(Timber.DebugTree())
    }

}