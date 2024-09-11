package com.exchange.app.data.repository

import com.exchange.app.data.db.UserDao
import com.exchange.app.domain.entity.Balance
import com.exchange.app.domain.repository.CommissionCalculator

private const val COMMISSION = 0.007
const val USER_NAME = "Venger"
const val COMMISSION_FREE_AMOUNT = 0.0

class CommissionCalculatorImpl(
    private val userDao: UserDao,
) : CommissionCalculator {

    override suspend fun calcCommission(sellMoney: Balance): Balance {
        val user = userDao.getUser(USER_NAME)
        val commissionAmount = if (user.counterFreeCommission >= 5) {
            COMMISSION * sellMoney.amount
        } else {
            COMMISSION_FREE_AMOUNT
        }
        user.counterFreeCommission++
        userDao.insertUser(user)
        return Balance(sellMoney.currencyType, commissionAmount)
    }
}
