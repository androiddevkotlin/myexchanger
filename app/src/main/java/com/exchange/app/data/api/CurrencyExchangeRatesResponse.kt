package com.exchange.app.data.api

import androidx.annotation.Keep

@Keep
data class CurrencyExchangeRatesResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
)
