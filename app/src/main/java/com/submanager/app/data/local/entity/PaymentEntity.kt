package com.submanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long? = null,
    val subscriptionId: Long? = null,
    val type: String = "INCOME", // INCOME | EXPENSE | REFUND
    val amount: Double = 0.0,
    val method: String? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
