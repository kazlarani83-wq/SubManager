package com.submanager.app.core.util

import java.text.NumberFormat
import java.util.Locale

object Money {
    fun format(amount: Double, currencySymbol: String = "$"): String {
        val nf = NumberFormat.getNumberInstance(Locale.getDefault())
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2
        return "$currencySymbol${nf.format(amount)}"
    }
}
