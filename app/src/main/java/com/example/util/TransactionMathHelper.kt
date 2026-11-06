package com.example.util

import androidx.compose.ui.graphics.Color
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import kotlin.math.abs

data class CategorySlice(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val color: Color
)

object TransactionMathHelper {

    val expensePalette = listOf(
        Color(0xFF6B7280), // Other
        Color(0xFFEC4899), // Shopping
        Color(0xFFF59E0B), // Bills & Fees
        Color(0xFF10B981), // Groceries / Market
        Color(0xFF3B82F6), // Food & Dining
        Color(0xFF8B5CF6), // Transport
        Color(0xFF14B8A6), // Kesehatan
        Color(0xFFF43F5E), // Pendidikan
        Color(0xFF6366F1), // Hiburan
        Color(0xFFD97706),
        Color(0xFF0284C7)
    )

    val incomePalette = listOf(
        Color(0xFF10B981), // Salary
        Color(0xFF06B6D4), // Extra Income
        Color(0xFF3B82F6), // Bonus
        Color(0xFF8B5CF6), // Investasi
        Color(0xFFF59E0B), // Pemberian
        Color(0xFF14B8A6), // Dividen / Usaha
        Color(0xFFA855F7)
    )

    /**
     * Normalizes and cleans monetary values, preventing NaN, infinite, or negative amounts.
     * Prevents double-negative bugs where subtracting a negative would increase a balance.
     */
    fun sanitizeAmount(amount: Double): Double {
        return if (amount.isNaN() || amount.isInfinite()) {
            0.0
        } else {
            abs(amount)
        }
    }

    /**
     * Sanitizes transaction to ensure amount and transferFee are strictly non-negative,
     * ensuring math operations behave deterministically according to transaction type.
     */
    fun sanitizeTransaction(tx: Transaction): Transaction {
        val cleanAmount = sanitizeAmount(tx.amount)
        val cleanFee = sanitizeAmount(tx.transferFee)

        val resolvedType = when {
            tx.type == TransactionType.TRANSFER ||
            tx.category.equals("Transfer", ignoreCase = true) ||
            tx.category.equals("Transfer Antar Bank", ignoreCase = true) ||
            tx.category.equals("Transfer Antar Akun", ignoreCase = true) ||
            tx.category.equals("Transfer Aset", ignoreCase = true) ||
            tx.category.equals("Asset", ignoreCase = true) ||
            tx.category.equals("Aset", ignoreCase = true) -> TransactionType.TRANSFER

            tx.type == TransactionType.INCOME ||
            tx.category.equals("Salary", ignoreCase = true) ||
            tx.category.equals("Gaji", ignoreCase = true) ||
            tx.category.equals("Extra Income", ignoreCase = true) ||
            tx.category.equals("Bonus", ignoreCase = true) ||
            tx.category.equals("Pemberian", ignoreCase = true) ||
            tx.category.equals("Hadiah", ignoreCase = true) -> TransactionType.INCOME

            else -> tx.type
        }

        return tx.copy(
            amount = cleanAmount,
            transferFee = cleanFee,
            type = resolvedType
        )
    }

    /**
     * Calculates the new balance for an account wallet when applying a transaction.
     * Rule:
     * - Income: newBalance = currentBalance + amount
     * - Expense: newBalance = currentBalance - amount
     * - Transfer (Source): newBalance = currentBalance - (amount + fee)
     * - Transfer (Destination): newBalance = currentBalance + amount
     */
    fun applyTransactionToBalance(
        currentBalance: Double,
        transaction: Transaction,
        isSourceAccount: Boolean
    ): Double {
        val cleanTx = sanitizeTransaction(transaction)
        return if (isSourceAccount) {
            when (cleanTx.type) {
                TransactionType.INCOME -> currentBalance + cleanTx.amount
                TransactionType.EXPENSE -> currentBalance - cleanTx.amount
                TransactionType.TRANSFER -> currentBalance - (cleanTx.amount + cleanTx.transferFee)
            }
        } else if (cleanTx.type == TransactionType.TRANSFER) {
            currentBalance + cleanTx.amount
        } else {
            currentBalance
        }
    }

    /**
     * Calculates the reversed balance for an account wallet when deleting or reverting a transaction.
     * Rule:
     * - Income deleted: newBalance = currentBalance - amount
     * - Expense deleted: newBalance = currentBalance + amount (saldo bertambah kembali)
     * - Transfer deleted:
     *     - Source account: newBalance = currentBalance + (amount + fee) (saldo bertambah kembali)
     *     - Destination account: newBalance = currentBalance - amount (saldo berkurang)
     */
    fun revertTransactionFromBalance(
        currentBalance: Double,
        transaction: Transaction,
        isSourceAccount: Boolean
    ): Double {
        val cleanTx = sanitizeTransaction(transaction)
        return if (isSourceAccount) {
            when (cleanTx.type) {
                TransactionType.INCOME -> currentBalance - cleanTx.amount
                TransactionType.EXPENSE -> currentBalance + cleanTx.amount
                TransactionType.TRANSFER -> currentBalance + (cleanTx.amount + cleanTx.transferFee)
            }
        } else if (cleanTx.type == TransactionType.TRANSFER) {
            currentBalance - cleanTx.amount
        } else {
            currentBalance
        }
    }

    /**
     * Computes category slices for donut charts and breakdown lists.
     * Guarantees that:
     * 1. Total = exact sum of all slice amounts
     * 2. Percentage = (sliceAmount / total) * 100%
     * 3. Slices are sorted by amount descending
     */
    fun buildCategoryBreakdown(
        transactions: List<Transaction>,
        type: TransactionType,
        palette: List<Color> = if (type == TransactionType.INCOME) incomePalette else expensePalette
    ): Pair<Double, List<CategorySlice>> {
        val filtered = transactions
            .map { sanitizeTransaction(it) }
            .filter { it.type == type }
        val grouped = filtered
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        if (grouped.isEmpty()) {
            return Pair(0.0, emptyList())
        }

        val total = grouped.values.sum()
        val slices = grouped.entries.mapIndexed { index, (cat, amt) ->
            val percentage = if (total > 0.0) ((amt / total) * 100.0).toFloat() else 0f
            CategorySlice(
                category = cat,
                amount = amt,
                percentage = percentage,
                color = palette[index % palette.size]
            )
        }.sortedByDescending { it.amount }

        return Pair(total, slices)
    }

    /**
     * Converts a time string (e.g. "18:51", "09.30", "14:06:22", "02:30 PM", "11.15 AM") to total seconds for precise sorting.
     */
    fun parseTimeToSeconds(timeStr: String): Int {
        if (timeStr.isBlank()) return 0
        val clean = timeStr.trim()
        val isPm = clean.contains("PM", ignoreCase = true)
        val isAm = clean.contains("AM", ignoreCase = true)
        val normalized = clean
            .replace("PM", "", ignoreCase = true)
            .replace("AM", "", ignoreCase = true)
            .replace("WIB", "", ignoreCase = true)
            .replace("WITA", "", ignoreCase = true)
            .replace("WIT", "", ignoreCase = true)
            .trim()
            .replace('.', ':')
            .replace(' ', ':')
        val parts = normalized.split(':').filter { it.isNotBlank() }
        var hour = parts.getOrNull(0)?.filter { it.isDigit() }?.toIntOrNull() ?: 0
        val min = parts.getOrNull(1)?.filter { it.isDigit() }?.toIntOrNull() ?: 0
        val sec = parts.getOrNull(2)?.filter { it.isDigit() }?.toIntOrNull() ?: 0
        if (isPm && hour < 12) {
            hour += 12
        } else if (isAm && hour == 12) {
            hour = 0
        }
        return (hour.coerceIn(0, 23) * 3600) + (min.coerceIn(0, 59) * 60) + sec.coerceIn(0, 59)
    }

    /**
     * Standard comparator to sort transactions in reverse chronological order:
     * Newest year -> newest month -> newest day of month -> newest time (hours:minutes:seconds)
     */
    val transactionChronologicalDescendingComparator = Comparator<Transaction> { t1, t2 ->
        val cYear = t2.year.compareTo(t1.year)
        if (cYear != 0) return@Comparator cYear
        val cMonth = t2.monthIndex.compareTo(t1.monthIndex)
        if (cMonth != 0) return@Comparator cMonth
        val cDay = t2.dayOfMonth.compareTo(t1.dayOfMonth)
        if (cDay != 0) return@Comparator cDay
        val cTime = parseTimeToSeconds(t2.time).compareTo(parseTimeToSeconds(t1.time))
        if (cTime != 0) return@Comparator cTime
        t2.id.compareTo(t1.id)
    }

    /**
     * Parses standard Indonesian/English date strings (e.g. "12 Okt 2026", "2 Nov 2026 · 12:00", "02/11/2026")
     * into structured day, month, year, and dayOfWeek components.
     */
    fun parseDateComponents(dateStr: String): ParsedDateComponents {
        val cal = java.util.Calendar.getInstance()
        var day = cal.get(java.util.Calendar.DAY_OF_MONTH)
        var month = cal.get(java.util.Calendar.MONTH) + 1
        var year = cal.get(java.util.Calendar.YEAR)

        if (dateStr.isNotBlank()) {
            try {
                val clean = dateStr.replace("·", " ").trim()
                val tokens = clean.split(Regex("[\\s/\\-]+")).filter { it.isNotBlank() }
                if (tokens.size >= 3) {
                    val d = tokens[0].toIntOrNull()
                    val y = tokens[2].toIntOrNull()
                    val mStr = tokens[1].lowercase()
                    val m = when {
                        mStr.startsWith("jan") -> 1
                        mStr.startsWith("feb") -> 2
                        mStr.startsWith("mar") -> 3
                        mStr.startsWith("apr") -> 4
                        mStr.startsWith("mei") || mStr.startsWith("may") -> 5
                        mStr.startsWith("jun") -> 6
                        mStr.startsWith("jul") -> 7
                        mStr.startsWith("agu") || mStr.startsWith("aug") -> 8
                        mStr.startsWith("sep") -> 9
                        mStr.startsWith("okt") || mStr.startsWith("oct") -> 10
                        mStr.startsWith("nov") -> 11
                        mStr.startsWith("des") || mStr.startsWith("dec") -> 12
                        else -> tokens[1].toIntOrNull() ?: month
                    }
                    if (d != null && d in 1..31) day = d
                    if (m in 1..12) month = m
                    if (y != null && y in 2000..2100) year = y
                }
            } catch (_: Exception) {}
        }

        val c = java.util.Calendar.getInstance()
        c.set(year, (month - 1).coerceIn(0, 11), day.coerceIn(1, 31))
        val dayOfWeek = when (c.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.SUNDAY -> "Min"
            java.util.Calendar.MONDAY -> "Sen"
            java.util.Calendar.TUESDAY -> "Sel"
            java.util.Calendar.WEDNESDAY -> "Rab"
            java.util.Calendar.THURSDAY -> "Kam"
            java.util.Calendar.FRIDAY -> "Jum"
            java.util.Calendar.SATURDAY -> "Sab"
            else -> "Sen"
        }

        return ParsedDateComponents(day, month, year, dayOfWeek)
    }
}

data class ParsedDateComponents(
    val day: Int,
    val month: Int,
    val year: Int,
    val dayOfWeek: String
)
