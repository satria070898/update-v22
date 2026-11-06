package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyHelper {

    private val rupiahSymbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    private val formatter = DecimalFormat("#,###", rupiahSymbols)

    fun formatRupiah(amount: Double, withPrefix: Boolean = true): String {
        val formatted = formatter.format(amount.toLong())
        return if (withPrefix) "Rp$formatted" else formatted
    }

    fun formatCompact(amount: Double): String {
        val absAmount = Math.abs(amount)
        return when {
            absAmount >= 1_000_000_000 -> String.format(Locale.US, "%.1fMld", amount / 1_000_000_000)
            absAmount >= 1_000_000 -> {
                val formatted = String.format(Locale.US, "%.1fM", amount / 1_000_000)
                formatted.replace(".0M", "M")
            }
            absAmount >= 1_000 -> {
                val formatted = String.format(Locale.US, "%.0fK", amount / 1_000)
                formatted
            }
            else -> amount.toLong().toString()
        }
    }

    fun formatCalendarExpense(amount: Double): String {
        if (amount <= 0) return ""
        val thousands = (amount / 1000).toInt()
        return if (amount % 1000 != 0.0 && amount < 100000) {
            String.format(Locale.US, "-%.1fK", amount / 1000)
        } else {
            "-$thousands" + "K"
        }
    }
}
