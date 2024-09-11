package com.exchange.app.di

import android.content.Context
import androidx.room.Room
import com.exchange.app.data.api.CurrencyExchangeApi
import com.exchange.app.data.db.BalanceDao
import com.exchange.app.data.db.CurrencyExchangeDatabase
import com.exchange.app.data.db.CurrencyRateDao
import com.exchange.app.data.db.UserDao
import com.exchange.app.data.repository.CommissionCalculatorImpl
import com.exchange.app.data.repository.ConnectivityChecker
import com.exchange.app.data.repository.CurrencyExchangeRepositoryImpl
import com.exchange.app.domain.repository.CommissionCalculator
import com.exchange.app.domain.repository.CurrencyExchangeRepository
import dagger.Module
import dagger.Provides

@Module
class CurrencyExchangeDataModule {

    @Provides
    fun provideCurrencyExchangeDatabase(@ApplicationContext context: Context): CurrencyExchangeDatabase {
        return Room.databaseBuilder(
            context,
            CurrencyExchangeDatabase::class.java,
            CurrencyExchangeDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideCurrencyExchangeRepository(
        currencyExchangeApi: CurrencyExchangeApi,
        connectivityChecker: ConnectivityChecker,
        balanceDao: BalanceDao,
        currencyRateDao: CurrencyRateDao,
        userDao: UserDao,
        commissionCalculator: CommissionCalculator,
    ): CurrencyExchangeRepository {
        return CurrencyExchangeRepositoryImpl(
            currencyExchangeApi,
            connectivityChecker,
            balanceDao,
            currencyRateDao,
            userDao,
            commissionCalculator
        )
    }

    @Provides
    fun provideConnectivityChecker(@ApplicationContext context: Context): ConnectivityChecker {
        return ConnectivityChecker(context)
    }

    @Provides
    fun provideCommissionCalculator(
        userDao: UserDao,
    ): CommissionCalculator {
        return CommissionCalculatorImpl(userDao)
    }

    @Provides
    fun provideBalanceDao(currencyExchangeDatabase: CurrencyExchangeDatabase) =
        currencyExchangeDatabase.getBalanceDao()

    @Provides
    fun provideCurrencyDao(currencyExchangeDatabase: CurrencyExchangeDatabase) =
        currencyExchangeDatabase.getCurrencyDao()

    @Provides
    fun provideUserDao(currencyExchangeDatabase: CurrencyExchangeDatabase) =
        currencyExchangeDatabase.getUserDao()
}
