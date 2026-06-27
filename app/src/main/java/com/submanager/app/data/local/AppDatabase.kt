package com.submanager.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.submanager.app.data.local.dao.CustomerDao
import com.submanager.app.data.local.dao.PaymentDao
import com.submanager.app.data.local.dao.ProductDao
import com.submanager.app.data.local.dao.RenewalDao
import com.submanager.app.data.local.dao.SubscriptionDao
import com.submanager.app.data.local.entity.CustomerEntity
import com.submanager.app.data.local.entity.PaymentEntity
import com.submanager.app.data.local.entity.ProductEntity
import com.submanager.app.data.local.entity.RenewalEntity
import com.submanager.app.data.local.entity.SubscriptionEntity

@Database(
    entities = [
        CustomerEntity::class,
        ProductEntity::class,
        SubscriptionEntity::class,
        RenewalEntity::class,
        PaymentEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun renewalDao(): RenewalDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        const val NAME = "submanager.db"
    }
}
