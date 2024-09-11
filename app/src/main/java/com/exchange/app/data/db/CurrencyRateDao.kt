package com.exchange.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exchange.app.domain.entity.CurrencyRate
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyRates(currencyRateList: List<CurrencyRate>)

    @Query("SELECT * FROM CurrencyRate ORDER BY type")
    fun getAllCurrencyRates(): Flow<List<CurrencyRate>>

    @Query("SELECT * FROM CurrencyRate WHERE type = :type")
    suspend fun getCurrencyRate(type: String): CurrencyRate
}
