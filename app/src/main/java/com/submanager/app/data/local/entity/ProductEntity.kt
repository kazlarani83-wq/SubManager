package com.submanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String? = null,
    val availableAccounts: Int = 0,
    val soldAccounts: Int = 0,
    val lowStockThreshold: Int = 3,
    val defaultCostPrice: Double = 0.0,
    val defaultSellingPrice: Double = 0.0,
    val notes: String? = null,
    val isArchived: Boolean = false,
    val isTrashed: Boolean = false,
    val trashedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val remainingStock: Int get() = (availableAccounts - soldAccounts).coerceAtLeast(0)
    val isLowStock: Boolean get() = remainingStock in 1..lowStockThreshold
    val isOutOfStock: Boolean get() = remainingStock <= 0
}
