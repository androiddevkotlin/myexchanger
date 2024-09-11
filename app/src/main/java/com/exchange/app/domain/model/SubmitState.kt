package com.exchange.app.domain.model

import com.exchange.app.domain.entity.Balance

sealed class SubmitState(
    val type: String,
) {
    data class Success(
        val sellMoney: Balance,
        val receiveMoney: Balance,
        val commission: Balance,
    ) : SubmitState(
        type = "Currency converted"
    )

    data class SmallAmount(
        val sellMoney: Balance,
        val storageSellBalance: Balance,
    ) : SubmitState(
        type = "Error",
    )

    data class NoBalanceType(
        val sellMoney: Balance,
    ) : SubmitState(
        type = "Error",
    )

    data class SameType(
        val sellMoney: Balance,
    ) : SubmitState(
        type = "Error",
    )

    class NoTypes : SubmitState(
        type = "Error",
    )
}
