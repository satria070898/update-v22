package com.example.data.model

import java.util.UUID

enum class TransactionType {
    EXPENSE,
    INCOME,
    TRANSFER
}

enum class DebtType {
    BORROWED, // Dipinjam
    LENT      // Dipinjamkan
}

data class SubcategoryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconName: String = "",
    val isVisible: Boolean = true
)

data class MainCategory(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: TransactionType = TransactionType.EXPENSE,
    val iconName: String = "",
    val isVisible: Boolean = true,
    val subcategories: List<SubcategoryItem> = emptyList()
)

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val subcategory: String = "",
    val amount: Double,
    val type: TransactionType,
    val accountName: String,
    val dayOfMonth: Int,
    val monthIndex: Int, // 1 to 12 (10 = Oktober)
    val year: Int = 2026,
    val dayOfWeekName: String = "Sen",
    val note: String = "",
    val label: String = "",
    val recurrence: String = "Tidak pernah",
    val time: String = "18:51",
    val toAccountName: String = "",
    val transferFee: Double = 0.0,
    val targetAssetId: String = "",
    val targetAssetName: String = "",
    val targetDebtId: String = "",
    val targetDebtName: String = ""
)

data class AccountWallet(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bankType: String = "Bank",
    val balance: Double,
    val lastCategory: String = "Other",
    val lastAmount: Double = 0.0,
    val lastActivityTime: String = "2 hari lalu",
    val currency: String = "IDR",
    val iconName: String = "Bank",
    val excludeFromNetWorth: Boolean = false,
    val excludeFromBudget: Boolean = false
)

data class AssetRecord(
    val id: String = UUID.randomUUID().toString(),
    val assetId: String,
    val tag: String = "Buy", // "Buy", "Invest more", "Sold"
    val isIncrease: Boolean = true,
    val amount: Double,
    val sourceWalletName: String = "",
    val date: String = "7 Okt 2026",
    val time: String = "10.09",
    val note: String = "",
    val lotCount: Double = 0.0,
    val pricePerUnit: Double = 0.0
)

data class AssetItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val group: String = "Investasi", // "Tunai & Bank" or "Investasi"
    val assetType: String = "Bank Account", // "Bank Account", "Emas", "Saham", "Reksadana", "Kripto", "Properti", etc.
    val value: Double,
    val platform: String = "Bibit",
    val performancePercent: Double = 10.2,
    val currency: String = "IDR",
    val createdAtDate: String = "29 Okt 2026",
    val createdAtTime: String = "00.15",
    val valuationMethod: String = "Manual",
    val records: List<AssetRecord> = emptyList(),
    val iconUri: String = "",
    val tickerSymbol: String = "",
    val lotCount: Double = 0.0,
    val averagePrice: Double = 0.0,
    val currentPrice: Double = 0.0,
    val dailyPriceChange: Double = 0.0,
    val dailyPriceChangePercent: Double = 0.0,
    val lastPriceUpdate: Long = 0L
) {
    val isStock: Boolean
        get() = assetType.equals("Saham", ignoreCase = true) || name.contains("Saham", ignoreCase = true)

    val sharesCount: Double
        get() = lotCount * 100.0

    // Modal Pembelian (Cost Basis): lot * 100 * averagePrice, atau fallback ke value
    val costBasis: Double
        get() = if (lotCount > 0.0 && averagePrice > 0.0) lotCount * 100.0 * averagePrice else value

    // Nilai Real / Nilai Pasar Aktual:
    // Jika lot > 0 dan currentPrice > 0 -> lot * 100 * currentPrice
    // Jika currentPrice == 0 tapi lot > 0 dan averagePrice > 0 -> lot * 100 * averagePrice
    // Fallback: value
    val realValue: Double
        get() {
            if (lotCount > 0.0 && currentPrice > 0.0) {
                return lotCount * 100.0 * currentPrice
            }
            if (lotCount > 0.0 && averagePrice > 0.0) {
                return lotCount * 100.0 * averagePrice
            }
            return value
        }

    // Capital Gain / Loss (Unrealized Profit/Loss)
    val unrealizedGainLoss: Double
        get() = realValue - costBasis

    // Persentase Untung / Rugi aktual
    val calculatedPerformancePercent: Double
        get() {
            if (isStock && costBasis > 0.0 && (currentPrice > 0.0 || (lotCount > 0.0 && averagePrice > 0.0))) {
                return ((realValue - costBasis) / costBasis) * 100.0
            }
            return performancePercent
        }

    val resolvedTicker: String
        get() {
            if (tickerSymbol.isNotBlank()) {
                val clean = tickerSymbol.trim().uppercase()
                return if (!clean.contains(":") && clean.length in 3..6) "IDX:$clean" else clean
            }
            val upper = name.uppercase()
            val indonesianStocks = listOf(
                "BBRI", "BBCA", "BMRI", "BBNI", "TLKM", "ASII", "GOTO", "AMMN",
                "ANTM", "BRIS", "ICBP", "INDF", "UNVR", "ADRO", "PGAS", "KLBF",
                "MDKA", "ACES", "CPIN", "SMGR", "INCO", "PTBA", "MEDC", "BUKA",
                "ARTO", "TPIA", "BREN", "CUAN", "GGRM", "HMSP", "MYOR", "BUMI",
                "UNTR", "BRPT", "INKP", "TKIM", "MAPI", "AKRA", "EXCL", "ISAT"
            )
            for (code in indonesianStocks) {
                if (upper.contains(code)) return "IDX:$code"
            }
            if (assetType.equals("Kripto", ignoreCase = true) || upper.contains("BITCOIN") || upper.contains("BTC")) return "BINANCE:BTCUSDT"
            if (upper.contains("ETHEREUM") || upper.contains("ETH")) return "BINANCE:ETHUSDT"
            if (assetType.equals("Emas", ignoreCase = true) || upper.contains("EMAS") || upper.contains("GOLD")) return "TVC:GOLD"
            if (isStock) {
                val words = upper.split(" ", "_", "-", "/")
                for (w in words) {
                    val letters = w.filter { it.isLetter() }
                    if (letters.length in 4..5 && !letters.equals("SAHAM", ignoreCase = true)) {
                        return "IDX:$letters"
                    }
                }
                return "IDX:BBRI"
            }
            return ""
        }
}

data class DebtRecord(
    val id: String = UUID.randomUUID().toString(),
    val debtId: String,
    val type: String = "Pembayaran", // "Pembayaran", "Pinjam Lagi", "Bunga"
    val amount: Double,
    val date: String = "2 Nov 2026",
    val time: String = "12.56",
    val sourceWalletName: String = "",
    val note: String = ""
)

data class DebtItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: DebtType = DebtType.BORROWED,
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val dueDate: String,
    val startDate: String = "28 Mar 2026",
    val monthlyPayment: Double = 0.0,
    val interestRate: Double = 0.0,
    val extraFees: Double = 0.0,
    val categoryIcon: String = "CreditCard", // "House", "Building", "Car", "CreditCard", "Bank", "Motor", "Tools", "Cash"
    val institutionLogo: String = "",
    val tenureMonths: Int = 0,
    val linkedWalletName: String = "",
    val records: List<DebtRecord> = emptyList()
) {
    val remainingAmount: Double
        get() = (totalAmount - paidAmount).coerceAtLeast(0.0)

    val progressPercent: Int
        get() = if (totalAmount > 0) ((paidAmount / totalAmount) * 100).toInt().coerceIn(0, 100) else 0
}

data class UpcomingScheduleItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val accountName: String,
    val amount: Double,
    val isIncome: Boolean,
    val dateLabel: String, // e.g. "1 Nov"
    val dayLabel: String   // e.g. "Min"
)

data class BudgetCategory(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val allocated: Double,
    val spent: Double
) {
    val remaining: Double get() = (allocated - spent).coerceAtLeast(0.0)
    val percentage: Float get() = if (allocated > 0) (spent / allocated).toFloat().coerceIn(0f, 1f) else 0f
}

data class UserSettings(
    val language: String = "Indonesia",
    val isDarkMode: Boolean = true,
    val primaryCurrency: String = "IDR",
    val monthlyStartDay: Int = 1,
    val textSizePercent: Int = 100,
    val cashAccountsOnly: Boolean = false,
    val showPlannedInBalance: Boolean = true,
    val saveReminder: Boolean = false,
    val billReminder: Boolean = false,
    val budgetAlert: Boolean = true,
    val passcodeEnabled: Boolean = false,
    val appPasscode: String = "1234",
    val themeColorName: String = "FinTrack Blue",
    val numberFormat: String = "1.000.000 (Titik)",
    val dateFormat: String = "DD/MM/YYYY",
    val defaultHideBalance: Boolean = false,
    val dailyReminderTime: String = "20:00",
    val dailyReminderEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true
)

data class RecurringTransaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String,
    val subcategory: String = "",
    val accountName: String,
    val dayOfMonth: Int = 1, // 1..31
    val frequency: String = "Setiap bulan", // "Setiap hari", "Setiap 2 minggu", "Setiap bulan", "Setiap tahun"
    val startDate: String = "", // e.g. "01/11/2026"
    val endDate: String = "", // e.g. "01/11/2027" or empty for tanpa batas
    val note: String = "",
    val label: String = "",
    val isActive: Boolean = true,
    val autoDeduct: Boolean = true,
    val lastProcessedMonthYear: String = "" // format "M/yyyy", e.g. "10/2026"
)

enum class PlanSectionType {
    INCOME,
    EXPENSE,
    SAVINGS
}

data class PlannedItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String = "",
    val amount: Double,
    val section: PlanSectionType
)

data class FinancialPlan(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Pembagian Gaji",
    val monthIndex: Int = 10,
    val year: Int = 2026,
    val items: List<PlannedItem> = emptyList()
)

data class BackupRestoreResult(
    val transactionCount: Int = 0,
    val walletCount: Int = 0,
    val assetCount: Int = 0,
    val debtCount: Int = 0,
    val categoryCount: Int = 0,
    val planCount: Int = 0,
    val budgetCount: Int = 0,
    val recurringCount: Int = 0,
    val settingsRestored: Boolean = false,
    val logoCount: Int = 0
) {
    val totalItems: Int get() = transactionCount + walletCount + assetCount + debtCount + categoryCount + planCount + budgetCount + recurringCount + logoCount
}

