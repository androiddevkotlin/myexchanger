package com.exchange.app.domain.repository

import com.exchange.app.domain.entity.Balance
import com.exchange.app.domain.entity.User
import com.exchange.app.domain.model.SubmitState
import kotlinx.coroutines.flow.Flow

interface CurrencyExchangeRepository {

    fun isConnect(): Boolean

    suspend fun fetchCurrencyRates(): Boolean
    fun getAllCurrencyTypes(): Flow<List<String>>

    suspend fun isBalancesEmpty(): Boolean
    suspend fun saveBalance(balance: Balance)
    fun getAllBalances(): Flow<List<Balance>>
    suspend fun getBalance(currencyType: String): Balance?

    suspend fun isUsersEmpty(): Boolean
    suspend fun saveUser(user: User)

    suspend fun calculateReceiveAmount(
        sellAmount: Double,
        receiveCurrencyType: String,
        sellCurrencyType: String,
    ): Double

    suspend fun submitExchange(sellMoney: Balance, receiveMoney: Balance): SubmitState
}
