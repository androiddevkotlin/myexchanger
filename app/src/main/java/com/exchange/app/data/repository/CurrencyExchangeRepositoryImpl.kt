package com.exchange.app.data.repository


import com.exchange.app.data.api.CurrencyExchangeApi
import com.exchange.app.data.db.BalanceDao
import com.exchange.app.data.db.CurrencyRateDao
import com.exchange.app.data.db.UserDao
import com.exchange.app.domain.entity.Balance
import com.exchange.app.domain.entity.CurrencyRate
import com.exchange.app.domain.entity.User
import com.exchange.app.domain.model.SubmitState
import com.exchange.app.domain.repository.CommissionCalculator
import com.exchange.app.domain.repository.CurrencyExchangeRepository
import com.exchange.app.utils.minus
import com.exchange.app.utils.parseToCurrencyRateList
import com.exchange.app.utils.plus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class CurrencyExchangeRepositoryImpl(
    private val currencyExchangeApi: CurrencyExchangeApi,
    private val connectivityChecker: ConnectivityChecker,
    private val balanceDao: BalanceDao,
    private val currencyRateDao: CurrencyRateDao,
    private val userDao: UserDao,
    private val commissionCalculator: CommissionCalculator,
) : CurrencyExchangeRepository {

    override fun isConnect(): Boolean = connectivityChecker.isConnect()

    override suspend fun fetchCurrencyRates(): Boolean {
        if (isConnect()) {
            val currencyRateList: List<CurrencyRate> = getCurrencyRates()
            saveCurrencyRates(currencyRateList)
        }
        return isConnect()
    }

    private suspend fun getCurrencyRates(): List<CurrencyRate> {
        return currencyExchangeApi.fetchCurrencyRates().rates.parseToCurrencyRateList()
    }

    private suspend fun saveCurrencyRates(currencyRateList: List<CurrencyRate>) {
        currencyRateDao.insertCurrencyRates(currencyRateList)
    }

    override fun getAllCurrencyTypes(): Flow<List<String>> {
        return currencyRateDao.getAllCurrencyRates().map { currencyRateList ->
            currencyRateList.map { currencyRate ->
                currencyRate.type
            }
        }
    }

    override suspend fun isBalancesEmpty(): Boolean {
        return balanceDao.isEmpty()
    }

    override suspend fun saveBalance(balance: Balance) {
        balanceDao.insertBalance(balance)
    }

    override fun getAllBalances(): Flow<List<Balance>> {
        return balanceDao.getAllBalances()
    }

    override suspend fun getBalance(currencyType: String): Balance? {
        return balanceDao.getBalance(currencyType)
    }

    override suspend fun isUsersEmpty(): Boolean {
        return userDao.isEmpty()
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(user)
    }

    override suspend fun calculateReceiveAmount(
        sellAmount: Double,
        receiveCurrencyType: String,
        sellCurrencyType: String,
    ): Double {
        val receiveRate = getRate(receiveCurrencyType)
        val sellRate = getRate(sellCurrencyType)
        return sellAmount * sellRate / receiveRate
    }

    private suspend fun getRate(type: String): Double {
        return currencyRateDao.getCurrencyRate(type).rate
    }

    override suspend fun submitExchange(
        sellMoney: Balance,
        receiveMoney: Balance,
    ): SubmitState {
        if (sellMoney.currencyType == receiveMoney.currencyType) {
            return SubmitState.SameType(sellMoney)
        }
        val storageSellBalance =
            getBalance(sellMoney.currencyType) ?: return SubmitState.NoBalanceType(sellMoney)

        val commission = commissionCalculator.calcCommission(
            sellMoney
        )
        val commissionWithSellMoney = sellMoney + commission

        if (storageSellBalance.amount < (commissionWithSellMoney).amount) {
            return SubmitState.SmallAmount(commissionWithSellMoney, storageSellBalance)
        }

        saveBalance(storageSellBalance - commissionWithSellMoney)
        val storageReceiveBalance = getBalance(receiveMoney.currencyType)
        if (storageReceiveBalance == null) {
            saveBalance(receiveMoney)
        } else {
            saveBalance(storageReceiveBalance + receiveMoney)
        }
        return SubmitState.Success(sellMoney, receiveMoney, commission)
    }
}

