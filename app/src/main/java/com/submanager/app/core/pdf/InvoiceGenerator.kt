package com.submanager.app.core.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import com.submanager.app.core.qr.QrGenerator
import com.submanager.app.core.util.DateUtils
import com.submanager.app.core.util.Money
import com.submanager.app.data.local.entity.CustomerEntity
import com.submanager.app.data.local.entity.SubscriptionEntity
import com.submanager.app.data.prefs.BusinessInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Renders a single-page A4 PDF invoice using the platform PdfDocument API (no external lib).
 * Returns the saved [File] in the app cache so it can be shared via FileProvider.
 */
@Singleton
class InvoiceGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val qrGenerator: QrGenerator
) {
    private val pageWidth = 595   // A4 @72dpi
    private val pageHeight = 842
    private val margin = 40f

    fun generate(
        invoiceNumber: String,
        business: BusinessInfo,
        customer: CustomerEntity,
        sub: SubscriptionEntity,
        includeLogin: Boolean = false
    ): File {
        val doc = PdfDocument()
        val page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
        val c = page.canvas

        val title = Paint().apply { color = Color.parseColor("#4F46E5"); textSize = 26f; isFakeBoldText = true }
        val h = Paint().apply { color = Color.BLACK; textSize = 13f; isFakeBoldText = true }
        val p = Paint().apply { color = Color.DKGRAY; textSize = 12f }
        val light = Paint().apply { color = Color.GRAY; textSize = 11f }
        val line = Paint().apply { color = Color.parseColor("#E2E8F0"); strokeWidth = 1f }
        val cur = business.currency

        var y = margin + 10

        // Header
        c.drawText(business.name, margin, y, title); y += 22
        if (business.address.isNotBlank()) { c.drawText(business.address, margin, y, light); y += 16 }
        if (business.phone.isNotBlank()) { c.drawText("Tel: ${business.phone}", margin, y, light); y += 16 }

        // Invoice meta (right)
        c.drawText("INVOICE", pageWidth - margin - 120, margin + 10, h)
        c.drawText(invoiceNumber, pageWidth - margin - 120, margin + 30, p)
        c.drawText("Date: ${DateUtils.format(System.currentTimeMillis())}", pageWidth - margin - 120, margin + 46, light)

        y += 12
        c.drawLine(margin, y, pageWidth - margin, y, line); y += 24

        // Bill to
        c.drawText("Bill To", margin, y, h); y += 18
        c.drawText(customer.name, margin, y, p); y += 15
        customer.phone?.let { c.drawText("Phone: $it", margin, y, light); y += 14 }
        customer.email?.let { c.drawText("Email: $it", margin, y, light); y += 14 }
        y += 12

        // Table header
        c.drawLine(margin, y, pageWidth - margin, y, line); y += 18
        c.drawText("Description", margin, y, h)
        c.drawText("Amount", pageWidth - margin - 80, y, h); y += 6
        c.drawLine(margin, y, pageWidth - margin, y, line); y += 22

        // Line item
        c.drawText(sub.productName + (sub.productCategory?.let { " ($it)" } ?: ""), margin, y, p)
        c.drawText(Money.format(sub.sellingPrice, cur), pageWidth - margin - 80, y, p); y += 18
        c.drawText("Validity: ${sub.validityDays ?: 0} days", margin, y, light); y += 14
        c.drawText("Expiry: ${DateUtils.format(sub.expiryDate)}", margin, y, light); y += 14
        if (sub.hasWarranty) { c.drawText("Warranty until: ${DateUtils.format(sub.warrantyExpiry)}", margin, y, light); y += 14 }

        if (includeLogin) {
            y += 8
            c.drawText("Login Details", margin, y, h); y += 16
            c.drawText("Email: ${sub.loginEmail ?: "-"}", margin, y, light); y += 14
            c.drawText("Password: ${sub.loginPasswordEnc ?: "-"}", margin, y, light); y += 14
        }

        y += 10
        c.drawLine(margin, y, pageWidth - margin, y, line); y += 22

        // Totals
        c.drawText("Subtotal", pageWidth - margin - 180, y, p)
        c.drawText(Money.format(sub.sellingPrice, cur), pageWidth - margin - 80, y, p); y += 18
        c.drawText("Paid", pageWidth - margin - 180, y, p)
        c.drawText(Money.format(sub.amountPaid, cur), pageWidth - margin - 80, y, p); y += 18
        val dueP = Paint().apply { color = if (sub.dueAmount > 0) Color.parseColor("#DC2626") else Color.parseColor("#16A34A"); textSize = 13f; isFakeBoldText = true }
        c.drawText("Due", pageWidth - margin - 180, y, dueP)
        c.drawText(Money.format(sub.dueAmount, cur), pageWidth - margin - 80, y, dueP); y += 24

        c.drawText("Payment status: ${sub.paymentStatus.name}", margin, y, light)

        // QR code (encodes invoice + customer + amount)
        val qrContent = "INVOICE:$invoiceNumber|${customer.name}|${sub.productName}|${Money.format(sub.sellingPrice, cur)}"
        val qr = qrGenerator.generate(qrContent, 120)
        c.drawBitmap(qr, pageWidth - margin - 120, pageHeight - margin - 160, null)
        c.drawText("Scan to verify", pageWidth - margin - 115, pageHeight - margin - 32, light)

        // Footer
        val footer = Paint().apply { color = Color.GRAY; textSize = 10f }
        c.drawText("Thank you for your business!", margin, pageHeight - margin - 10f, footer)

        doc.finishPage(page)

        val dir = File(context.cacheDir, "invoices").apply { mkdirs() }
        val file = File(dir, "$invoiceNumber.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()
        return file
    }
}
