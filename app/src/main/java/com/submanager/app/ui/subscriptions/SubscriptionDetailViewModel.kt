package com.submanager.app.ui.subscriptions

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submanager.app.core.pdf.InvoiceGenerator
import com.submanager.app.core.share.ShareUtils
import com.submanager.app.core.util.DateUtils
import com.submanager.app.core.util.Money
import com.submanager.app.data.local.entity.CustomerEntity
import com.submanager.app.data.local.entity.RenewalEntity
import com.submanager.app.data.local.entity.SubscriptionEntity
import com.submanager.app.data.prefs.BusinessPrefs
import com.submanager.app.data.repository.CustomerRepository
import com.submanager.app.data.repository.RenewalRepository
import com.submanager.app.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionDetailViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val customerRepository: CustomerRepository,
    private val renewalRepository: RenewalRepository,
    private val invoiceGenerator: InvoiceGenerator,
    private val shareUtils: ShareUtils,
    private val businessPrefs: BusinessPrefs,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val id: Long = savedStateHandle.get<Long>("id") ?: -1L

    val subscription: StateFlow<SubscriptionEntity?> = subscriptionRepository.observeById(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val renewalHistory: StateFlow<List<RenewalEntity>> =
        renewalRepository.observeForSubscription(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun renew(days: Int, amount: Double, cost: Double) = viewModelScope.launch {
        renewalRepository.renew(id, days, amount, cost)
    }

    private suspend fun customer(sub: SubscriptionEntity): CustomerEntity? =
        customerRepository.getById(sub.customerId)

    /** Generates a PDF invoice and opens the share sheet (or WhatsApp). */
    fun shareInvoice(context: Context, viaWhatsApp: Boolean, includeLogin: Boolean = false) =
        viewModelScope.launch {
            val sub = subscription.value ?: return@launch
            val cust = customer(sub) ?: return@launch
            val business = businessPrefs.get()
            val number = businessPrefs.nextInvoiceNumber()
            val file = invoiceGenerator.generate(number, business, cust, sub, includeLogin)
            shareUtils.shareFile(
                context = context,
                file = file,
                text = "Invoice $number for ${cust.name}",
                whatsAppOnly = viaWhatsApp
            )
        }

    /** Sends the login/credential details as a WhatsApp text message. */
    fun sendLoginViaWhatsApp(context: Context) = viewModelScope.launch {
        val sub = subscription.value ?: return@launch
        val cust = customer(sub) ?: return@launch
        val business = businessPrefs.get()
        val msg = buildString {
            appendLine("Hi ${cust.name},")
            appendLine()
            appendLine("Here are your ${sub.productName} details:")
            appendLine("Login Email: ${sub.loginEmail ?: "-"}")
            appendLine("Password: ${sub.loginPasswordEnc ?: "-"}")
            sub.pinEnc?.let { appendLine("PIN: $it") }
            appendLine("Expiry: ${DateUtils.format(sub.expiryDate)}")
            appendLine("Amount: ${Money.format(sub.sellingPrice, business.currency)}")
            appendLine()
            append("- ${business.name}")
        }
        shareUtils.whatsAppText(context, cust.whatsapp ?: cust.phone, msg)
    }
}
