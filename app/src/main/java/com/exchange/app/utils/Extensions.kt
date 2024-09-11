package com.exchange.app.utils

import android.widget.Spinner
import com.exchange.app.domain.entity.CurrencyRate

infix fun <T> Collection<T>.deepEqualTo(other: Collection<T>): Boolean {
    if (this !== other) {
        if (this.size != other.size) return false
        val areNotEqual = this.asSequence()
            .zip(other.asSequence())
            .map { (fromThis, fromOther) -> fromThis == fromOther }
            .contains(false)
        if (areNotEqual) return false
    }
    return true
}

fun Map<String, Double>.parseToCurrencyRateList(): List<CurrencyRate> {
    return this.map { entryCurrencyRate ->
        CurrencyRate(entryCurrencyRate.key, entryCurrencyRate.value)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Spinner.getList(): List<T> {
    val list: List<T>
    if (adapter != null) {
        val n: Int = adapter.count
        list = ArrayList(n)
        for (i in 0 until n) {
            val currencyType = adapter.getItem(i) as T
            list.add(currencyType)
        }
    } else {
        list = listOf()
    }
    return list
}
