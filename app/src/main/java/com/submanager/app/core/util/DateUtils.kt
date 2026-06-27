package com.submanager.app.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun format(epochMillis: Long?): String =
        if (epochMillis == null) "-" else displayFormat.format(Date(epochMillis))

    /** Remaining days from now until [expiryMillis]. Negative if already expired. */
    fun remainingDays(expiryMillis: Long?): Long {
        if (expiryMillis == null) return 0
        val diff = expiryMillis - System.currentTimeMillis()
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    fun isExpired(expiryMillis: Long?): Boolean =
        expiryMillis != null && expiryMillis < System.currentTimeMillis()

    fun startOfDay(millis: Long = System.currentTimeMillis()): Long {
        val cal = java.util.Calendar.getInstance().apply {
            timeInMillis = millis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun plusDays(millis: Long, days: Int): Long = millis + TimeUnit.DAYS.toMillis(days.toLong())
}
