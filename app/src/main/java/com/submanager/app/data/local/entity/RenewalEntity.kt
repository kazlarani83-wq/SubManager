package com.submanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "renewals")
data class RenewalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subscriptionId: Long,
    val customerId: Long,
    val previousExpiry: Long? = null,
    val newExpiry: Long? = null,
    val validityDays: Int? = null,
    val amount: Double = 0.0,
    val cost: Double = 0.0,
    val renewedAt: Long = System.currentTimeMillis(),
    val note: String? = null
) {
    val profit: Double get() = amount - cost
}
