package com.exchange.app.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyRate(
    @PrimaryKey val type: String,
    val rate: Double,
)
