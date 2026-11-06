package com.example

import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.data.repository.FinanceRepository
import com.example.util.CurrencyHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCurrencyFormatting() {
        assertEquals("Rp341.472", CurrencyHelper.formatRupiah(341472.0))
        assertEquals("Rp6.182.200", CurrencyHelper.formatRupiah(6182200.0))
        assertEquals("4.8M", CurrencyHelper.formatCompact(4800000.0))
        assertEquals("170K", CurrencyHelper.formatCompact(170000.0))
        assertEquals("-100K", CurrencyHelper.formatCalendarExpense(100000.0))
    }

    @Test
    fun testThousandsSeparatorVisualTransformation() {
        val transformation = com.example.util.ThousandsSeparatorVisualTransformation('.')

        // Test empty string
        val emptyResult = transformation.filter(androidx.compose.ui.text.AnnotatedString(""))
        assertEquals("", emptyResult.text.text)

        // Test single/double/triple digits
        assertEquals("5", transformation.filter(androidx.compose.ui.text.AnnotatedString("5")).text.text)
        assertEquals("50", transformation.filter(androidx.compose.ui.text.AnnotatedString("50")).text.text)
        assertEquals("500", transformation.filter(androidx.compose.ui.text.AnnotatedString("500")).text.text)

        // Test thousands
        assertEquals("1.000", transformation.filter(androidx.compose.ui.text.AnnotatedString("1000")).text.text)
        assertEquals("10.000", transformation.filter(androidx.compose.ui.text.AnnotatedString("10000")).text.text)
        assertEquals("100.000", transformation.filter(androidx.compose.ui.text.AnnotatedString("100000")).text.text)

        // Test millions and billions
        assertEquals("1.500.000", transformation.filter(androidx.compose.ui.text.AnnotatedString("1500000")).text.text)
        assertEquals("25.000.000", transformation.filter(androidx.compose.ui.text.AnnotatedString("25000000")).text.text)
        assertEquals("1.000.000.000", transformation.filter(androidx.compose.ui.text.AnnotatedString("1000000000")).text.text)

        // Test offset mapping bounds and monotonicity
        val millionTransformed = transformation.filter(androidx.compose.ui.text.AnnotatedString("1000000"))
        assertEquals("1.000.000", millionTransformed.text.text)
        val mapping = millionTransformed.offsetMapping

        // originalToTransformed checks
        assertEquals(0, mapping.originalToTransformed(0))
        assertEquals(9, mapping.originalToTransformed(7))

        // transformedToOriginal checks
        assertEquals(0, mapping.transformedToOriginal(0))
        assertEquals(7, mapping.transformedToOriginal(9))
    }

    @Test
    fun testRepositoryInitialBalancesAndReactiveAddition() {
        val repo = FinanceRepository()
        val initialWallets = repo.wallets.value
        val bri = initialWallets.find { it.name == "BRI Utama" }
        val initialBalance = bri?.balance ?: 0.0

        // Add expense of 50.000 to BRI Utama
        val tx = Transaction(
            title = "Beli Kopi",
            category = "Food & Dining",
            amount = 50000.0,
            type = TransactionType.EXPENSE,
            accountName = "BRI Utama",
            dayOfMonth = 28,
            monthIndex = 10,
            dayOfWeekName = "Rab"
        )
        repo.addTransaction(tx)

        val updatedBri = repo.wallets.value.find { it.name == "BRI Utama" }
        assertEquals(initialBalance - 50000.0, updatedBri?.balance ?: 0.0, 0.01)
        assertTrue(repo.transactions.value.any { it.title == "Beli Kopi" })
    }

    @Test
    fun testNetWorthCalculation() {
        val repo = FinanceRepository()
        val totalWallets = repo.wallets.value.sumOf { it.balance }
        val totalAssets = repo.assets.value.sumOf { it.value }
        val totalDebts = repo.debts.value.sumOf { it.totalAmount - it.paidAmount }

        val netWorth = totalWallets + totalAssets - totalDebts
        assertTrue("Net worth should be positive", netWorth > 0)
    }

    @Test
    fun testMonthAndYearSelection() {
        val repo = FinanceRepository()
        val viewModel = com.example.ui.viewmodel.FinanceViewModel(repo)

        viewModel.selectMonthAndYear(11, 2027)
        assertEquals(11, viewModel.selectedMonthIndex.value)
        assertEquals(2027, viewModel.selectedYear.value)
    }

    @Test
    fun testRecurringTransactionAutoDeduct() {
        val repo = FinanceRepository()
        val bri = repo.wallets.value.find { it.name == "BRI Utama" }
        val initialBalance = bri?.balance ?: 0.0

        val recurring = com.example.data.model.RecurringTransaction(
            title = "Langganan Spotify Family",
            amount = 86000.0,
            type = TransactionType.EXPENSE,
            category = "Bills & Fees",
            accountName = "BRI Utama",
            dayOfMonth = 15,
            autoDeduct = true,
            isActive = true
        )
        // Add recurring transaction with currentDay = 28 (today is past day 15), so it should auto-deduct for month 10!
        repo.addRecurringTransaction(recurring, checkImmediateProcess = true, currentDay = 28, currentMonth = 10, currentYear = 2026)

        val updatedBri = repo.wallets.value.find { it.name == "BRI Utama" }
        assertEquals(initialBalance - 86000.0, updatedBri?.balance ?: 0.0, 0.01)

        val createdTx = repo.transactions.value.find { it.title == "Langganan Spotify Family" }
        assertTrue("Automatic transaction must be recorded in transactions list", createdTx != null)
        assertEquals(15, createdTx?.dayOfMonth)
        assertEquals(10, createdTx?.monthIndex)
    }

    @Test
    fun testPlannerCalculationsAndEdit() {
        val repo = FinanceRepository()
        val viewModel = com.example.ui.viewmodel.FinanceViewModel(repo)

        val incomeTotal = repo.plannedItems.value.filter { it.section == com.example.data.model.PlanSectionType.INCOME }.sumOf { it.amount }
        val expenseTotal = repo.plannedItems.value.filter { it.section == com.example.data.model.PlanSectionType.EXPENSE }.sumOf { it.amount }
        val savingsTotal = repo.plannedItems.value.filter { it.section == com.example.data.model.PlanSectionType.SAVINGS }.sumOf { it.amount }

        assertEquals(5758000.0, incomeTotal, 0.01)
        assertEquals(4150000.0, expenseTotal, 0.01)
        assertEquals(1600000.0, savingsTotal, 0.01)

        val unallocated = incomeTotal - (expenseTotal + savingsTotal)
        assertEquals(8000.0, unallocated, 0.01)

        // Test editing an item with pencil edit
        val gajiItem = repo.plannedItems.value.find { it.name == "Gaji Pokok" }
        assertTrue(gajiItem != null)
        repo.updatePlannedItem(gajiItem!!.copy(amount = 3000000.0))

        val updatedIncome = repo.plannedItems.value.filter { it.section == com.example.data.model.PlanSectionType.INCOME }.sumOf { it.amount }
        assertEquals(5758000.0 + (3000000.0 - 2684000.0), updatedIncome, 0.01)
    }

    @Test
    fun testChartPeriodOneMonthSpansFullMonth() {
        val repo = FinanceRepository()
        val wallets = repo.wallets.value
        val assets = repo.assets.value
        val debts = repo.debts.value
        val transactions = repo.transactions.value

        val chartData = com.example.util.ChartDataHelper.getNetWorthTrend(
            wallets = wallets,
            assets = assets,
            debts = debts,
            transactions = transactions,
            subTab = 0,
            period = com.example.util.ChartPeriod.ONE_MONTH
        )

        // 1M should span a full month with 30 data points
        assertEquals(30, chartData.points.size)
        assertEquals(30, chartData.rawValues.size)
        assertTrue(chartData.startLabel.isNotBlank())
        assertTrue(chartData.midLabel.isNotBlank())
        assertTrue(chartData.endLabel.isNotBlank())
    }

    @Test
    fun testCustomUploadedIconToModels() {
        val customWallet = com.example.data.model.AccountWallet(
            name = "Dompet Pribadi",
            balance = 1500000.0,
            iconName = "custom:/data/user/0/com.aistudio.keuangan/files/custom_icons/wallet_123.png"
        )
        assertTrue(customWallet.iconName.startsWith("custom:"))

        val customAsset = com.example.data.model.AssetItem(
            name = "Emas Batangan",
            value = 10000000.0,
            iconUri = "custom:/data/user/0/com.aistudio.keuangan/files/custom_icons/asset_123.png"
        )
        assertTrue(customAsset.iconUri.startsWith("custom:"))
    }

    @Test
    fun testTransactionChronologicalDescendingComparatorWithTime() {
        val helper = com.example.util.TransactionMathHelper
        assertEquals(14 * 3600 + 30 * 60, helper.parseTimeToSeconds("14:30"))
        assertEquals(9 * 3600 + 15 * 60, helper.parseTimeToSeconds("09:15"))
        assertEquals(20 * 3600 + 45 * 60, helper.parseTimeToSeconds("08:45 PM"))
        assertEquals(8 * 3600 + 45 * 60, helper.parseTimeToSeconds("08:45 AM"))

        val tx1 = Transaction(id = "1", title = "Sarapan", category = "Makanan", type = TransactionType.EXPENSE, accountName = "Dompet", amount = 25000.0, dayOfMonth = 10, monthIndex = 11, year = 2026, time = "07:30")
        val tx2 = Transaction(id = "2", title = "Makan Siang", category = "Makanan", type = TransactionType.EXPENSE, accountName = "Dompet", amount = 45000.0, dayOfMonth = 10, monthIndex = 11, year = 2026, time = "12:15")
        val tx3 = Transaction(id = "3", title = "Kopi Sore", category = "Makanan", type = TransactionType.EXPENSE, accountName = "Dompet", amount = 30000.0, dayOfMonth = 10, monthIndex = 11, year = 2026, time = "16:45")
        val tx4 = Transaction(id = "4", title = "Makan Malam", category = "Makanan", type = TransactionType.EXPENSE, accountName = "Dompet", amount = 60000.0, dayOfMonth = 10, monthIndex = 11, year = 2026, time = "20:00")
        val txOlderDay = Transaction(id = "5", title = "Kemarin", category = "Makanan", type = TransactionType.EXPENSE, accountName = "Dompet", amount = 50000.0, dayOfMonth = 9, monthIndex = 11, year = 2026, time = "23:59")

        val list = listOf(tx1, txOlderDay, tx3, tx2, tx4)
        val sorted = list.sortedWith(helper.transactionChronologicalDescendingComparator)

        assertEquals("Makan Malam", sorted[0].title)
        assertEquals("Kopi Sore", sorted[1].title)
        assertEquals("Makan Siang", sorted[2].title)
        assertEquals("Sarapan", sorted[3].title)
        assertEquals("Kemarin", sorted[4].title)
    }

    @Test
    fun testIndonesianInstitutionHelperCustomLogoSync() {
        val helper = com.example.util.IndonesianInstitutionHelper
        val initialVersion = helper.logoRegistryVersion

        helper.registerCustomLogo("BCA Digital", "custom:/data/user/0/com.aistudio.keuangan/files/custom_icons/bca_new.png")
        assertTrue(helper.logoRegistryVersion > initialVersion)

        val resolved = helper.getRegisteredLogo("BCA Digital")
        assertEquals("custom:/data/user/0/com.aistudio.keuangan/files/custom_icons/bca_new.png", resolved)

        val resolvedIgnoreCase = helper.getRegisteredLogo("bca digital")
        assertEquals("custom:/data/user/0/com.aistudio.keuangan/files/custom_icons/bca_new.png", resolvedIgnoreCase)
    }

    @Test
    fun testImageUploadHelperFileNameExtraction() {
        val helper = com.example.util.ImageUploadHelper
        assertEquals("wallet_123.png", helper.extractFileName("custom:/data/user/0/app/files/custom_icons/wallet_123.png"))
        assertEquals("asset_456.png", helper.extractFileName("file:///storage/emulated/0/asset_456.png"))
        assertEquals("icon_789.png", helper.extractFileName("/storage/emulated/0/icon_789.png"))
        assertEquals("Bank", helper.extractFileName("Bank"))
    }

    @Test
    fun testStockAssetLotAverageAndRealValueCalculations() {
        val stockAsset = com.example.data.model.AssetItem(
            name = "Saham BBRI",
            assetType = "Saham",
            value = 0.0,
            lotCount = 10.0,
            averagePrice = 4500.0,
            currentPrice = 5000.0
        )

        assertTrue(stockAsset.isStock)
        assertEquals(1000.0, stockAsset.sharesCount, 0.01)
        assertEquals(4500000.0, stockAsset.costBasis, 0.01)
        assertEquals(5000000.0, stockAsset.realValue, 0.01)
        assertEquals(500000.0, stockAsset.unrealizedGainLoss, 0.01)
        assertEquals(11.11, stockAsset.calculatedPerformancePercent, 0.05)
    }

    @Test
    fun testLentDebtRepaymentIncreasesWalletBalance() {
        val repo = FinanceRepository()
        val walletName = "BRI Utama"
        val initialWallet = repo.wallets.value.find { it.name.equals(walletName, ignoreCase = true) }
        val initialBalance = initialWallet?.balance ?: 0.0
        assertTrue("Initial balance should be positive", initialBalance > 0)

        // Create a LENT debt (uang yang dipinjamkan)
        val lentDebt = com.example.data.model.DebtItem(
            id = "lent_test_1",
            name = "Pinjaman Budi",
            type = com.example.data.model.DebtType.LENT,
            totalAmount = 1000000.0,
            dueDate = "30 Des 2026",
            linkedWalletName = walletName
        )
        repo.addDebt(lentDebt, depositToWalletName = null)

        // Record a repayment of 300.000 from borrower
        val paymentRecord = com.example.data.model.DebtRecord(
            id = "rec_lent_1",
            debtId = lentDebt.id,
            type = "Pembayaran",
            amount = 300000.0,
            sourceWalletName = walletName,
            note = "Cicilan ke-1 dari Budi"
        )
        repo.addDebtRecord(lentDebt.id, paymentRecord, affectWalletName = walletName)

        // The wallet balance MUST have INCREASED by 300.000, not decreased!
        val walletAfterPayment = repo.wallets.value.find { it.name.equals(walletName, ignoreCase = true) }
        assertEquals(initialBalance + 300000.0, walletAfterPayment?.balance ?: 0.0, 0.01)

        // And an Income transaction should have been recorded
        val tx = repo.transactions.value.find { it.id == "debt_rec_rec_lent_1" }
        assertEquals(TransactionType.INCOME, tx?.type)
        assertEquals(300000.0, tx?.amount ?: 0.0, 0.01)

        // Delete the record and verify wallet balance reverts (decreases back)
        repo.deleteDebtRecord(lentDebt.id, paymentRecord.id)
        val walletAfterDelete = repo.wallets.value.find { it.name.equals(walletName, ignoreCase = true) }
        assertEquals(initialBalance, walletAfterDelete?.balance ?: 0.0, 0.01)
    }

    @Test
    fun testBorrowedDebtPaymentDecreasesWalletBalance() {
        val repo = FinanceRepository()
        val walletName = "BRI Utama"
        val initialWallet = repo.wallets.value.find { it.name.equals(walletName, ignoreCase = true) }
        val initialBalance = initialWallet?.balance ?: 0.0
        assertTrue("Initial balance should be positive", initialBalance > 0)

        // Create a BORROWED debt (utang yang dipinjam user)
        val borrowedDebt = com.example.data.model.DebtItem(
            id = "borrowed_test_1",
            name = "KTA Bank",
            type = com.example.data.model.DebtType.BORROWED,
            totalAmount = 2000000.0,
            dueDate = "30 Des 2026",
            linkedWalletName = walletName
        )
        repo.addDebt(borrowedDebt, depositToWalletName = null)

        // User pays debt 100.000
        val paymentRecord = com.example.data.model.DebtRecord(
            id = "rec_borrowed_1",
            debtId = borrowedDebt.id,
            type = "Pembayaran",
            amount = 100000.0,
            sourceWalletName = walletName,
            note = "Cicilan KTA"
        )
        repo.addDebtRecord(borrowedDebt.id, paymentRecord, affectWalletName = walletName)

        // The wallet balance MUST have DECREASED by 100.000
        val walletAfterPayment = repo.wallets.value.find { it.name.equals(walletName, ignoreCase = true) }
        assertEquals(initialBalance - 100000.0, walletAfterPayment?.balance ?: 0.0, 0.01)
    }
}
