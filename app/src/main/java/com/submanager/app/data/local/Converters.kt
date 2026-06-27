package com.submanager.app.data.local

import androidx.room.TypeConverter
import com.submanager.app.domain.model.PaymentStatus
import com.submanager.app.domain.model.SubscriptionStatus

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String = value?.joinToString("|||") ?: ""

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        if (value.isNullOrEmpty()) emptyList() else value.split("|||")

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String =
        value?.entries?.joinToString("|||") { "${it.key}===${it.value}" } ?: ""

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String> {
        if (value.isNullOrEmpty()) return emptyMap()
        return value.split("|||").mapNotNull {
            val parts = it.split("===")
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()
    }

    @TypeConverter fun fromPaymentStatus(v: PaymentStatus): String = v.name
    @TypeConverter fun toPaymentStatus(v: String): PaymentStatus = PaymentStatus.valueOf(v)

    @TypeConverter fun fromSubStatus(v: SubscriptionStatus): String = v.name
    @TypeConverter fun toSubStatus(v: String): SubscriptionStatus = SubscriptionStatus.valueOf(v)
}
