package com.submanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.submanager.app.domain.model.PaymentStatus
import com.submanager.app.domain.model.SubscriptionStatus

/**
 * A sold subscription/account linked to a customer and product.
 * Sensitive fields (loginPassword, pin, backupCodes) are stored ENCRYPTED.
 */
@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val productId: Long? = null,
    val productName: String,
    val productCategory: String? = null,

    // Credentials (encrypted at field level)
    val loginEmail: String? = null,
    val loginPasswordEnc: String? = null,
    val recoveryEmail: String? = null,
    val recoveryPhone: String? = null,
    val backupCodesEnc: String? = null,
    val pinEnc: String? = null,

    // Lifecycle dates (epoch millis)
    val purchaseDate: Long? = null,
    val activationDate: Long? = null,
    val expiryDate: Long? = null,
    val validityDays: Int? = null,

    // Warranty
    val hasWarranty: Boolean = false,
    val warrantyExpiry: Long? = null,

    // Commercials
    val sellerName: String? = null,
    val costPrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PAID,
    val paymentMethod: String? = null,
    val amountPaid: Double = 0.0,

    val notes: String? = null,
    val tags: List<String> = emptyList(),
    val customFields: Map<String, String> = emptyMap(),
    val attachments: List<String> = emptyList(),

    val status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
    val isTrashed: Boolean = false,
    val trashedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val profit: Double get() = sellingPrice - costPrice
    val dueAmount: Double get() = (sellingPrice - amountPaid).coerceAtLeast(0.0)
}
