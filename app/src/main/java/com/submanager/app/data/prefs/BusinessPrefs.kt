package com.submanager.app.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "business_prefs")

data class BusinessInfo(
    val name: String = "My Subscription Store",
    val address: String = "",
    val phone: String = "",
    val currency: String = "$"
)

@Singleton
class BusinessPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val NAME = stringPreferencesKey("biz_name")
        val ADDRESS = stringPreferencesKey("biz_address")
        val PHONE = stringPreferencesKey("biz_phone")
        val CURRENCY = stringPreferencesKey("biz_currency")
        val INVOICE_COUNTER = intPreferencesKey("invoice_counter")
    }

    val businessInfo: Flow<BusinessInfo> = context.dataStore.data.map { p ->
        BusinessInfo(
            name = p[Keys.NAME] ?: "My Subscription Store",
            address = p[Keys.ADDRESS] ?: "",
            phone = p[Keys.PHONE] ?: "",
            currency = p[Keys.CURRENCY] ?: "$"
        )
    }

    suspend fun get(): BusinessInfo = businessInfo.first()

    suspend fun save(info: BusinessInfo) {
        context.dataStore.edit { p ->
            p[Keys.NAME] = info.name
            p[Keys.ADDRESS] = info.address
            p[Keys.PHONE] = info.phone
            p[Keys.CURRENCY] = info.currency
        }
    }

    /** Atomically increments and returns the next invoice number e.g. INV-00007. */
    suspend fun nextInvoiceNumber(): String {
        var next = 1
        context.dataStore.edit { p ->
            next = (p[Keys.INVOICE_COUNTER] ?: 0) + 1
            p[Keys.INVOICE_COUNTER] = next
        }
        return "INV-" + next.toString().padStart(5, '0')
    }
}
