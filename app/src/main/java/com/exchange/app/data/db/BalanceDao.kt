package com.exchange.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exchange.app.domain.entity.Balance
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {

    @Query("SELECT (SELECT COUNT(*) FROM Balance) == 0")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: Balance)

    @Query("SELECT * FROM Balance ORDER BY amount DESC")
    fun getAllBalances(): Flow<List<Balance>>

    @Query("SELECT * FROM Balance WHERE currencyType = :currencyType ")
    suspend fun getBalance(currencyType: String): Balance?
}
