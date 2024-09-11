package com.exchange.app.utils

import com.exchange.app.domain.entity.Balance


operator fun Balance.plus(money: Balance): Balance {
    if (this.currencyType == money.currencyType) {
        return apply { amount += money.amount }
    } else {
        throw Exception("Balances have different currencyTypes!")
    }
}

operator fun Balance.minus(money: Balance): Balance {
    if (this.currencyType == money.currencyType) {
        return apply { amount -= money.amount }
    } else {
        throw Exception("Balances have different currencyTypes!")
    }
}

fun Balance.amountAndCurrencyText(): String {
    return String.format("%1\$.2f %2\$s", amount, currencyType)
}
