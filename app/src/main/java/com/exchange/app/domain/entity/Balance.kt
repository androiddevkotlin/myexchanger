package com.exchange.app.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Balance(
    @PrimaryKey var currencyType: String,
    var amount: Double,
)
