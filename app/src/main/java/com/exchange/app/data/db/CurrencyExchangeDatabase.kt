package com.exchange.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.exchange.app.domain.entity.Balance
import com.exchange.app.domain.entity.CurrencyRate
import com.exchange.app.domain.entity.User

@Database(
    entities = [
        Balance::class,
        CurrencyRate::class,
        User::class
    ],
    version = CurrencyExchangeDatabase.DATABASE_VERSION
)
abstract class CurrencyExchangeDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "currency_exchange_database"
    }

    abstract fun getBalanceDao(): BalanceDao
    abstract fun getCurrencyDao(): CurrencyRateDao
    abstract fun getUserDao(): UserDao
}
