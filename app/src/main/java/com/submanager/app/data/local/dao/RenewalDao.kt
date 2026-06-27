package com.submanager.app.data.local.dao

import androidx.room.*
import com.submanager.app.data.local.entity.RenewalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RenewalDao {
    @Query("SELECT * FROM renewals ORDER BY renewedAt DESC")
    fun observeAll(): Flow<List<RenewalEntity>>

    @Query("SELECT * FROM renewals WHERE subscriptionId = :subId ORDER BY renewedAt DESC")
    fun observeForSubscription(subId: Long): Flow<List<RenewalEntity>>

    @Insert suspend fun insert(renewal: RenewalEntity): Long
}
