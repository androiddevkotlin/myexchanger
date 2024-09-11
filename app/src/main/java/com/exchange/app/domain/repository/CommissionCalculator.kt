package com.exchange.app.domain.repository

import com.exchange.app.domain.entity.Balance

interface CommissionCalculator {

    suspend fun calcCommission(sellMoney: Balance): Balance
}
