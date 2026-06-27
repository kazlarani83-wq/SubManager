package com.submanager.app.data.local.dao

import androidx.room.*
import com.submanager.app.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers WHERE isTrashed = 0 AND isArchived = 0 ORDER BY isPinned DESC, name ASC")
    fun observeActive(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE isTrashed = 1 ORDER BY trashedAt DESC")
    fun observeTrashed(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun observeById(id: Long): Flow<CustomerEntity?>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getById(id: Long): CustomerEntity?

    @Query("SELECT COUNT(*) FROM customers WHERE isTrashed = 0 AND isArchived = 0")
    fun countActive(): Flow<Int>

    @Query("""SELECT * FROM customers WHERE isTrashed = 0 AND (
        name LIKE '%' || :q || '%' OR phone LIKE '%' || :q || '%' OR
        whatsapp LIKE '%' || :q || '%' OR email LIKE '%' || :q || '%')
        ORDER BY name ASC""")
    fun search(q: String): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(customer: CustomerEntity): Long

    @Update suspend fun update(customer: CustomerEntity)
    @Delete suspend fun delete(customer: CustomerEntity)

    @Query("UPDATE customers SET isTrashed = 1, trashedAt = :ts, updatedAt = :ts WHERE id = :id")
    suspend fun moveToTrash(id: Long, ts: Long = System.currentTimeMillis())

    @Query("UPDATE customers SET isTrashed = 0, trashedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM customers WHERE isTrashed = 1 AND trashedAt < :cutoff")
    suspend fun purgeOldTrash(cutoff: Long)
}
