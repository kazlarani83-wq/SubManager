package com.submanager.app.data.local.dao

import androidx.room.*
import com.submanager.app.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun observeForCustomer(customerId: Long): Flow<List<PaymentEntity>>

    @Insert suspend fun insert(payment: PaymentEntity): Long
    @Delete suspend fun delete(payment: PaymentEntity)
}
