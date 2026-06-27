package com.submanager.app.data.local.dao

import androidx.room.*
import com.submanager.app.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isTrashed = 0 AND isArchived = 0 ORDER BY name ASC")
    fun observeActive(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun observeById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): ProductEntity?

    @Query("SELECT COUNT(*) FROM products WHERE isTrashed = 0 AND isArchived = 0")
    fun countActive(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(product: ProductEntity): Long

    @Update suspend fun update(product: ProductEntity)

    @Query("UPDATE products SET isTrashed = 1, trashedAt = :ts WHERE id = :id")
    suspend fun moveToTrash(id: Long, ts: Long = System.currentTimeMillis())

    @Query("UPDATE products SET isTrashed = 0, trashedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)
}
