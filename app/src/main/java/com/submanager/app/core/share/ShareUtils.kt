package com.submanager.app.core.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helpers for sharing text and files to WhatsApp / Telegram / Email / generic chooser.
 */
@Singleton
class ShareUtils @Inject constructor() {

    private fun authority(context: Context) = "${context.packageName}.fileprovider"

    /** Opens WhatsApp chat with [phone] (digits incl. country code) pre-filled with [message]. */
    fun whatsAppText(context: Context, phone: String?, message: String) {
        val clean = phone?.filter { it.isDigit() } ?: ""
        val url = if (clean.isNotEmpty())
            "https://wa.me/$clean?text=" + Uri.encode(message)
        else
            "https://wa.me/?text=" + Uri.encode(message)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /** Shares a file (e.g. PDF invoice). If [whatsAppOnly], targets WhatsApp, else shows chooser. */
    fun shareFile(context: Context, file: File, mime: String = "application/pdf", text: String? = null, whatsAppOnly: Boolean = false) {
        val uri: Uri = FileProvider.getUriForFile(context, authority(context), file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            text?.let { putExtra(Intent.EXTRA_TEXT, it) }
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            if (whatsAppOnly) setPackage("com.whatsapp")
        }
        try {
            context.startActivity(
                if (whatsAppOnly) intent else Intent.createChooser(intent, "Share invoice").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: Exception) {
            // WhatsApp not installed -> fall back to chooser
            context.startActivity(Intent.createChooser(intent.apply { setPackage(null) }, "Share invoice").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    fun emailText(context: Context, to: String?, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:" + (to ?: ""))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
