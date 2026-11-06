package com.example.data.repository

import android.content.Context
import com.example.data.model.AccountWallet
import com.example.data.model.AssetItem
import com.example.data.model.AssetRecord
import com.example.data.model.BudgetCategory
import com.example.data.model.DebtItem
import com.example.data.model.DebtRecord
import com.example.data.model.DebtType
import com.example.data.model.MainCategory
import com.example.data.model.PlanSectionType
import com.example.data.model.PlannedItem
import com.example.data.model.RecurringTransaction
import com.example.data.model.SubcategoryItem
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.data.model.UpcomingScheduleItem
import com.example.data.model.UserSettings
import com.example.util.ImageUploadHelper
import com.example.util.IndonesianInstitutionHelper
import com.example.util.TradingViewHelper
import com.example.util.TradingViewQuote
import com.example.util.TransactionMathHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class FinanceRepository(private val context: Context? = null) {

    companion object {
        private var sharedContext: Context? = null

        fun setApplicationContext(context: Context) {
            sharedContext = context.applicationContext
        }
    }

    private fun getActiveContext(): Context? = context ?: sharedContext

    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _wallets = MutableStateFlow<List<AccountWallet>>(emptyList())
    val wallets: StateFlow<List<AccountWallet>> = _wallets.asStateFlow()

    private val _assets = MutableStateFlow<List<AssetItem>>(emptyList())
    val assets: StateFlow<List<AssetItem>> = _assets.asStateFlow()

    private val _debts = MutableStateFlow<List<DebtItem>>(emptyList())
    val debts: StateFlow<List<DebtItem>> = _debts.asStateFlow()

    private val _upcomingItems = MutableStateFlow<List<UpcomingScheduleItem>>(emptyList())
    val upcomingItems: StateFlow<List<UpcomingScheduleItem>> = _upcomingItems.asStateFlow()

    private val _recurringTransactions = MutableStateFlow<List<RecurringTransaction>>(emptyList())
    val recurringTransactions: StateFlow<List<RecurringTransaction>> = _recurringTransactions.asStateFlow()

    private val _budgets = MutableStateFlow<List<BudgetCategory>>(emptyList())
    val budgets: StateFlow<List<BudgetCategory>> = _budgets.asStateFlow()

    private val _categories = MutableStateFlow<List<MainCategory>>(FinanceStorageHelper.getDefaultCategories())
    val categories: StateFlow<List<MainCategory>> = _categories.asStateFlow()

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _labels = MutableStateFlow<List<String>>(
        listOf("Investasi", "Susu Egra", "Tabungan", "Pribadi", "Keluarga")
    )
    val labels: StateFlow<List<String>> = _labels.asStateFlow()

    private val _planTitle = MutableStateFlow("Pembagian Gaji")
    val planTitle: StateFlow<String> = _planTitle.asStateFlow()

    private val _plannedItems = MutableStateFlow<List<PlannedItem>>(emptyList())
    val plannedItems: StateFlow<List<PlannedItem>> = _plannedItems.asStateFlow()

    private val _selectedWalletFilter = MutableStateFlow("Semua")
    val selectedWalletFilter: StateFlow<String> = _selectedWalletFilter.asStateFlow()

    fun selectWalletFilter(walletName: String) {
        _selectedWalletFilter.value = walletName
        saveToDisk()
    }

    init {
        val ctx = getActiveContext()
        if (ctx != null && FinanceStorageHelper.hasSavedData(ctx)) {
            val loaded = loadFromDisk(ctx)
            if (!loaded) {
                loadSampleData()
                saveToDisk(ctx)
            }
        } else {
            loadSampleData()
            if (ctx != null) {
                saveToDisk(ctx)
            }
        }
        syncDebtPaymentsWithTransactions()
    }

    fun initContext(ctx: Context) {
        sharedContext = ctx.applicationContext
        if (FinanceStorageHelper.hasSavedData(ctx)) {
            loadFromDisk(ctx)
        } else {
            saveToDisk(ctx)
        }
        syncDebtPaymentsWithTransactions()
    }

    private fun loadFromDisk(ctx: Context): Boolean {
        val data = FinanceStorageHelper.loadData(ctx) ?: return false
        _transactions.value = data.transactions.sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator)
        _wallets.value = data.wallets
        _assets.value = data.assets
        _debts.value = data.debts
        _budgets.value = data.budgets
        _recurringTransactions.value = data.recurringTransactions
        _plannedItems.value = data.plannedItems
        _planTitle.value = data.planTitle
        _labels.value = data.labels
        _settings.value = data.settings
        _selectedWalletFilter.value = data.selectedWalletFilter
        if (data.categories.isNotEmpty()) {
            _categories.value = data.categories
        }
        IndonesianInstitutionHelper.syncWithWalletsAndAssets(data.wallets, data.assets)
        syncDebtPaymentsWithTransactions()
        return true
    }

    fun saveToDisk(ctx: Context? = getActiveContext()) {
        val c = ctx ?: return
        val data = FinanceStorageHelper.PersistentData(
            transactions = _transactions.value,
            wallets = _wallets.value,
            assets = _assets.value,
            debts = _debts.value,
            budgets = _budgets.value,
            recurringTransactions = _recurringTransactions.value,
            plannedItems = _plannedItems.value,
            planTitle = _planTitle.value,
            labels = _labels.value,
            settings = _settings.value,
            selectedWalletFilter = _selectedWalletFilter.value,
            categories = _categories.value
        )
        ioScope.launch {
            FinanceStorageHelper.saveData(c, data)
        }
    }

    fun loadSampleData() {
        val initialWallets = listOf(
            AccountWallet(
                name = "BRI Utama",
                bankType = "Bank · IDR",
                balance = 341472.0,
                lastCategory = "Other",
                lastAmount = 100000.0,
                lastActivityTime = "2 hari lalu"
            ),
            AccountWallet(
                name = "BRI Istri",
                bankType = "Bank · IDR",
                balance = 106857.0,
                lastCategory = "Market",
                lastAmount = 75000.0,
                lastActivityTime = "3 hari lalu"
            ),
            AccountWallet(
                name = "Bank Jago",
                bankType = "Bank · IDR",
                balance = 7575.0,
                lastCategory = "Transfer",
                lastAmount = 50000.0,
                lastActivityTime = "1 minggu lalu"
            ),
            AccountWallet(
                name = "RDN Bank Jago",
                bankType = "Bank · IDR",
                balance = 2323.56,
                lastCategory = "Investasi",
                lastAmount = 10000.0,
                lastActivityTime = "2 minggu lalu"
            )
        )

        val tabunganBibitId = "asset_tabungan_bibit_1"
        val initialAssets = listOf(
            AssetItem(
                id = "asset_saham_bbri",
                name = "Saham BBRI",
                group = "Investasi",
                assetType = "Saham",
                value = 3794000.0,
                platform = "Stockbit",
                performancePercent = -41.72,
                tickerSymbol = "IDX:BBRI",
                lotCount = 14.0,
                averagePrice = 4650.0,
                currentPrice = 2710.0,
                dailyPriceChange = 20.0,
                dailyPriceChangePercent = 0.74,
                lastPriceUpdate = System.currentTimeMillis(),
                createdAtDate = "15 Sep 2026",
                createdAtTime = "09.30",
                records = listOf(
                    AssetRecord(
                        assetId = "asset_saham_bbri",
                        tag = "Buy",
                        isIncrease = true,
                        amount = 6510000.0,
                        sourceWalletName = "BRI Utama",
                        date = "15 Sep 2026",
                        time = "09.30",
                        note = "Beli 14 lot BBRI (Avg Rp4.650)"
                    )
                )
            ),
            AssetItem(
                id = "asset_saham_bbca",
                name = "Saham BBCA",
                group = "Investasi",
                assetType = "Saham",
                value = 5125000.0,
                platform = "Stockbit",
                performancePercent = 4.59,
                tickerSymbol = "IDX:BBCA",
                lotCount = 10.0,
                averagePrice = 4900.0,
                currentPrice = 5125.0,
                dailyPriceChange = 195.0,
                dailyPriceChangePercent = 3.96,
                lastPriceUpdate = System.currentTimeMillis(),
                createdAtDate = "20 Sep 2026",
                createdAtTime = "10.15",
                records = listOf(
                    AssetRecord(
                        assetId = "asset_saham_bbca",
                        tag = "Buy",
                        isIncrease = true,
                        amount = 4900000.0,
                        sourceWalletName = "BRI Utama",
                        date = "20 Sep 2026",
                        time = "10.15",
                        note = "Beli 10 lot BBCA (Avg Rp4.900)"
                    )
                )
            ),
            AssetItem(
                name = "Tabungan Allegra bibit",
                group = "Investasi",
                assetType = "Reksadana",
                value = 4100000.0,
                platform = "Bibit",
                performancePercent = 8.5,
                createdAtDate = "1 Okt 2026",
                createdAtTime = "14.20"
            ),
            AssetItem(
                id = tabunganBibitId,
                name = "Tabungan Bibit",
                group = "Tunai & Bank",
                assetType = "Bank Account",
                value = 1200000.0,
                platform = "Bibit",
                performancePercent = 5.2,
                createdAtDate = "29 Okt 2026",
                createdAtTime = "00.15",
                records = listOf(
                    AssetRecord(
                        assetId = tabunganBibitId,
                        tag = "Buy",
                        isIncrease = true,
                        amount = 300000.0,
                        sourceWalletName = "BRI Utama",
                        date = "7 Okt 2026",
                        time = "10.09",
                        note = "tabungan ayah"
                    ),
                    AssetRecord(
                        assetId = tabunganBibitId,
                        tag = "Invest more",
                        isIncrease = true,
                        amount = 500000.0,
                        sourceWalletName = "BRI Utama",
                        date = "30 Sep 2026",
                        time = "08.45",
                        note = "Setoran awal Bibit"
                    )
                )
            ),
            AssetItem(
                name = "Emas Antam LM",
                group = "Investasi",
                assetType = "Emas",
                value = 14500000.0,
                platform = "Antam",
                performancePercent = 12.4,
                createdAtDate = "10 Okt 2026",
                createdAtTime = "11.00",
                records = listOf(
                    AssetRecord(
                        assetId = "asset_emas_antam",
                        tag = "Buy",
                        isIncrease = true,
                        amount = 13000000.0,
                        sourceWalletName = "BRI Utama",
                        date = "10 Okt 2026",
                        time = "11.00",
                        note = "Beli 10gr Emas Batangan Antam"
                    )
                )
            ),
            AssetItem(
                name = "Kripto Bitcoin",
                group = "Investasi",
                assetType = "Kripto",
                value = 8750000.0,
                platform = "Indodax",
                performancePercent = 18.2,
                createdAtDate = "5 Okt 2026",
                createdAtTime = "20.15",
                records = listOf(
                    AssetRecord(
                        assetId = "asset_kripto_btc",
                        tag = "Buy",
                        isIncrease = true,
                        amount = 7500000.0,
                        sourceWalletName = "BRI Utama",
                        date = "5 Okt 2026",
                        time = "20.15",
                        note = "Akumulasi DCA BTC"
                    )
                )
            ),
            AssetItem(
                name = "Dana Darurat",
                group = "Tunai & Bank",
                assetType = "Bank Account",
                value = 3164000.0,
                platform = "Bibit",
                performancePercent = 6.1,
                createdAtDate = "25 Sep 2026",
                createdAtTime = "11.10"
            )
        )

        val initialDebts = listOf(
            DebtItem(
                id = "debt_uang_allegra",
                name = "Uang Allegra",
                type = DebtType.BORROWED,
                totalAmount = 8000000.0,
                paidAmount = 500000.0,
                dueDate = "1 Apr 2029",
                startDate = "28 Mar 2026",
                monthlyPayment = 250000.0,
                categoryIcon = "CreditCard",
                records = listOf(
                    DebtRecord(
                        id = "rec_allegra_1",
                        debtId = "debt_uang_allegra",
                        type = "Pembayaran",
                        amount = 250000.0,
                        date = "12 Okt 2026",
                        time = "20.41",
                        sourceWalletName = "BRI Utama",
                        note = "200 bibit 50 BRI"
                    ),
                    DebtRecord(
                        id = "rec_allegra_2",
                        debtId = "debt_uang_allegra",
                        type = "Pembayaran",
                        amount = 250000.0,
                        date = "9 Sep 2026",
                        time = "14.06",
                        sourceWalletName = "BRI Utama",
                        note = "200 bibit 50 BRI"
                    )
                )
            ),
            DebtItem(
                id = "debt_beli_bbri",
                name = "beli BBRI",
                type = DebtType.BORROWED,
                totalAmount = 550000.0,
                paidAmount = 0.0,
                dueDate = "16 Nov 2026",
                startDate = "16 Okt 2026",
                monthlyPayment = 50000.0,
                categoryIcon = "Bank",
                records = emptyList()
            )
        )

        val initialUpcoming = listOf(
            UpcomingScheduleItem(
                title = "Gaji Pokok",
                accountName = "BRI Utama",
                amount = 2700000.0,
                isIncome = true,
                dateLabel = "1 Nov",
                dayLabel = "Min"
            ),
            UpcomingScheduleItem(
                title = "Admin BRI Istri",
                accountName = "BRI Istri",
                amount = 8000.0,
                isIncome = false,
                dateLabel = "16 Nov",
                dayLabel = "Sen"
            ),
            UpcomingScheduleItem(
                title = "Cicilan BBRI",
                accountName = "BRI Utama",
                amount = 55000.0,
                isIncome = false,
                dateLabel = "25 Nov",
                dayLabel = "Rab"
            )
        )

        val initialTransactions = mutableListOf(
            Transaction(
                title = "Ambil Cash",
                category = "Other",
                amount = 100000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 26,
                monthIndex = 10,
                dayOfWeekName = "Sen",
                note = "Tarik tunai ATM"
            ),
            Transaction(
                title = "Admin BRI Utama",
                category = "Other",
                amount = 4000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 20,
                monthIndex = 10,
                dayOfWeekName = "Sel",
                note = "Biaya admin bulanan"
            ),
            Transaction(
                title = "rapel tambahan gaji",
                category = "Extra Income",
                amount = 289200.0,
                type = TransactionType.INCOME,
                accountName = "BRI Utama",
                dayOfMonth = 19,
                monthIndex = 10,
                dayOfWeekName = "Sen",
                note = "Bonus proyek"
            ),
            Transaction(
                title = "uang egra",
                category = "Extra Income",
                amount = 100000.0,
                type = TransactionType.INCOME,
                accountName = "BRI Utama",
                dayOfMonth = 18,
                monthIndex = 10,
                dayOfWeekName = "Min",
                note = "Pemberian teman"
            ),
            Transaction(
                title = "pulsa",
                category = "Bills & Fees",
                amount = 75890.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 18,
                monthIndex = 10,
                dayOfWeekName = "Min",
                note = "Paket internet telkomsel"
            ),
            Transaction(
                title = "Belanja Mingguan",
                category = "Market",
                amount = 300000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 17,
                monthIndex = 10,
                dayOfWeekName = "Sab",
                note = "Sayur dan buah"
            ),
            Transaction(
                title = "Biaya Admin Rekening",
                category = "Bills & Fees",
                amount = 8000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Istri",
                dayOfMonth = 16,
                monthIndex = 10,
                dayOfWeekName = "Jum"
            ),
            Transaction(
                title = "Bensin & Parkir",
                category = "Transport",
                amount = 170000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 13,
                monthIndex = 10,
                dayOfWeekName = "Sel"
            ),
            Transaction(
                title = "Makan Siang Tim",
                category = "Food & Dining",
                amount = 100000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 12,
                monthIndex = 10,
                dayOfWeekName = "Sen"
            ),
            Transaction(
                title = "Listrik PLN",
                category = "Bills & Fees",
                amount = 288000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 11,
                monthIndex = 10,
                dayOfWeekName = "Min"
            ),
            Transaction(
                title = "Kopi & Snack",
                category = "Food & Dining",
                amount = 90000.0,
                type = TransactionType.EXPENSE,
                accountName = "Bank Jago",
                dayOfMonth = 10,
                monthIndex = 10,
                dayOfWeekName = "Sab"
            ),
            Transaction(
                title = "Belanja Elektronik",
                category = "Shopping",
                amount = 629000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 8,
                monthIndex = 10,
                dayOfWeekName = "Kam"
            ),
            Transaction(
                title = "Service Motor & Oli",
                category = "Other",
                amount = 984000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 7,
                monthIndex = 10,
                dayOfWeekName = "Rab",
                note = "Hari terboros"
            ),
            Transaction(
                title = "Belanja Perlengkapan Rumah",
                category = "Shopping",
                amount = 433000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Istri",
                dayOfMonth = 6,
                monthIndex = 10,
                dayOfWeekName = "Sel"
            ),
            Transaction(
                title = "Langganan Netflix & Spotify",
                category = "Bills & Fees",
                amount = 493000.0,
                type = TransactionType.EXPENSE,
                accountName = "Bank Jago",
                dayOfMonth = 3,
                monthIndex = 10,
                dayOfWeekName = "Jum"
            ),
            Transaction(
                title = "Belanja Bulanan Supermarket",
                category = "Market",
                amount = 600000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 2,
                monthIndex = 10,
                dayOfWeekName = "Kam"
            ),
            Transaction(
                title = "Sarapan Pagi",
                category = "Food & Dining",
                amount = 100000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                dayOfMonth = 1,
                monthIndex = 10,
                dayOfWeekName = "Rab"
            ),
            Transaction(
                title = "Gaji Pokok Oktober",
                category = "Salary",
                amount = 5793000.0,
                type = TransactionType.INCOME,
                accountName = "BRI Utama",
                dayOfMonth = 1,
                monthIndex = 10,
                dayOfWeekName = "Rab",
                note = "Payroll perusahaan"
            ),
            Transaction(
                title = "Transfer ke Bank Jago",
                category = "Transfer",
                amount = 500000.0,
                type = TransactionType.TRANSFER,
                accountName = "BRI Utama",
                toAccountName = "Bank Jago",
                transferFee = 2500.0,
                dayOfMonth = 14,
                monthIndex = 10,
                dayOfWeekName = "Sel",
                note = "Top up saldo operasional Jago"
            ),
            Transaction(
                title = "Transfer ke Bibit Reksadana",
                category = "Asset",
                amount = 350000.0,
                type = TransactionType.TRANSFER,
                accountName = "BRI Utama",
                targetAssetId = "1",
                targetAssetName = "Bibit Reksadana",
                dayOfMonth = 9,
                monthIndex = 10,
                dayOfWeekName = "Jum",
                note = "Investasi rutin bulanan"
            ),
            Transaction(
                id = "debt_rec_rec_allegra_1",
                title = "Pembayaran Utang: Uang Allegra",
                category = "Pembayaran Utang",
                amount = 250000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                toAccountName = "Uang Allegra",
                dayOfMonth = 12,
                monthIndex = 10,
                year = 2026,
                dayOfWeekName = "Sen",
                time = "20.41",
                note = "200 bibit 50 BRI",
                targetDebtId = "debt_uang_allegra",
                targetDebtName = "Uang Allegra"
            ),
            Transaction(
                id = "debt_rec_rec_allegra_2",
                title = "Pembayaran Utang: Uang Allegra",
                category = "Pembayaran Utang",
                amount = 250000.0,
                type = TransactionType.EXPENSE,
                accountName = "BRI Utama",
                toAccountName = "Uang Allegra",
                dayOfMonth = 9,
                monthIndex = 9,
                year = 2026,
                dayOfWeekName = "Rab",
                time = "14.06",
                note = "200 bibit 50 BRI",
                targetDebtId = "debt_uang_allegra",
                targetDebtName = "Uang Allegra"
            )
        )

        val initialBudgets = listOf(
            BudgetCategory(category = "Makanan & Minuman", allocated = 2000000.0, spent = 1450000.0),
            BudgetCategory(category = "Bills & Fees", allocated = 1500000.0, spent = 1256890.0),
            BudgetCategory(category = "Market / Belanja", allocated = 1500000.0, spent = 1333000.0),
            BudgetCategory(category = "Transport", allocated = 800000.0, spent = 340000.0),
            BudgetCategory(category = "Other", allocated = 1200000.0, spent = 1088000.0)
        )

        val initialRecurring = listOf(
            RecurringTransaction(
                title = "Langganan Netflix & Spotify",
                amount = 186000.0,
                type = TransactionType.EXPENSE,
                category = "Bills & Fees",
                accountName = "Bank Jago",
                dayOfMonth = 3,
                note = "Autodebet langganan media",
                label = "Pribadi",
                isActive = true,
                autoDeduct = true,
                lastProcessedMonthYear = "10/2026"
            ),
            RecurringTransaction(
                title = "Listrik PLN & Token",
                amount = 288000.0,
                type = TransactionType.EXPENSE,
                category = "Bills & Fees",
                accountName = "BRI Utama",
                dayOfMonth = 11,
                note = "Listrik rumah bulanan",
                label = "Keluarga",
                isActive = true,
                autoDeduct = true,
                lastProcessedMonthYear = "10/2026"
            ),
            RecurringTransaction(
                title = "Tagihan WiFi Indihome",
                amount = 350000.0,
                type = TransactionType.EXPENSE,
                category = "Bills & Fees",
                accountName = "BRI Utama",
                dayOfMonth = 20,
                note = "Internet 50 Mbps",
                label = "Keluarga",
                isActive = true,
                autoDeduct = true,
                lastProcessedMonthYear = "10/2026"
            ),
            RecurringTransaction(
                title = "Cicilan Motor Honda",
                amount = 850000.0,
                type = TransactionType.EXPENSE,
                category = "Other",
                accountName = "BRI Utama",
                dayOfMonth = 28,
                note = "Potong otomatis setiap tgl 28",
                label = "Pribadi",
                isActive = true,
                autoDeduct = true,
                lastProcessedMonthYear = ""
            ),
            RecurringTransaction(
                title = "Gaji Pokok Karyawan",
                amount = 5793000.0,
                type = TransactionType.INCOME,
                category = "Salary",
                accountName = "BRI Utama",
                dayOfMonth = 1,
                note = "Payroll bulanan",
                label = "Pribadi",
                isActive = true,
                autoDeduct = true,
                lastProcessedMonthYear = "10/2026"
            )
        )

        val initialPlannedItems = listOf(
            // Pemasukan (Total: 5.758.000)
            PlannedItem(name = "Gaji Pokok", category = "Salary", amount = 2684000.0, section = PlanSectionType.INCOME),
            PlannedItem(name = "Tunjangan Kinerja", category = "Salary", amount = 2199000.0, section = PlanSectionType.INCOME),
            PlannedItem(name = "Uang Makan", category = "Extra Income", amount = 875000.0, section = PlanSectionType.INCOME),

            // Pengeluaran (Total: 4.150.000)
            PlannedItem(name = "Uang Belanja", category = "Food & Drink", amount = 2000000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "Bensin", category = "Transport", amount = 200000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "Susu", category = "Family & Personal", amount = 400000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "Pampers", category = "Family & Personal", amount = 250000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "Pulsa", category = "Bills & Fees", amount = 100000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "Wifi", category = "Bills & Fees", amount = 100000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "Cicilan Shopee", category = "Bills & Fees", amount = 300000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "PNPM", category = "Bills & Fees", amount = 200000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "Jajan & Hiburan", category = "Entertainment", amount = 300000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "Arisan DWP", category = "Other", amount = 50000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "Kas Seganteng", category = "Other", amount = 30000.0, section = PlanSectionType.EXPENSE),
            PlannedItem(name = "Susu Dancow", category = "Food & Drink", amount = 220000.0, section = PlanSectionType.EXPENSE),

            // Rencana Tabungan (Total: 1.600.000)
            PlannedItem(name = "Tabungan", category = "", amount = 300000.0, section = PlanSectionType.SAVINGS),
            PlannedItem(name = "Dana Darurat", category = "", amount = 300000.0, section = PlanSectionType.SAVINGS),
            PlannedItem(name = "Investasi", category = "", amount = 700000.0, section = PlanSectionType.SAVINGS),
            PlannedItem(name = "Tabungan Allegra", category = "", amount = 200000.0, section = PlanSectionType.SAVINGS),
            PlannedItem(name = "BRI Egra", category = "", amount = 50000.0, section = PlanSectionType.SAVINGS),
            PlannedItem(name = "Pajak Sepeda", category = "", amount = 50000.0, section = PlanSectionType.SAVINGS)
        )

        _wallets.value = initialWallets
        _assets.value = initialAssets
        _debts.value = initialDebts
        _upcomingItems.value = initialUpcoming
        _transactions.value = initialTransactions.sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator)
        _budgets.value = initialBudgets
        _recurringTransactions.value = initialRecurring
        _plannedItems.value = initialPlannedItems
        IndonesianInstitutionHelper.syncWithWalletsAndAssets(initialWallets, initialAssets)
        syncDebtPaymentsWithTransactions()
    }

    fun addTransaction(tx: Transaction) {
        val cleanTx = TransactionMathHelper.sanitizeTransaction(tx)
        _transactions.update { (listOf(cleanTx) + it).sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator) }

        // Reactively adjust wallet balance
        _wallets.update { list ->
            list.map { wallet ->
                val isSource = wallet.name.equals(cleanTx.accountName, ignoreCase = true)
                val isDest = cleanTx.toAccountName.isNotBlank() && wallet.name.equals(cleanTx.toAccountName, ignoreCase = true)

                var newBalance = wallet.balance
                if (isSource) {
                    newBalance = TransactionMathHelper.applyTransactionToBalance(newBalance, cleanTx, isSourceAccount = true)
                }
                if (isDest && cleanTx.type == TransactionType.TRANSFER) {
                    newBalance = TransactionMathHelper.applyTransactionToBalance(newBalance, cleanTx, isSourceAccount = false)
                }

                if (isSource) {
                    wallet.copy(
                        balance = newBalance,
                        lastCategory = cleanTx.category,
                        lastAmount = cleanTx.amount,
                        lastActivityTime = "Baru saja"
                    )
                } else if (newBalance != wallet.balance) {
                    wallet.copy(
                        balance = newBalance,
                        lastCategory = "Transfer Masuk",
                        lastAmount = cleanTx.amount,
                        lastActivityTime = "Baru saja"
                    )
                } else {
                    wallet
                }
            }
        }

        // If transferred to asset, increase asset value AND record into asset's riwayat pencatatan
        if (cleanTx.type == TransactionType.TRANSFER && (cleanTx.targetAssetId.isNotBlank() || cleanTx.targetAssetName.isNotBlank())) {
            val monthNames = listOf("", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
            val monthStr = if (cleanTx.monthIndex in 1..12) monthNames[cleanTx.monthIndex] else "Okt"
            _assets.update { list ->
                list.map { asset ->
                    if (asset.id == cleanTx.targetAssetId || (cleanTx.targetAssetName.isNotBlank() && asset.name.equals(cleanTx.targetAssetName, ignoreCase = true))) {
                        val record = AssetRecord(
                            id = "tx_${cleanTx.id}",
                            assetId = asset.id,
                            tag = "Transfer",
                            isIncrease = true,
                            amount = cleanTx.amount,
                            sourceWalletName = cleanTx.accountName,
                            date = "${cleanTx.dayOfMonth} $monthStr ${cleanTx.year}",
                            time = cleanTx.time.ifBlank { "12:00" },
                            note = if (cleanTx.note.isNotBlank()) cleanTx.note else "Transfer dari ${cleanTx.accountName}"
                        )
                        asset.copy(
                            value = asset.value + cleanTx.amount,
                            records = listOf(record) + asset.records
                        )
                    } else asset
                }
            }
        }

        // If transferred to debt (Pembayaran Utang), record into debt's records and increase paidAmount
        if ((cleanTx.type == TransactionType.TRANSFER || cleanTx.category.equals("Pembayaran Utang", ignoreCase = true)) &&
            (cleanTx.targetDebtId.isNotBlank() || cleanTx.targetDebtName.isNotBlank())) {
            val monthNames = listOf("", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
            val monthStr = if (cleanTx.monthIndex in 1..12) monthNames[cleanTx.monthIndex] else "Okt"
            val recordTime = cleanTx.time.ifBlank { "12:00" }
            val formattedDate = "${cleanTx.dayOfMonth} $monthStr ${cleanTx.year} · $recordTime"
            val debtRecord = DebtRecord(
                id = "tx_${cleanTx.id}",
                debtId = cleanTx.targetDebtId,
                type = "Pembayaran",
                amount = cleanTx.amount,
                date = formattedDate,
                note = if (cleanTx.note.isNotBlank()) cleanTx.note else "Transfer dari ${cleanTx.accountName}",
                sourceWalletName = cleanTx.accountName
            )
            _debts.update { list ->
                list.map { debt ->
                    if (debt.id == cleanTx.targetDebtId || (cleanTx.targetDebtName.isNotBlank() && debt.name.equals(cleanTx.targetDebtName, ignoreCase = true))) {
                        val newPaid = debt.paidAmount + cleanTx.amount
                        debt.copy(
                            paidAmount = newPaid,
                            records = listOf(debtRecord) + debt.records
                        )
                    } else debt
                }
            }
        }
        saveToDisk()
    }

    fun deleteTransaction(txId: String) {
        val target = _transactions.value.find { it.id == txId } ?: return
        val cleanTx = TransactionMathHelper.sanitizeTransaction(target)
        _transactions.update { it.filterNot { item -> item.id == txId } }

        // Reverse wallet balance strictly according to rules:
        // - Income deleted -> wallet balance decreases (new = old - amount)
        // - Expense deleted -> wallet balance increases back (new = old + amount)
        // - Transfer deleted -> source wallet increases back (new = old + amount + fee), destination wallet decreases (new = old - amount)
        _wallets.update { list ->
            list.map { wallet ->
                val isSource = wallet.name.equals(cleanTx.accountName, ignoreCase = true)
                val isDest = cleanTx.toAccountName.isNotBlank() && wallet.name.equals(cleanTx.toAccountName, ignoreCase = true)

                var newBal = wallet.balance
                if (isSource) {
                    newBal = TransactionMathHelper.revertTransactionFromBalance(newBal, cleanTx, isSourceAccount = true)
                }
                if (isDest && cleanTx.type == TransactionType.TRANSFER) {
                    newBal = TransactionMathHelper.revertTransactionFromBalance(newBal, cleanTx, isSourceAccount = false)
                }

                if (newBal != wallet.balance) {
                    wallet.copy(balance = newBal)
                } else {
                    wallet
                }
            }
        }

        // Reverse asset value and remove record if transferred to asset
        if (cleanTx.type == TransactionType.TRANSFER && (cleanTx.targetAssetId.isNotBlank() || cleanTx.targetAssetName.isNotBlank())) {
            val recId = "tx_${cleanTx.id}"
            val assetRecId = "asset_rec_${cleanTx.id}"
            _assets.update { list ->
                list.map { asset ->
                    if (asset.id == cleanTx.targetAssetId || (cleanTx.targetAssetName.isNotBlank() && asset.name.equals(cleanTx.targetAssetName, ignoreCase = true))) {
                        asset.copy(
                            value = (asset.value - cleanTx.amount).coerceAtLeast(0.0),
                            records = asset.records.filterNot { it.id == recId || it.id == assetRecId || it.id == cleanTx.id }
                        )
                    } else asset
                }
            }
        }

        // Reverse debt payment if transferred to debt
        if ((cleanTx.type == TransactionType.TRANSFER || cleanTx.category.equals("Pembayaran Utang", ignoreCase = true)) &&
            (cleanTx.targetDebtId.isNotBlank() || cleanTx.targetDebtName.isNotBlank())) {
            val recId = "tx_${cleanTx.id}"
            _debts.update { list ->
                list.map { debt ->
                    if (debt.id == cleanTx.targetDebtId || (cleanTx.targetDebtName.isNotBlank() && debt.name.equals(cleanTx.targetDebtName, ignoreCase = true))) {
                        val newPaid = (debt.paidAmount - cleanTx.amount).coerceAtLeast(0.0)
                        debt.copy(
                            paidAmount = newPaid,
                            records = debt.records.filterNot { it.id == recId || it.id == cleanTx.id }
                        )
                    } else debt
                }
            }
        }
        saveToDisk()
    }

    fun updateTransaction(updatedTx: Transaction) {
        val oldTxRaw = _transactions.value.find { it.id == updatedTx.id } ?: return
        val oldTx = TransactionMathHelper.sanitizeTransaction(oldTxRaw)
        val newTx = TransactionMathHelper.sanitizeTransaction(updatedTx)

        _transactions.update { list ->
            list.map { if (it.id == updatedTx.id) newTx else it }
                .sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator)
        }

        // Revert old effect and apply new effect on wallets
        _wallets.update { list ->
            list.map { wallet ->
                var bal = wallet.balance
                // 1. Revert old transaction effect
                if (wallet.name.equals(oldTx.accountName, ignoreCase = true)) {
                    bal = TransactionMathHelper.revertTransactionFromBalance(bal, oldTx, isSourceAccount = true)
                }
                if (oldTx.type == TransactionType.TRANSFER && oldTx.toAccountName.isNotBlank() && wallet.name.equals(oldTx.toAccountName, ignoreCase = true)) {
                    bal = TransactionMathHelper.revertTransactionFromBalance(bal, oldTx, isSourceAccount = false)
                }

                // 2. Apply new transaction effect
                if (wallet.name.equals(newTx.accountName, ignoreCase = true)) {
                    bal = TransactionMathHelper.applyTransactionToBalance(bal, newTx, isSourceAccount = true)
                }
                if (newTx.type == TransactionType.TRANSFER && newTx.toAccountName.isNotBlank() && wallet.name.equals(newTx.toAccountName, ignoreCase = true)) {
                    bal = TransactionMathHelper.applyTransactionToBalance(bal, newTx, isSourceAccount = false)
                }

                if (wallet.name.equals(newTx.accountName, ignoreCase = true)) {
                    wallet.copy(
                        balance = bal,
                        lastCategory = newTx.category,
                        lastAmount = newTx.amount,
                        lastActivityTime = "Baru saja"
                    )
                } else if (bal != wallet.balance) {
                    wallet.copy(balance = bal)
                } else {
                    wallet
                }
            }
        }

        // Revert old asset and apply new asset
        if (oldTx.type == TransactionType.TRANSFER || newTx.type == TransactionType.TRANSFER) {
            val monthNames = listOf("", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
            val monthStr = if (newTx.monthIndex in 1..12) monthNames[newTx.monthIndex] else "Okt"
            val oldRecId = "tx_${oldTx.id}"
            val newRecId = "tx_${newTx.id}"

            _assets.update { list ->
                list.map { asset ->
                    var v = asset.value
                    var recs = asset.records
                    if (oldTx.type == TransactionType.TRANSFER && (asset.id == oldTx.targetAssetId || asset.name.equals(oldTx.targetAssetName, ignoreCase = true))) {
                        v = (v - oldTx.amount).coerceAtLeast(0.0)
                        recs = recs.filterNot { it.id == oldRecId || it.id == newRecId }
                    }
                    if (newTx.type == TransactionType.TRANSFER && (asset.id == newTx.targetAssetId || asset.name.equals(newTx.targetAssetName, ignoreCase = true))) {
                        v += newTx.amount
                        val record = AssetRecord(
                            id = newRecId,
                            assetId = asset.id,
                            tag = "Transfer",
                            isIncrease = true,
                            amount = newTx.amount,
                            sourceWalletName = newTx.accountName,
                            date = "${newTx.dayOfMonth} $monthStr ${newTx.year}",
                            time = newTx.time.ifBlank { "12:00" },
                            note = if (newTx.note.isNotBlank()) newTx.note else "Transfer dari ${newTx.accountName}"
                        )
                        recs = listOf(record) + recs
                    }
                    asset.copy(value = v, records = recs)
                }
            }
        }

        // Revert old debt and apply new debt if transferred to or paying debt
        val isOldDebt = (oldTx.type == TransactionType.TRANSFER || oldTx.category.equals("Pembayaran Utang", ignoreCase = true)) &&
                (oldTx.targetDebtId.isNotBlank() || oldTx.targetDebtName.isNotBlank())
        val isNewDebt = (newTx.type == TransactionType.TRANSFER || newTx.category.equals("Pembayaran Utang", ignoreCase = true)) &&
                (newTx.targetDebtId.isNotBlank() || newTx.targetDebtName.isNotBlank())

        if (isOldDebt || isNewDebt) {
            val monthNames = listOf("", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
            val monthStr = if (newTx.monthIndex in 1..12) monthNames[newTx.monthIndex] else "Okt"
            val oldRecId = "tx_${oldTx.id}"
            val debtRecId = if (oldTx.id.startsWith("debt_rec_")) oldTx.id.removePrefix("debt_rec_") else ""
            val newRecId = if (newTx.id.startsWith("debt_rec_")) newTx.id.removePrefix("debt_rec_") else "tx_${newTx.id}"

            _debts.update { list ->
                list.map { debt ->
                    var paid = debt.paidAmount
                    var recs = debt.records
                    if (isOldDebt && (debt.id == oldTx.targetDebtId || debt.name.equals(oldTx.targetDebtName, ignoreCase = true))) {
                        paid = (paid - oldTx.amount).coerceAtLeast(0.0)
                        recs = recs.filterNot { it.id == oldRecId || it.id == debtRecId || it.id == oldTx.id }
                    }
                    if (isNewDebt && (debt.id == newTx.targetDebtId || debt.name.equals(newTx.targetDebtName, ignoreCase = true))) {
                        paid += newTx.amount
                        val record = DebtRecord(
                            id = newRecId,
                            debtId = debt.id,
                            type = "Pembayaran",
                            amount = newTx.amount,
                            date = "${newTx.dayOfMonth} $monthStr ${newTx.year}",
                            time = newTx.time.ifBlank { "12:00" },
                            note = if (newTx.note.isNotBlank()) newTx.note else "Transfer dari ${newTx.accountName}",
                            sourceWalletName = newTx.accountName
                        )
                        recs = listOf(record) + recs
                    }
                    debt.copy(paidAmount = paid, records = recs)
                }
            }
        }
        saveToDisk()
    }

    fun batchDeleteTransactions(txIds: Set<String>) {
        if (txIds.isEmpty()) return
        val targets = _transactions.value.filter { it.id in txIds }.map { TransactionMathHelper.sanitizeTransaction(it) }
        _transactions.update { it.filterNot { item -> item.id in txIds } }

        // Reverse wallet balance for all deleted transactions
        _wallets.update { list ->
            list.map { wallet ->
                var bal = wallet.balance
                targets.forEach { target ->
                    val isSource = wallet.name.equals(target.accountName, ignoreCase = true)
                    val isDest = target.toAccountName.isNotBlank() && wallet.name.equals(target.toAccountName, ignoreCase = true)

                    if (isSource) {
                        bal = TransactionMathHelper.revertTransactionFromBalance(bal, target, isSourceAccount = true)
                    }
                    if (isDest && target.type == TransactionType.TRANSFER) {
                        bal = TransactionMathHelper.revertTransactionFromBalance(bal, target, isSourceAccount = false)
                    }
                }
                wallet.copy(balance = bal)
            }
        }

        // Reverse asset records for all deleted transfers to assets
        targets.filter { it.type == TransactionType.TRANSFER && (it.targetAssetId.isNotBlank() || it.targetAssetName.isNotBlank()) }.forEach { target ->
            val recId = "tx_${target.id}"
            val assetRecId = "asset_rec_${target.id}"
            _assets.update { list ->
                list.map { asset ->
                    if (asset.id == target.targetAssetId || (target.targetAssetName.isNotBlank() && asset.name.equals(target.targetAssetName, ignoreCase = true))) {
                        asset.copy(
                            value = (asset.value - target.amount).coerceAtLeast(0.0),
                            records = asset.records.filterNot { it.id == recId || it.id == assetRecId || it.id == target.id }
                        )
                    } else asset
                }
            }
        }

        // Reverse debt records for all deleted debt payments
        targets.filter { (it.type == TransactionType.TRANSFER || it.category.equals("Pembayaran Utang", ignoreCase = true)) && (it.targetDebtId.isNotBlank() || it.targetDebtName.isNotBlank()) }.forEach { target ->
            val recId = "tx_${target.id}"
            val debtRecId = if (target.id.startsWith("debt_rec_")) target.id.removePrefix("debt_rec_") else ""
            _debts.update { list ->
                list.map { debt ->
                    if (debt.id == target.targetDebtId || (target.targetDebtName.isNotBlank() && debt.name.equals(target.targetDebtName, ignoreCase = true))) {
                        debt.copy(
                            paidAmount = (debt.paidAmount - target.amount).coerceAtLeast(0.0),
                            records = debt.records.filterNot { it.id == recId || it.id == debtRecId || it.id == target.id }
                        )
                    } else debt
                }
            }
        }

        saveToDisk()
    }

    fun batchUpdateCategory(txIds: Set<String>, newCategory: String, newSubcategory: String = "") {
        if (txIds.isEmpty()) return
        val matchingCat = _categories.value.find { it.name.equals(newCategory, ignoreCase = true) }
        val newType = matchingCat?.type ?: when (newCategory) {
            "Salary", "Extra Income", "Bonus", "Investasi", "Pemberian" -> TransactionType.INCOME
            "Transfer", "Transfer Antar Bank", "Asset", "Aset", "Top Up", "Top Up E-Wallet" -> TransactionType.TRANSFER
            else -> TransactionType.EXPENSE
        }
        _transactions.update { list ->
            list.map { tx ->
                if (tx.id in txIds) {
                    tx.copy(category = newCategory, subcategory = newSubcategory, type = newType)
                } else tx
            }
        }
        saveToDisk()
    }

    fun batchUpdateLabel(txIds: Set<String>, newLabel: String) {
        if (txIds.isEmpty()) return
        _transactions.update { list ->
            list.map { tx ->
                if (tx.id in txIds) {
                    tx.copy(label = newLabel)
                } else tx
            }
        }
        saveToDisk()
    }

    fun addWallet(wallet: AccountWallet) {
        _wallets.update { it + wallet }
        IndonesianInstitutionHelper.registerCustomLogo(wallet.name, wallet.iconName)
        IndonesianInstitutionHelper.syncWithWalletsAndAssets(_wallets.value, _assets.value)
        saveToDisk()
    }

    fun updateWallet(wallet: AccountWallet) {
        val oldWallet = _wallets.value.find { it.id == wallet.id }
        _wallets.update { list -> list.map { if (it.id == wallet.id) wallet else it } }
        IndonesianInstitutionHelper.registerCustomLogo(wallet.name, wallet.iconName)
        if (oldWallet != null && oldWallet.name != wallet.name) {
            IndonesianInstitutionHelper.registerCustomLogo(oldWallet.name, wallet.iconName)
            _transactions.update { list ->
                list.map { tx ->
                    var updatedTx = tx
                    if (tx.accountName.equals(oldWallet.name, ignoreCase = true)) {
                        updatedTx = updatedTx.copy(accountName = wallet.name)
                    }
                    if (tx.toAccountName.equals(oldWallet.name, ignoreCase = true)) {
                        updatedTx = updatedTx.copy(toAccountName = wallet.name)
                    }
                    updatedTx
                }
            }
        }
        IndonesianInstitutionHelper.syncWithWalletsAndAssets(_wallets.value, _assets.value)
        saveToDisk()
    }

    fun deleteWallet(walletId: String) {
        _wallets.update { list -> list.filterNot { it.id == walletId } }
        IndonesianInstitutionHelper.syncWithWalletsAndAssets(_wallets.value, _assets.value)
        saveToDisk()
    }

    fun addAsset(asset: AssetItem) {
        _assets.update { it + asset }
        IndonesianInstitutionHelper.registerCustomLogo(asset.name, asset.iconUri)
        IndonesianInstitutionHelper.syncWithWalletsAndAssets(_wallets.value, _assets.value)
        saveToDisk()
    }

    fun updateAsset(asset: AssetItem) {
        val oldAsset = _assets.value.find { it.id == asset.id }
        val syncedAsset = if (asset.isStock && asset.lotCount > 0.0 && asset.currentPrice > 0.0) {
            asset.copy(value = asset.lotCount * 100.0 * asset.currentPrice)
        } else {
            asset
        }
        _assets.update { list -> list.map { if (it.id == syncedAsset.id) syncedAsset else it } }
        IndonesianInstitutionHelper.registerCustomLogo(syncedAsset.name, syncedAsset.iconUri)
        if (oldAsset != null && oldAsset.name != syncedAsset.name) {
            IndonesianInstitutionHelper.registerCustomLogo(oldAsset.name, syncedAsset.iconUri)
            _transactions.update { list ->
                list.map { tx ->
                    if (tx.targetAssetId == syncedAsset.id || tx.targetAssetName.equals(oldAsset.name, ignoreCase = true)) {
                        tx.copy(targetAssetName = syncedAsset.name)
                    } else tx
                }
            }
        }
        IndonesianInstitutionHelper.syncWithWalletsAndAssets(_wallets.value, _assets.value)
        saveToDisk()
    }

    fun updateStockCurrentPrice(assetId: String, currentPrice: Double, dailyChange: Double = 0.0, dailyChangePercent: Double = 0.0) {
        _assets.update { list ->
            list.map { asset ->
                if (asset.id == assetId) {
                    val updatedVal = if (asset.isStock && asset.lotCount > 0.0) asset.lotCount * 100.0 * currentPrice else asset.value
                    asset.copy(
                        currentPrice = currentPrice,
                        dailyPriceChange = dailyChange,
                        dailyPriceChangePercent = dailyChangePercent,
                        lastPriceUpdate = System.currentTimeMillis(),
                        value = updatedVal
                    )
                } else asset
            }
        }
        saveToDisk()
    }

    suspend fun refreshStockPricesFromTradingView(): Map<String, TradingViewQuote> {
        val stockAssets = _assets.value.filter { it.isStock || it.tickerSymbol.isNotBlank() }
        if (stockAssets.isEmpty()) return emptyMap()

        val symbols = stockAssets.map { it.resolvedTicker.ifBlank { "IDX:BBRI" } }.distinct()
        val quotes = TradingViewHelper.fetchLiveQuotes(symbols)
        if (quotes.isEmpty()) return quotes

        var hasChanged = false
        val updatedAssets = _assets.value.map { asset ->
            if (asset.isStock || asset.tickerSymbol.isNotBlank()) {
                val ticker = asset.resolvedTicker
                val quote = quotes[ticker]
                    ?: quotes[TradingViewHelper.formatSymbol(ticker)]
                    ?: quotes[TradingViewHelper.extractTickerCode(asset.name)]
                if (quote != null && quote.closePrice > 0.0) {
                    hasChanged = true
                    val updatedVal = if (asset.lotCount > 0.0) asset.lotCount * 100.0 * quote.closePrice else asset.value
                    val updatedPerf = if (asset.averagePrice > 0.0) {
                        ((quote.closePrice - asset.averagePrice) / asset.averagePrice) * 100.0
                    } else quote.changePercent
                    asset.copy(
                        currentPrice = quote.closePrice,
                        dailyPriceChange = quote.changeAbsolute,
                        dailyPriceChangePercent = quote.changePercent,
                        lastPriceUpdate = quote.timestamp,
                        performancePercent = updatedPerf,
                        value = updatedVal
                    )
                } else asset
            } else asset
        }

        if (hasChanged) {
            _assets.value = updatedAssets
            saveToDisk()
        }
        return quotes
    }

    fun deleteAsset(assetId: String) {
        _assets.update { list -> list.filterNot { it.id == assetId } }
        IndonesianInstitutionHelper.syncWithWalletsAndAssets(_wallets.value, _assets.value)
        saveToDisk()
    }

    data class ParsedDate(val day: Int, val month: Int, val year: Int, val dayOfWeek: String)

    fun parseDateString(dateStr: String): ParsedDate {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val currentMonth = cal.get(Calendar.MONTH) + 1
        val currentYear = cal.get(Calendar.YEAR)
        val dayNames = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

        val clean = dateStr.split("·").firstOrNull()?.trim() ?: dateStr.trim()
        val tokens = when {
            clean.contains("/") -> clean.split("/").map { it.trim() }
            clean.contains("-") -> clean.split("-").map { it.trim() }
            else -> clean.split(" ").filter { it.isNotBlank() }
        }

        var day = currentDay
        var month = currentMonth
        var year = currentYear

        if (clean.contains("/") || (clean.contains("-") && (tokens.firstOrNull()?.length ?: 0) != 4)) {
            day = tokens.getOrNull(0)?.toIntOrNull() ?: currentDay
            month = tokens.getOrNull(1)?.toIntOrNull() ?: currentMonth
            year = tokens.getOrNull(2)?.toIntOrNull() ?: currentYear
        } else if (clean.contains("-") && (tokens.firstOrNull()?.length ?: 0) == 4) {
            year = tokens.getOrNull(0)?.toIntOrNull() ?: currentYear
            month = tokens.getOrNull(1)?.toIntOrNull() ?: currentMonth
            day = tokens.getOrNull(2)?.toIntOrNull() ?: currentDay
        } else {
            day = tokens.getOrNull(0)?.filter { it.isDigit() }?.toIntOrNull() ?: currentDay
            val monthToken = tokens.getOrNull(1)?.lowercase() ?: ""
            month = when {
                monthToken.startsWith("jan") -> 1
                monthToken.startsWith("feb") -> 2
                monthToken.startsWith("mar") -> 3
                monthToken.startsWith("apr") -> 4
                monthToken.startsWith("mei") || monthToken.startsWith("may") -> 5
                monthToken.startsWith("jun") -> 6
                monthToken.startsWith("jul") -> 7
                monthToken.startsWith("agu") || monthToken.startsWith("aug") -> 8
                monthToken.startsWith("sep") -> 9
                monthToken.startsWith("okt") || monthToken.startsWith("oct") -> 10
                monthToken.startsWith("nov") -> 11
                monthToken.startsWith("des") || monthToken.startsWith("dec") -> 12
                else -> currentMonth
            }
            year = tokens.getOrNull(2)?.filter { it.isDigit() }?.toIntOrNull() ?: currentYear
        }

        val testCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, (month - 1).coerceIn(0, 11))
            set(Calendar.DAY_OF_MONTH, day.coerceIn(1, 31))
        }
        val dayOfWeek = dayNames[(testCal.get(Calendar.DAY_OF_WEEK) - 1 + 7) % 7]
        return ParsedDate(day, month, year, dayOfWeek)
    }

    fun syncDebtPaymentsWithTransactions() {
        val currentTxs = _transactions.value.toMutableList()
        var modified = false
        val existingTxIds = currentTxs.map { it.id }.toSet()

        for (debt in _debts.value) {
            val isLent = debt.type == DebtType.LENT
            val defaultWallet = if (debt.linkedWalletName.isNotBlank()) debt.linkedWalletName
                                else _wallets.value.firstOrNull()?.name ?: "BRI Utama"

            for (rec in debt.records) {
                val txId = "debt_rec_${rec.id}"
                val alreadyExists = existingTxIds.contains(txId) ||
                                    currentTxs.any { it.id == "tx_${rec.id}" || it.id == rec.id }
                if (!alreadyExists) {
                    val parsed = parseDateString(rec.date)
                    val wallet = if (rec.sourceWalletName.isNotBlank()) rec.sourceWalletName else defaultWallet
                    val recType = rec.type

                    val (txType, txCat, txTitle) = when (recType) {
                        "Pembayaran" -> {
                            if (isLent) {
                                Triple(
                                    TransactionType.INCOME,
                                    "Pendapatan",
                                    if (rec.note.isNotBlank()) rec.note else "Pelunasan Piutang: ${debt.name}"
                                )
                            } else {
                                Triple(
                                    TransactionType.EXPENSE,
                                    "Pembayaran Utang",
                                    if (rec.note.isNotBlank()) rec.note else "Pembayaran Utang: ${debt.name}"
                                )
                            }
                        }
                        "Pinjam Lagi" -> {
                            if (isLent) {
                                Triple(
                                    TransactionType.EXPENSE,
                                    "Pengeluaran",
                                    if (rec.note.isNotBlank()) rec.note else "Pemberian Pinjaman: ${debt.name}"
                                )
                            } else {
                                Triple(
                                    TransactionType.INCOME,
                                    "Pendapatan",
                                    if (rec.note.isNotBlank()) rec.note else "Penerimaan Pinjaman: ${debt.name}"
                                )
                            }
                        }
                        else -> {
                            Triple(
                                TransactionType.EXPENSE,
                                "Bunga Utang",
                                if (rec.note.isNotBlank()) rec.note else "Bunga Utang: ${debt.name}"
                            )
                        }
                    }

                    val tx = Transaction(
                        id = txId,
                        title = txTitle,
                        category = txCat,
                        amount = rec.amount,
                        type = txType,
                        accountName = wallet,
                        toAccountName = if (!isLent && recType == "Pembayaran") debt.name else "",
                        dayOfMonth = parsed.day,
                        monthIndex = parsed.month,
                        year = parsed.year,
                        dayOfWeekName = parsed.dayOfWeek,
                        note = rec.note,
                        time = rec.time.ifBlank { "12:00" },
                        targetDebtId = debt.id,
                        targetDebtName = debt.name
                    )
                    currentTxs.add(tx)
                    modified = true
                }
            }
        }

        if (modified) {
            _transactions.value = currentTxs.sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator)
            saveToDisk()
        }
    }

    fun addAssetRecord(assetId: String, record: AssetRecord, updateWalletBalance: Boolean = true) {
        _assets.update { list ->
            list.map { asset ->
                if (asset.id == assetId) {
                    val newRecords = listOf(record) + asset.records
                    if (asset.isStock && record.lotCount > 0.0) {
                        val recordPrice = if (record.pricePerUnit > 0.0) {
                            record.pricePerUnit
                        } else {
                            record.amount / (record.lotCount * 100.0)
                        }
                        if (record.isIncrease) {
                            val newLot = asset.lotCount + record.lotCount
                            val currentAvg = if (asset.averagePrice > 0.0) asset.averagePrice else recordPrice
                            val newAvg = if (newLot > 0.0) {
                                ((asset.lotCount * currentAvg) + (record.lotCount * recordPrice)) / newLot
                            } else recordPrice
                            val currentP = if (asset.currentPrice > 0.0) asset.currentPrice else newAvg
                            val newValue = newLot * 100.0 * currentP
                            asset.copy(
                                lotCount = newLot,
                                averagePrice = newAvg,
                                value = newValue,
                                records = newRecords
                            )
                        } else {
                            val newLot = (asset.lotCount - record.lotCount).coerceAtLeast(0.0)
                            val currentP = if (asset.currentPrice > 0.0) asset.currentPrice else asset.averagePrice
                            val newValue = newLot * 100.0 * currentP
                            asset.copy(
                                lotCount = newLot,
                                value = newValue,
                                records = newRecords
                            )
                        }
                    } else {
                        val newValue = if (record.isIncrease) asset.value + record.amount else (asset.value - record.amount).coerceAtLeast(0.0)
                        asset.copy(value = newValue, records = newRecords)
                    }
                } else asset
            }
        }
        if (updateWalletBalance && record.sourceWalletName.isNotBlank()) {
            _wallets.update { list ->
                list.map { wallet ->
                    if (wallet.name.equals(record.sourceWalletName, ignoreCase = true)) {
                        val newBal = if (record.isIncrease) wallet.balance - record.amount else wallet.balance + record.amount
                        wallet.copy(balance = newBal, lastActivityTime = "Baru saja")
                    } else wallet
                }
            }
            // Sinkronkan ke riwayat pencatatan transaksi utama (TransactionScreen)
            val targetAsset = _assets.value.find { it.id == assetId }
            val assetName = targetAsset?.name ?: "Aset"
            val parsed = parseDateString(record.date)
            val tx = Transaction(
                id = "asset_rec_${record.id}",
                title = if (record.note.isNotBlank()) record.note else "Transfer ke $assetName",
                category = "Asset",
                amount = record.amount,
                type = TransactionType.TRANSFER,
                accountName = record.sourceWalletName,
                dayOfMonth = parsed.day,
                monthIndex = parsed.month,
                year = parsed.year,
                dayOfWeekName = parsed.dayOfWeek,
                note = record.note,
                time = record.time.ifBlank { "12:00" },
                targetAssetId = assetId,
                targetAssetName = assetName
            )
            _transactions.update { list -> listOf(tx) + list.filterNot { it.id == tx.id } }
        }
        saveToDisk()
    }

    fun updateAssetRecord(assetId: String, oldRecord: AssetRecord, updatedRecord: AssetRecord, updateWalletBalance: Boolean = true) {
        _assets.update { list ->
            list.map { asset ->
                if (asset.id == assetId) {
                    val newRecords = asset.records.map { if (it.id == updatedRecord.id) updatedRecord else it }
                    val revertedValue = if (oldRecord.isIncrease) asset.value - oldRecord.amount else asset.value + oldRecord.amount
                    val finalValue = if (updatedRecord.isIncrease) revertedValue + updatedRecord.amount else (revertedValue - updatedRecord.amount).coerceAtLeast(0.0)
                    asset.copy(value = finalValue, records = newRecords)
                } else asset
            }
        }
        if (updateWalletBalance) {
            if (oldRecord.sourceWalletName.isNotBlank() || updatedRecord.sourceWalletName.isNotBlank()) {
                _wallets.update { list ->
                    list.map { wallet ->
                        var b = wallet.balance
                        if (oldRecord.sourceWalletName.isNotBlank() && wallet.name.equals(oldRecord.sourceWalletName, ignoreCase = true)) {
                            b = if (oldRecord.isIncrease) b + oldRecord.amount else b - oldRecord.amount
                        }
                        if (updatedRecord.sourceWalletName.isNotBlank() && wallet.name.equals(updatedRecord.sourceWalletName, ignoreCase = true)) {
                            b = if (updatedRecord.isIncrease) b - updatedRecord.amount else b + updatedRecord.amount
                        }
                        if (b != wallet.balance) wallet.copy(balance = b, lastActivityTime = "Baru saja") else wallet
                    }
                }
            }
            // Update juga di riwayat pencatatan transaksi utama
            val targetAsset = _assets.value.find { it.id == assetId }
            val assetName = targetAsset?.name ?: "Aset"
            if (updatedRecord.sourceWalletName.isNotBlank()) {
                val parsed = parseDateString(updatedRecord.date)
                val tx = Transaction(
                    id = "asset_rec_${updatedRecord.id}",
                    title = if (updatedRecord.note.isNotBlank()) updatedRecord.note else "Transfer ke $assetName",
                    category = "Asset",
                    amount = updatedRecord.amount,
                    type = TransactionType.TRANSFER,
                    accountName = updatedRecord.sourceWalletName,
                    dayOfMonth = parsed.day,
                    monthIndex = parsed.month,
                    year = parsed.year,
                    dayOfWeekName = parsed.dayOfWeek,
                    note = updatedRecord.note,
                    time = updatedRecord.time.ifBlank { "12:00" },
                    targetAssetId = assetId,
                    targetAssetName = assetName
                )
                _transactions.update { list ->
                    val exists = list.any { it.id == tx.id }
                    if (exists) list.map { if (it.id == tx.id) tx else it } else listOf(tx) + list
                }
            } else {
                _transactions.update { list -> list.filterNot { it.id == "asset_rec_${updatedRecord.id}" } }
            }
        }
        saveToDisk()
    }

    fun deleteAssetRecord(assetId: String, recordId: String, revertWalletBalance: Boolean = true) {
        var targetRecord: AssetRecord? = null
        _assets.update { list ->
            list.map { asset ->
                if (asset.id == assetId) {
                    targetRecord = asset.records.find { it.id == recordId }
                    val rec = targetRecord
                    val newRecords = asset.records.filterNot { it.id == recordId }
                    if (asset.isStock && rec != null && rec.lotCount > 0.0) {
                        val newLot = if (rec.isIncrease) {
                            (asset.lotCount - rec.lotCount).coerceAtLeast(0.0)
                        } else {
                            asset.lotCount + rec.lotCount
                        }
                        val currentP = if (asset.currentPrice > 0.0) asset.currentPrice else asset.averagePrice
                        val finalValue = newLot * 100.0 * currentP
                        asset.copy(lotCount = newLot, value = finalValue, records = newRecords)
                    } else {
                        val finalValue = if (rec != null) {
                            if (rec.isIncrease) (asset.value - rec.amount).coerceAtLeast(0.0) else asset.value + rec.amount
                        } else asset.value
                        asset.copy(value = finalValue, records = newRecords)
                    }
                } else asset
            }
        }
        val rec = targetRecord
        if (revertWalletBalance && rec != null && rec.sourceWalletName.isNotBlank()) {
            _wallets.update { list ->
                list.map { wallet ->
                    if (wallet.name.equals(rec.sourceWalletName, ignoreCase = true)) {
                        val b = if (rec.isIncrease) wallet.balance + rec.amount else wallet.balance - rec.amount
                        wallet.copy(balance = b)
                    } else wallet
                }
            }
            // Hapus dari riwayat pencatatan transaksi utama
            _transactions.update { list ->
                list.filterNot { it.id == "asset_rec_$recordId" || it.id == "tx_$recordId" }
            }
        }
        saveToDisk()
    }

    fun addDebt(debt: DebtItem, depositToWalletName: String? = null) {
        _debts.update { it + debt }
        if (!depositToWalletName.isNullOrBlank()) {
            val parsed = parseDateString(debt.startDate)
            if (debt.type == DebtType.BORROWED) {
                _wallets.update { list ->
                    list.map { w ->
                        if (w.name.equals(depositToWalletName, ignoreCase = true)) {
                            w.copy(balance = w.balance + debt.totalAmount)
                        } else w
                    }
                }
                _transactions.update { txs ->
                    listOf(
                        Transaction(
                            id = "debt_init_${debt.id}",
                            title = "Penerimaan Pinjaman: ${debt.name}",
                            category = "Pendapatan",
                            amount = debt.totalAmount,
                            type = TransactionType.INCOME,
                            accountName = depositToWalletName,
                            dayOfMonth = parsed.day,
                            monthIndex = parsed.month,
                            year = parsed.year,
                            dayOfWeekName = parsed.dayOfWeek,
                            note = "Pencairan dana utang ${debt.name}",
                            targetDebtId = debt.id,
                            targetDebtName = debt.name
                        )
                    ) + txs
                }
            } else {
                _wallets.update { list ->
                    list.map { w ->
                        if (w.name.equals(depositToWalletName, ignoreCase = true)) {
                            w.copy(balance = (w.balance - debt.totalAmount).coerceAtLeast(0.0))
                        } else w
                    }
                }
                _transactions.update { txs ->
                    listOf(
                        Transaction(
                            id = "debt_init_${debt.id}",
                            title = "Pemberian Pinjaman: ${debt.name}",
                            category = "Transfer",
                            amount = debt.totalAmount,
                            type = TransactionType.EXPENSE,
                            accountName = depositToWalletName,
                            dayOfMonth = parsed.day,
                            monthIndex = parsed.month,
                            year = parsed.year,
                            dayOfWeekName = parsed.dayOfWeek,
                            note = "Pemberian pinjaman untuk ${debt.name}",
                            targetDebtId = debt.id,
                            targetDebtName = debt.name
                        )
                    ) + txs
                }
            }
        }
        syncDebtPaymentsWithTransactions()
        saveToDisk()
    }

    fun updateDebt(debt: DebtItem) {
        _debts.update { list -> list.map { if (it.id == debt.id) debt else it } }
        saveToDisk()
    }

    fun markDebtFullyPaid(debtId: String) {
        val targetDebt = _debts.value.firstOrNull { it.id == debtId } ?: return
        val remaining = (targetDebt.totalAmount - targetDebt.paidAmount).coerceAtLeast(0.0)
        val isLent = targetDebt.type == DebtType.LENT
        val linkedWallet = targetDebt.linkedWalletName.ifBlank { _wallets.value.firstOrNull()?.name ?: "BRI Utama" }

        val cal = Calendar.getInstance()
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val monthIndex = cal.get(Calendar.MONTH) + 1
        val monthStr = monthNames.getOrElse(cal.get(Calendar.MONTH)) { "Nov" }
        val year = cal.get(Calendar.YEAR)
        val dateStr = "$day $monthStr $year"
        val timeStr = String.format("%02d.%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        val dayNames = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
        val dayOfWeek = dayNames[(cal.get(Calendar.DAY_OF_WEEK) - 1 + 7) % 7]

        val newRecordId = java.util.UUID.randomUUID().toString()
        val newRecord = if (remaining > 0) {
            DebtRecord(
                id = newRecordId,
                debtId = targetDebt.id,
                type = "Pembayaran",
                amount = remaining,
                date = dateStr,
                time = timeStr,
                note = "Pelunasan penuh",
                sourceWalletName = linkedWallet
            )
        } else null

        _debts.update { list ->
            list.map { debt ->
                if (debt.id == debtId) {
                    val updatedRecords = if (newRecord != null) debt.records + newRecord else debt.records
                    debt.copy(paidAmount = debt.totalAmount, records = updatedRecords)
                } else debt
            }
        }

        if (remaining > 0 && linkedWallet.isNotBlank()) {
            if (isLent) {
                // Uang yang dipinjamkan telah lunas dibayar kembali -> masuk ke saldo akun penerima (bertambah)
                _wallets.update { list ->
                    list.map { w ->
                        if (w.name.equals(linkedWallet, ignoreCase = true)) {
                            w.copy(balance = w.balance + remaining)
                        } else w
                    }
                }
                _transactions.update { txs ->
                    listOf(
                        Transaction(
                            id = "debt_rec_$newRecordId",
                            title = "Pelunasan Piutang: ${targetDebt.name}",
                            category = "Pendapatan",
                            amount = remaining,
                            type = TransactionType.INCOME,
                            accountName = linkedWallet,
                            dayOfMonth = day,
                            monthIndex = monthIndex,
                            year = year,
                            dayOfWeekName = dayOfWeek,
                            note = "Pelunasan penuh piutang ${targetDebt.name}",
                            time = timeStr,
                            targetDebtId = debtId,
                            targetDebtName = targetDebt.name
                        )
                    ) + txs
                }
            } else {
                // Utang yang dipinjam dilunasi oleh user -> keluar dari saldo akun (berkurang)
                _wallets.update { list ->
                    list.map { w ->
                        if (w.name.equals(linkedWallet, ignoreCase = true)) {
                            w.copy(balance = (w.balance - remaining).coerceAtLeast(0.0))
                        } else w
                    }
                }
                _transactions.update { txs ->
                    listOf(
                        Transaction(
                            id = "debt_rec_$newRecordId",
                            title = "Pelunasan Utang: ${targetDebt.name}",
                            category = "Pembayaran Utang",
                            amount = remaining,
                            type = TransactionType.EXPENSE,
                            accountName = linkedWallet,
                            toAccountName = targetDebt.name,
                            dayOfMonth = day,
                            monthIndex = monthIndex,
                            year = year,
                            dayOfWeekName = dayOfWeek,
                            note = "Pelunasan penuh utang ${targetDebt.name}",
                            time = timeStr,
                            targetDebtId = debtId,
                            targetDebtName = targetDebt.name
                        )
                    ) + txs
                }
            }
        }
        saveToDisk()
    }

    fun addDebtRecord(debtId: String, record: DebtRecord, affectWalletName: String? = null) {
        _debts.update { list ->
            list.map { debt ->
                if (debt.id == debtId) {
                    val updatedRecords = debt.records + record
                    when (record.type) {
                        "Pembayaran" -> {
                            val newPaid = (debt.paidAmount + record.amount).coerceAtMost(debt.totalAmount)
                            debt.copy(paidAmount = newPaid, records = updatedRecords)
                        }
                        "Pinjam Lagi" -> {
                            val newTotal = debt.totalAmount + record.amount
                            debt.copy(totalAmount = newTotal, records = updatedRecords)
                        }
                        "Bunga" -> {
                            val newTotal = debt.totalAmount + record.amount
                            debt.copy(totalAmount = newTotal, records = updatedRecords)
                        }
                        else -> debt.copy(records = updatedRecords)
                    }
                } else debt
            }
        }

        val d = _debts.value.firstOrNull { it.id == debtId }
        val isLent = d?.type == DebtType.LENT
        val targetWallet = if (!affectWalletName.isNullOrBlank()) affectWalletName else record.sourceWalletName
        val walletForTx = if (!targetWallet.isNullOrBlank()) targetWallet
                          else if (!d?.linkedWalletName.isNullOrBlank()) d.linkedWalletName
                          else _wallets.value.firstOrNull()?.name ?: "BRI Utama"

        val parsed = parseDateString(record.date)
        val recTime = record.time.ifBlank { "12:00" }

        when (record.type) {
            "Pembayaran" -> {
                if (isLent) {
                    // Uang yang dipinjamkan telah dibayar kembali oleh peminjam -> SALDO AKUN BERTAMBAH
                    if (!targetWallet.isNullOrBlank()) {
                        _wallets.update { list ->
                            list.map { w ->
                                if (w.name.equals(targetWallet, ignoreCase = true)) {
                                    w.copy(balance = w.balance + record.amount)
                                } else w
                            }
                        }
                    }
                    val newTx = Transaction(
                        id = "debt_rec_${record.id}",
                        title = if (record.note.isNotBlank()) record.note else "Pelunasan Piutang: ${d?.name ?: "Piutang"}",
                        category = "Pendapatan",
                        amount = record.amount,
                        type = TransactionType.INCOME,
                        accountName = walletForTx,
                        dayOfMonth = parsed.day,
                        monthIndex = parsed.month,
                        year = parsed.year,
                        dayOfWeekName = parsed.dayOfWeek,
                        note = record.note.ifBlank { "Pembayaran piutang diterima" },
                        time = recTime,
                        targetDebtId = debtId,
                        targetDebtName = d?.name ?: ""
                    )
                    _transactions.update { txs -> (listOf(newTx) + txs.filterNot { it.id == newTx.id }).sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator) }
                } else {
                    // Utang yang dipinjam oleh user dibayar -> SALDO AKUN BERKURANG
                    if (!targetWallet.isNullOrBlank()) {
                        _wallets.update { list ->
                            list.map { w ->
                                if (w.name.equals(targetWallet, ignoreCase = true)) {
                                    w.copy(balance = (w.balance - record.amount).coerceAtLeast(0.0))
                                } else w
                            }
                        }
                    }
                    val newTx = Transaction(
                        id = "debt_rec_${record.id}",
                        title = if (record.note.isNotBlank()) record.note else "Pembayaran Utang: ${d?.name ?: "Utang"}",
                        category = "Pembayaran Utang",
                        amount = record.amount,
                        type = TransactionType.EXPENSE,
                        accountName = walletForTx,
                        toAccountName = d?.name ?: "",
                        dayOfMonth = parsed.day,
                        monthIndex = parsed.month,
                        year = parsed.year,
                        dayOfWeekName = parsed.dayOfWeek,
                        note = record.note.ifBlank { "Pembayaran cicilan utang" },
                        time = recTime,
                        targetDebtId = debtId,
                        targetDebtName = d?.name ?: ""
                    )
                    _transactions.update { txs -> (listOf(newTx) + txs.filterNot { it.id == newTx.id }).sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator) }
                }
            }
            "Pinjam Lagi" -> {
                if (isLent) {
                    // User meminjamkan uang lagi kepada orang lain -> SALDO AKUN BERKURANG
                    if (!targetWallet.isNullOrBlank()) {
                        _wallets.update { list ->
                            list.map { w ->
                                if (w.name.equals(targetWallet, ignoreCase = true)) {
                                    w.copy(balance = (w.balance - record.amount).coerceAtLeast(0.0))
                                } else w
                            }
                        }
                    }
                    val newTx = Transaction(
                        id = "debt_rec_${record.id}",
                        title = if (record.note.isNotBlank()) record.note else "Pinjaman Tambahan Diberikan: ${d?.name ?: "Piutang"}",
                        category = "Pengeluaran",
                        amount = record.amount,
                        type = TransactionType.EXPENSE,
                        accountName = walletForTx,
                        dayOfMonth = parsed.day,
                        monthIndex = parsed.month,
                        year = parsed.year,
                        dayOfWeekName = parsed.dayOfWeek,
                        note = record.note.ifBlank { "Pemberian pinjaman tambahan" },
                        time = recTime,
                        targetDebtId = debtId,
                        targetDebtName = d?.name ?: ""
                    )
                    _transactions.update { txs -> (listOf(newTx) + txs.filterNot { it.id == newTx.id }).sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator) }
                } else {
                    // User meminjam uang lagi dari pihak lain -> SALDO AKUN BERTAMBAH
                    if (!targetWallet.isNullOrBlank()) {
                        _wallets.update { list ->
                            list.map { w ->
                                if (w.name.equals(targetWallet, ignoreCase = true)) {
                                    w.copy(balance = w.balance + record.amount)
                                } else w
                            }
                        }
                    }
                    val newTx = Transaction(
                        id = "debt_rec_${record.id}",
                        title = if (record.note.isNotBlank()) record.note else "Penerimaan Pinjaman Tambahan: ${d?.name ?: "Utang"}",
                        category = "Pendapatan",
                        amount = record.amount,
                        type = TransactionType.INCOME,
                        accountName = walletForTx,
                        dayOfMonth = parsed.day,
                        monthIndex = parsed.month,
                        year = parsed.year,
                        dayOfWeekName = parsed.dayOfWeek,
                        note = record.note.ifBlank { "Penerimaan pinjaman tambahan" },
                        time = recTime,
                        targetDebtId = debtId,
                        targetDebtName = d?.name ?: ""
                    )
                    _transactions.update { txs -> (listOf(newTx) + txs.filterNot { it.id == newTx.id }).sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator) }
                }
            }
        }
        saveToDisk()
    }

    fun updateDebtRecord(debtId: String, updatedRecord: DebtRecord) {
        val oldDebt = _debts.value.firstOrNull { it.id == debtId }
        val isLent = oldDebt?.type == DebtType.LENT
        val oldRecord = oldDebt?.records?.firstOrNull { it.id == updatedRecord.id }
        _debts.update { list ->
            list.map { debt ->
                if (debt.id == debtId) {
                    val updatedRecords = debt.records.map { if (it.id == updatedRecord.id) updatedRecord else it }
                    val totalPaid = updatedRecords.filter { it.type == "Pembayaran" }.sumOf { it.amount }
                    debt.copy(paidAmount = totalPaid, records = updatedRecords)
                } else debt
            }
        }
        // Adjust wallet balances if oldRecord or updatedRecord had a sourceWalletName
        if (oldRecord != null) {
            val oldWallet = oldRecord.sourceWalletName
            val newWallet = updatedRecord.sourceWalletName

            // 1. Batalkan efek dari oldRecord pada dompet lama
            if (oldWallet.isNotBlank()) {
                _wallets.update { list ->
                    list.map { w ->
                        if (w.name.equals(oldWallet, ignoreCase = true)) {
                            val revertedBal = when (oldRecord.type) {
                                "Pembayaran" -> {
                                    if (isLent) {
                                        (w.balance - oldRecord.amount).coerceAtLeast(0.0)
                                    } else {
                                        w.balance + oldRecord.amount
                                    }
                                }
                                "Pinjam Lagi" -> {
                                    if (isLent) {
                                        w.balance + oldRecord.amount
                                    } else {
                                        (w.balance - oldRecord.amount).coerceAtLeast(0.0)
                                    }
                                }
                                else -> w.balance
                            }
                            w.copy(balance = revertedBal)
                        } else w
                    }
                }
            }

            // 2. Terapkan efek dari updatedRecord pada dompet baru
            if (newWallet.isNotBlank()) {
                _wallets.update { list ->
                    list.map { w ->
                        if (w.name.equals(newWallet, ignoreCase = true)) {
                            val newBal = when (updatedRecord.type) {
                                "Pembayaran" -> {
                                    if (isLent) {
                                        w.balance + updatedRecord.amount
                                    } else {
                                        (w.balance - updatedRecord.amount).coerceAtLeast(0.0)
                                    }
                                }
                                "Pinjam Lagi" -> {
                                    if (isLent) {
                                        (w.balance - updatedRecord.amount).coerceAtLeast(0.0)
                                    } else {
                                        w.balance + updatedRecord.amount
                                    }
                                }
                                else -> w.balance
                            }
                            w.copy(balance = newBal)
                        } else w
                    }
                }
            }

            // 3. Update transaksi terkait jika ada, atau buat baru jika belum ada
            val txId = "debt_rec_${updatedRecord.id}"
            val parsed = parseDateString(updatedRecord.date)
            val d = _debts.value.firstOrNull { it.id == debtId }
            val targetWallet = updatedRecord.sourceWalletName.ifBlank { d?.linkedWalletName.orEmpty() }.ifBlank { _wallets.value.firstOrNull()?.name ?: "BRI Utama" }

            _transactions.update { txs ->
                val exists = txs.any { it.id == txId || it.id == "tx_${updatedRecord.id}" }
                if (exists) {
                    txs.map { tx ->
                        if (tx.id == txId || tx.id == "tx_${updatedRecord.id}") {
                            val newType = if (isLent) {
                                if (updatedRecord.type == "Pembayaran") TransactionType.INCOME else TransactionType.EXPENSE
                            } else {
                                if (updatedRecord.type == "Pembayaran") TransactionType.EXPENSE else TransactionType.INCOME
                            }
                            val newCategory = if (isLent) {
                                if (updatedRecord.type == "Pembayaran") "Pendapatan" else "Pengeluaran"
                            } else {
                                if (updatedRecord.type == "Pembayaran") "Pembayaran Utang" else "Pendapatan"
                            }
                            val newTitle = if (updatedRecord.note.isNotBlank()) updatedRecord.note
                                           else if (isLent) "Pelunasan Piutang: ${d?.name ?: ""}"
                                           else "Pembayaran Utang: ${d?.name ?: ""}"
                            tx.copy(
                                title = newTitle,
                                amount = updatedRecord.amount,
                                accountName = targetWallet,
                                note = updatedRecord.note.ifBlank { tx.note },
                                type = newType,
                                category = newCategory,
                                dayOfMonth = parsed.day,
                                monthIndex = parsed.month,
                                year = parsed.year,
                                dayOfWeekName = parsed.dayOfWeek,
                                time = updatedRecord.time.ifBlank { tx.time }
                            )
                        } else tx
                    }.sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator)
                } else {
                    val newType = if (isLent) {
                        if (updatedRecord.type == "Pembayaran") TransactionType.INCOME else TransactionType.EXPENSE
                    } else {
                        if (updatedRecord.type == "Pembayaran") TransactionType.EXPENSE else TransactionType.INCOME
                    }
                    val newCategory = if (isLent) {
                        if (updatedRecord.type == "Pembayaran") "Pendapatan" else "Pengeluaran"
                    } else {
                        if (updatedRecord.type == "Pembayaran") "Pembayaran Utang" else "Pendapatan"
                    }
                    val newTitle = if (updatedRecord.note.isNotBlank()) updatedRecord.note
                                   else if (isLent) "Pelunasan Piutang: ${d?.name ?: ""}"
                                   else "Pembayaran Utang: ${d?.name ?: ""}"
                    val newTx = Transaction(
                        id = txId,
                        title = newTitle,
                        category = newCategory,
                        amount = updatedRecord.amount,
                        type = newType,
                        accountName = targetWallet,
                        toAccountName = if (!isLent && updatedRecord.type == "Pembayaran") (d?.name ?: "") else "",
                        dayOfMonth = parsed.day,
                        monthIndex = parsed.month,
                        year = parsed.year,
                        dayOfWeekName = parsed.dayOfWeek,
                        note = updatedRecord.note,
                        time = updatedRecord.time.ifBlank { "12:00" },
                        targetDebtId = debtId,
                        targetDebtName = d?.name ?: ""
                    )
                    (listOf(newTx) + txs).sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator)
                }
            }
        }
        saveToDisk()
    }

    fun deleteDebtRecord(debtId: String, recordId: String) {
        val targetDebt = _debts.value.firstOrNull { it.id == debtId }
        val rec = targetDebt?.records?.firstOrNull { it.id == recordId }
        val isLent = targetDebt?.type == DebtType.LENT

        _debts.update { list ->
            list.map { debt ->
                if (debt.id == debtId) {
                    val updatedRecords = debt.records.filterNot { it.id == recordId }
                    val totalPaid = updatedRecords.filter { it.type == "Pembayaran" }.sumOf { it.amount }
                    debt.copy(paidAmount = totalPaid, records = updatedRecords)
                } else debt
            }
        }
        if (rec != null && rec.sourceWalletName.isNotBlank()) {
            _wallets.update { list ->
                list.map { w ->
                    if (w.name.equals(rec.sourceWalletName, ignoreCase = true)) {
                        val revertedBal = when (rec.type) {
                            "Pembayaran" -> {
                                if (isLent) {
                                    // Tadinya menambah saldo -> kurangi kembali
                                    (w.balance - rec.amount).coerceAtLeast(0.0)
                                } else {
                                    // Tadinya mengurangi saldo -> tambahkan kembali
                                    w.balance + rec.amount
                                }
                            }
                            "Pinjam Lagi" -> {
                                if (isLent) {
                                    // Tadinya mengurangi saldo -> tambahkan kembali
                                    w.balance + rec.amount
                                } else {
                                    // Tadinya menambah saldo -> kurangi kembali
                                    (w.balance - rec.amount).coerceAtLeast(0.0)
                                }
                            }
                            else -> w.balance
                        }
                        w.copy(balance = revertedBal)
                    } else w
                }
            }
        }
        _transactions.update { txs ->
            txs.filterNot { it.id == "debt_rec_$recordId" || it.id == "tx_$recordId" || it.id == recordId }
        }
        saveToDisk()
    }

    fun payDebt(debtId: String, amount: Double, walletName: String? = null, note: String = "Pembayaran cicilan") {
        val d = _debts.value.firstOrNull { it.id == debtId }
        val effectiveWallet = if (!walletName.isNullOrBlank()) walletName
                              else d?.linkedWalletName?.ifBlank { _wallets.value.firstOrNull()?.name ?: "" } ?: ""
        val cal = Calendar.getInstance()
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val monthStr = monthNames.getOrElse(cal.get(Calendar.MONTH)) { "Nov" }
        val year = cal.get(Calendar.YEAR)
        val dateStr = "$day $monthStr $year"
        val timeStr = String.format("%02d.%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        addDebtRecord(
            debtId = debtId,
            record = DebtRecord(
                debtId = debtId,
                type = "Pembayaran",
                amount = amount,
                date = dateStr,
                time = timeStr,
                sourceWalletName = effectiveWallet,
                note = note
            ),
            affectWalletName = effectiveWallet.ifBlank { null }
        )
    }

    fun deleteDebt(debtId: String, deleteLinkedTransactions: Boolean = false) {
        val targetDebt = _debts.value.firstOrNull { it.id == debtId }
        val targetDebtName = targetDebt?.name ?: ""
        val recordIds = targetDebt?.records?.map { it.id }?.toSet() ?: emptySet()

        if (deleteLinkedTransactions) {
            val linkedTxs = _transactions.value.filter { tx ->
                tx.targetDebtId == debtId ||
                (targetDebtName.isNotBlank() && tx.targetDebtName.equals(targetDebtName, ignoreCase = true)) ||
                tx.id == "debt_init_$debtId" ||
                (tx.id.startsWith("debt_rec_") && recordIds.contains(tx.id.removePrefix("debt_rec_"))) ||
                (tx.id.startsWith("tx_") && recordIds.contains(tx.id.removePrefix("tx_"))) ||
                recordIds.contains(tx.id)
            }
            linkedTxs.forEach { tx ->
                deleteTransaction(tx.id)
            }
        }

        _debts.update { list -> list.filterNot { it.id == debtId } }
        saveToDisk()
    }

    fun addBudget(budget: BudgetCategory) {
        _budgets.update { it + budget }
        saveToDisk()
    }

    fun updateSettings(newSettings: UserSettings) {
        _settings.value = newSettings
        saveToDisk()
    }

    fun addLabel(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && !_labels.value.any { it.equals(trimmed, ignoreCase = true) }) {
            _labels.update { it + trimmed }
            saveToDisk()
        }
    }

    fun deleteLabel(name: String) {
        _labels.update { it.filterNot { label -> label.equals(name, ignoreCase = true) } }
        saveToDisk()
    }

    // Category and Subcategory methods
    fun addCategory(category: MainCategory) {
        _categories.update { current ->
            current + category
        }
        saveToDisk()
    }

    fun updateCategory(category: MainCategory) {
        _categories.update { current ->
            current.map { if (it.id == category.id) category else it }
        }
        saveToDisk()
    }

    fun deleteCategory(categoryId: String) {
        _categories.update { current ->
            current.filterNot { it.id == categoryId }
        }
        saveToDisk()
    }

    fun addSubcategory(categoryId: String, subcategory: SubcategoryItem) {
        _categories.update { current ->
            current.map { cat ->
                if (cat.id == categoryId) {
                    cat.copy(subcategories = cat.subcategories + subcategory)
                } else cat
            }
        }
        saveToDisk()
    }

    fun updateSubcategory(categoryId: String, subcategory: SubcategoryItem) {
        _categories.update { current ->
            current.map { cat ->
                if (cat.id == categoryId) {
                    cat.copy(subcategories = cat.subcategories.map { if (it.id == subcategory.id) subcategory else it })
                } else cat
            }
        }
        saveToDisk()
    }

    fun deleteSubcategory(categoryId: String, subcategoryId: String) {
        _categories.update { current ->
            current.map { cat ->
                if (cat.id == categoryId) {
                    cat.copy(subcategories = cat.subcategories.filterNot { it.id == subcategoryId })
                } else cat
            }
        }
        saveToDisk()
    }

    fun toggleCategoryVisibility(categoryId: String) {
        _categories.update { current ->
            current.map { cat ->
                if (cat.id == categoryId) cat.copy(isVisible = !cat.isVisible) else cat
            }
        }
        saveToDisk()
    }

    fun toggleSubcategoryVisibility(categoryId: String, subcategoryId: String) {
        _categories.update { current ->
            current.map { cat ->
                if (cat.id == categoryId) {
                    cat.copy(subcategories = cat.subcategories.map {
                        if (it.id == subcategoryId) it.copy(isVisible = !it.isVisible) else it
                    })
                } else cat
            }
        }
        saveToDisk()
    }

    fun addRecurringTransaction(
        recurring: RecurringTransaction,
        checkImmediateProcess: Boolean = true,
        currentDay: Int = 28,
        currentMonth: Int = 10,
        currentYear: Int = 2026
    ) {
        var itemToAdd = recurring
        if (checkImmediateProcess && recurring.isActive && recurring.autoDeduct && recurring.dayOfMonth <= currentDay) {
            val key = "$currentMonth/$currentYear"
            if (itemToAdd.lastProcessedMonthYear != key) {
                val dayOfWeek = getDayOfWeekName(itemToAdd.dayOfMonth, currentMonth, currentYear)
                val tx = Transaction(
                    title = itemToAdd.title,
                    category = itemToAdd.category,
                    subcategory = itemToAdd.subcategory,
                    amount = itemToAdd.amount,
                    type = itemToAdd.type,
                    accountName = itemToAdd.accountName,
                    dayOfMonth = itemToAdd.dayOfMonth,
                    monthIndex = currentMonth,
                    year = currentYear,
                    dayOfWeekName = dayOfWeek,
                    note = if (itemToAdd.note.isNotBlank()) "${itemToAdd.note} (Otomatis Rutin)" else "Otomatis dari Transaksi Berulang",
                    label = itemToAdd.label,
                    recurrence = "Bulanan (Tgl ${itemToAdd.dayOfMonth})"
                )
                addTransaction(tx)
                itemToAdd = itemToAdd.copy(lastProcessedMonthYear = key)
            }
        }
        _recurringTransactions.update { it + itemToAdd }
        saveToDisk()
    }

    fun updateRecurringTransaction(recurring: RecurringTransaction) {
        _recurringTransactions.update { list ->
            list.map { if (it.id == recurring.id) recurring else it }
        }
        saveToDisk()
    }

    fun deleteRecurringTransaction(id: String) {
        _recurringTransactions.update { it.filterNot { item -> item.id == id } }
        saveToDisk()
    }

    fun toggleRecurringActive(id: String) {
        _recurringTransactions.update { list ->
            list.map { if (it.id == id) it.copy(isActive = !it.isActive) else it }
        }
        saveToDisk()
    }

    fun processRecurringTransaction(id: String, month: Int, year: Int): Boolean {
        val recurring = _recurringTransactions.value.find { it.id == id } ?: return false
        val key = "$month/$year"
        val dayOfWeek = getDayOfWeekName(recurring.dayOfMonth, month, year)
        val tx = Transaction(
            title = recurring.title,
            category = recurring.category,
            subcategory = recurring.subcategory,
            amount = recurring.amount,
            type = recurring.type,
            accountName = recurring.accountName,
            dayOfMonth = recurring.dayOfMonth,
            monthIndex = month,
            year = year,
            dayOfWeekName = dayOfWeek,
            note = if (recurring.note.isNotBlank()) "${recurring.note} (Otomatis Rutin)" else "Otomatis dari Transaksi Berulang",
            label = recurring.label,
            recurrence = "Bulanan (Tgl ${recurring.dayOfMonth})"
        )
        addTransaction(tx)
        _recurringTransactions.update { list ->
            list.map { if (it.id == id) it.copy(lastProcessedMonthYear = key) else it }
        }
        saveToDisk()
        return true
    }

    fun checkAndExecuteDueRecurringTransactions(currentDay: Int, currentMonth: Int, currentYear: Int): Int {
        val key = "$currentMonth/$currentYear"
        var executedCount = 0
        _recurringTransactions.value.forEach { item ->
            if (item.isActive && item.autoDeduct && item.dayOfMonth <= currentDay && item.lastProcessedMonthYear != key) {
                processRecurringTransaction(item.id, currentMonth, currentYear)
                executedCount++
            }
        }
        return executedCount
    }

    private fun getDayOfWeekName(day: Int, month: Int, year: Int): String {
        val c = java.util.Calendar.getInstance()
        c.set(year, month - 1, day)
        return when (c.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.SUNDAY -> "Min"
            java.util.Calendar.MONDAY -> "Sen"
            java.util.Calendar.TUESDAY -> "Sel"
            java.util.Calendar.WEDNESDAY -> "Rab"
            java.util.Calendar.THURSDAY -> "Kam"
            java.util.Calendar.FRIDAY -> "Jum"
            java.util.Calendar.SATURDAY -> "Sab"
            else -> "Sen"
        }
    }

    fun updatePlanTitle(newTitle: String) {
        _planTitle.value = newTitle
        saveToDisk()
    }

    fun addPlannedItem(item: PlannedItem) {
        _plannedItems.update { it + item }
        saveToDisk()
    }

    fun updatePlannedItem(item: PlannedItem) {
        _plannedItems.update { list -> list.map { if (it.id == item.id) item else it } }
        saveToDisk()
    }

    fun deletePlannedItem(id: String) {
        _plannedItems.update { list -> list.filterNot { it.id == id } }
        saveToDisk()
    }

    fun exportTransactionsJson(ctx: Context? = getActiveContext()): String {
        val root = JSONObject()
        root.put("app", "Keuangan")
        root.put("version", "3.0")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        root.put("exportedAt", sdf.format(Date()))
        root.put("totalTransactions", _transactions.value.size)
        root.put("totalWallets", _wallets.value.size)
        root.put("totalAssets", _assets.value.size)
        root.put("totalDebts", _debts.value.size)
        root.put("totalCategories", _categories.value.size)
        root.put("totalPlans", _plannedItems.value.size)
        root.put("totalBudgets", _budgets.value.size)
        root.put("totalRecurring", _recurringTransactions.value.size)

        // Collect custom logos (Base64)
        val referencedCustomPaths = mutableListOf<String>()
        _wallets.value.forEach { w ->
            if (w.iconName.isNotBlank() && (w.iconName.startsWith("custom:") || w.iconName.contains("custom_icons"))) {
                referencedCustomPaths.add(w.iconName)
            }
        }
        _assets.value.forEach { a ->
            if (a.iconUri.isNotBlank() && (a.iconUri.startsWith("custom:") || a.iconUri.contains("custom_icons"))) {
                referencedCustomPaths.add(a.iconUri)
            }
        }
        _debts.value.forEach { d ->
            if (d.institutionLogo.isNotBlank() && (d.institutionLogo.startsWith("custom:") || d.institutionLogo.contains("custom_icons"))) {
                referencedCustomPaths.add(d.institutionLogo)
            }
        }

        val logosMap = if (ctx != null) {
            ImageUploadHelper.collectAllCustomLogos(ctx, referencedCustomPaths)
        } else emptyMap()

        // 1. Transactions
        val txArr = JSONArray()
        _transactions.value.forEach { tx ->
            val o = JSONObject().apply {
                put("id", tx.id)
                put("title", tx.title)
                put("category", tx.category)
                put("subcategory", tx.subcategory)
                put("amount", tx.amount)
                put("type", tx.type.name)
                put("accountName", tx.accountName)
                put("dayOfMonth", tx.dayOfMonth)
                put("monthIndex", tx.monthIndex)
                put("year", tx.year)
                put("dayOfWeekName", tx.dayOfWeekName)
                put("note", tx.note)
                put("label", tx.label)
                put("recurrence", tx.recurrence)
                put("time", tx.time)
                put("toAccountName", tx.toAccountName)
                put("transferFee", tx.transferFee)
                put("targetAssetId", tx.targetAssetId)
                put("targetAssetName", tx.targetAssetName)
                put("targetDebtId", tx.targetDebtId)
                put("targetDebtName", tx.targetDebtName)
            }
            txArr.put(o)
        }
        root.put("transactions", txArr)

        // 2. Wallets / Akun (with custom logos)
        val wArr = JSONArray()
        _wallets.value.forEach { w ->
            val o = JSONObject().apply {
                put("id", w.id)
                put("name", w.name)
                put("bankType", w.bankType)
                put("balance", w.balance)
                put("lastCategory", w.lastCategory)
                put("lastAmount", w.lastAmount)
                put("lastActivityTime", w.lastActivityTime)
                put("currency", w.currency)
                put("iconName", w.iconName)
                put("excludeFromNetWorth", w.excludeFromNetWorth)
                put("excludeFromBudget", w.excludeFromBudget)
                if (w.iconName.isNotBlank() && (w.iconName.startsWith("custom:") || w.iconName.contains("custom_icons"))) {
                    val fName = ImageUploadHelper.extractFileName(w.iconName)
                    put("customLogoKey", fName)
                    logosMap[fName]?.let { put("customLogoBase64", it) }
                }
            }
            wArr.put(o)
        }
        root.put("wallets", wArr)

        // 3. Assets (with custom logos & full fields)
        val aArr = JSONArray()
        _assets.value.forEach { a ->
            val o = JSONObject().apply {
                put("id", a.id)
                put("name", a.name)
                put("group", a.group)
                put("assetType", a.assetType)
                put("value", a.value)
                put("platform", a.platform)
                put("performancePercent", a.performancePercent)
                put("currency", a.currency)
                put("createdAtDate", a.createdAtDate)
                put("createdAtTime", a.createdAtTime)
                put("valuationMethod", a.valuationMethod)
                put("iconUri", a.iconUri)
                put("tickerSymbol", a.tickerSymbol)
                put("lotCount", a.lotCount)
                put("averagePrice", a.averagePrice)
                put("currentPrice", a.currentPrice)
                put("dailyPriceChange", a.dailyPriceChange)
                put("dailyPriceChangePercent", a.dailyPriceChangePercent)
                put("lastPriceUpdate", a.lastPriceUpdate)
                if (a.iconUri.isNotBlank() && (a.iconUri.startsWith("custom:") || a.iconUri.contains("custom_icons"))) {
                    val fName = ImageUploadHelper.extractFileName(a.iconUri)
                    put("customLogoKey", fName)
                    logosMap[fName]?.let { put("customLogoBase64", it) }
                }

                val rArr = JSONArray()
                a.records.forEach { r ->
                    val ro = JSONObject().apply {
                        put("id", r.id)
                        put("assetId", r.assetId)
                        put("tag", r.tag)
                        put("isIncrease", r.isIncrease)
                        put("amount", r.amount)
                        put("sourceWalletName", r.sourceWalletName)
                        put("date", r.date)
                        put("time", r.time)
                        put("note", r.note)
                        put("lotCount", r.lotCount)
                        put("pricePerUnit", r.pricePerUnit)
                    }
                    rArr.put(ro)
                }
                put("records", rArr)
            }
            aArr.put(o)
        }
        root.put("assets", aArr)

        // 4. Debts (with custom logos & full fields)
        val dArr = JSONArray()
        _debts.value.forEach { d ->
            val o = JSONObject().apply {
                put("id", d.id)
                put("name", d.name)
                put("type", d.type.name)
                put("totalAmount", d.totalAmount)
                put("paidAmount", d.paidAmount)
                put("dueDate", d.dueDate)
                put("startDate", d.startDate)
                put("monthlyPayment", d.monthlyPayment)
                put("interestRate", d.interestRate)
                put("extraFees", d.extraFees)
                put("categoryIcon", d.categoryIcon)
                put("institutionLogo", d.institutionLogo)
                put("tenureMonths", d.tenureMonths)
                put("linkedWalletName", d.linkedWalletName)
                if (d.institutionLogo.isNotBlank() && (d.institutionLogo.startsWith("custom:") || d.institutionLogo.contains("custom_icons"))) {
                    val fName = ImageUploadHelper.extractFileName(d.institutionLogo)
                    put("customLogoKey", fName)
                    logosMap[fName]?.let { put("customLogoBase64", it) }
                }

                val rArr = JSONArray()
                d.records.forEach { r ->
                    val ro = JSONObject().apply {
                        put("id", r.id)
                        put("debtId", r.debtId)
                        put("type", r.type)
                        put("amount", r.amount)
                        put("date", r.date)
                        put("time", r.time)
                        put("sourceWalletName", r.sourceWalletName)
                        put("note", r.note)
                    }
                    rArr.put(ro)
                }
                put("records", rArr)
            }
            dArr.put(o)
        }
        root.put("debts", dArr)

        // 5. Budgets
        val bArr = JSONArray()
        _budgets.value.forEach { b ->
            val o = JSONObject().apply {
                put("id", b.id)
                put("category", b.category)
                put("allocated", b.allocated)
                put("spent", b.spent)
            }
            bArr.put(o)
        }
        root.put("budgets", bArr)

        // 6. Recurring Transactions
        val recArr = JSONArray()
        _recurringTransactions.value.forEach { r ->
            val o = JSONObject().apply {
                put("id", r.id)
                put("title", r.title)
                put("amount", r.amount)
                put("type", r.type.name)
                put("category", r.category)
                put("subcategory", r.subcategory)
                put("accountName", r.accountName)
                put("dayOfMonth", r.dayOfMonth)
                put("frequency", r.frequency)
                put("startDate", r.startDate)
                put("endDate", r.endDate)
                put("note", r.note)
                put("label", r.label)
                put("isActive", r.isActive)
                put("autoDeduct", r.autoDeduct)
                put("lastProcessedMonthYear", r.lastProcessedMonthYear)
            }
            recArr.put(o)
        }
        root.put("recurringTransactions", recArr)

        // 7. Upcoming Schedules
        val upArr = JSONArray()
        _upcomingItems.value.forEach { up ->
            val o = JSONObject().apply {
                put("id", up.id)
                put("title", up.title)
                put("accountName", up.accountName)
                put("amount", up.amount)
                put("isIncome", up.isIncome)
                put("dateLabel", up.dateLabel)
                put("dayLabel", up.dayLabel)
            }
            upArr.put(o)
        }
        root.put("upcomingItems", upArr)

        // 8. Categories & Subcategories (including all custom categories added by user)
        val cArr = JSONArray()
        _categories.value.forEach { cat ->
            val co = JSONObject().apply {
                put("id", cat.id)
                put("name", cat.name)
                put("type", cat.type.name)
                put("iconName", cat.iconName)
                put("isVisible", cat.isVisible)
                val subArr = JSONArray()
                cat.subcategories.forEach { sub ->
                    val so = JSONObject().apply {
                        put("id", sub.id)
                        put("name", sub.name)
                        put("iconName", sub.iconName)
                        put("isVisible", sub.isVisible)
                    }
                    subArr.put(so)
                }
                put("subcategories", subArr)
            }
            cArr.put(co)
        }
        root.put("categories", cArr)

        // 9. Planned Items (Perencanaan Keuangan)
        val pArr = JSONArray()
        _plannedItems.value.forEach { p ->
            val o = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("category", p.category)
                put("section", p.section.name)
                put("amount", p.amount)
            }
            pArr.put(o)
        }
        root.put("plannedItems", pArr)
        root.put("planTitle", _planTitle.value)

        // 10. Labels
        val lArr = JSONArray()
        _labels.value.forEach { lArr.put(it) }
        root.put("labels", lArr)

        // 11. User Settings
        val s = _settings.value
        val sObj = JSONObject().apply {
            put("language", s.language)
            put("isDarkMode", s.isDarkMode)
            put("primaryCurrency", s.primaryCurrency)
            put("monthlyStartDay", s.monthlyStartDay)
            put("textSizePercent", s.textSizePercent)
            put("cashAccountsOnly", s.cashAccountsOnly)
            put("showPlannedInBalance", s.showPlannedInBalance)
            put("saveReminder", s.saveReminder)
            put("billReminder", s.billReminder)
            put("budgetAlert", s.budgetAlert)
            put("passcodeEnabled", s.passcodeEnabled)
            put("appPasscode", s.appPasscode)
            put("themeColorName", s.themeColorName)
        }
        root.put("settings", sObj)
        root.put("selectedWalletFilter", _selectedWalletFilter.value)

        // 12. Custom Logos (Base64 dictionary)
        val logosObj = JSONObject()
        logosMap.forEach { (fileName, b64) ->
            logosObj.put(fileName, b64)
        }
        root.put("customLogos", logosObj)
        root.put("totalCustomLogos", logosMap.size)

        return root.toString(2)
    }

    fun importTransactionsFromJson(
        jsonStr: String,
        replaceExisting: Boolean,
        ctx: Context? = getActiveContext()
    ): Result<com.example.data.model.BackupRestoreResult> {
        return try {
            val trimmed = jsonStr.trim()
            val rootObj = if (trimmed.startsWith("{")) JSONObject(trimmed) else null

            // 0. Extract & Restore Custom Logos (Base64)
            val availableLogos = mutableMapOf<String, String>()
            val customLogosObj = rootObj?.optJSONObject("customLogos")
            if (customLogosObj != null) {
                val keys = customLogosObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val b64 = customLogosObj.optString(key, "")
                    if (b64.isNotBlank()) {
                        availableLogos[key] = b64
                    }
                }
            }

            // Also check individual customLogoBase64 inside wallets, assets, debts
            val rawWalletsArr = rootObj?.optJSONArray("wallets")
            if (rawWalletsArr != null) {
                for (i in 0 until rawWalletsArr.length()) {
                    val wo = rawWalletsArr.getJSONObject(i)
                    val k = wo.optString("customLogoKey", "")
                    val b64 = wo.optString("customLogoBase64", "")
                    if (k.isNotBlank() && b64.isNotBlank() && !availableLogos.containsKey(k)) {
                        availableLogos[k] = b64
                    }
                }
            }
            val rawAssetsArr = rootObj?.optJSONArray("assets")
            if (rawAssetsArr != null) {
                for (i in 0 until rawAssetsArr.length()) {
                    val ao = rawAssetsArr.getJSONObject(i)
                    val k = ao.optString("customLogoKey", "")
                    val b64 = ao.optString("customLogoBase64", "")
                    if (k.isNotBlank() && b64.isNotBlank() && !availableLogos.containsKey(k)) {
                        availableLogos[k] = b64
                    }
                }
            }
            val rawDebtsArr = rootObj?.optJSONArray("debts")
            if (rawDebtsArr != null) {
                for (i in 0 until rawDebtsArr.length()) {
                    val doItem = rawDebtsArr.getJSONObject(i)
                    val k = doItem.optString("customLogoKey", "")
                    val b64 = doItem.optString("customLogoBase64", "")
                    if (k.isNotBlank() && b64.isNotBlank() && !availableLogos.containsKey(k)) {
                        availableLogos[k] = b64
                    }
                }
            }

            var restoredLogoCount = 0
            if (ctx != null && availableLogos.isNotEmpty()) {
                availableLogos.forEach { (fileName, b64) ->
                    val file = ImageUploadHelper.base64ToFile(ctx, fileName, b64)
                    if (file != null && file.exists()) {
                        restoredLogoCount++
                    }
                }
            }

            // 1. Parse Transactions
            val txArr = if (trimmed.startsWith("[")) {
                JSONArray(trimmed)
            } else {
                rootObj?.optJSONArray("transactions") ?: JSONArray()
            }

            val importedTxs = mutableListOf<Transaction>()
            for (i in 0 until txArr.length()) {
                val o = txArr.getJSONObject(i)
                importedTxs.add(
                    Transaction(
                        id = o.optString("id", UUID.randomUUID().toString()),
                        title = o.optString("title", "Transaksi"),
                        category = o.optString("category", "Other"),
                        subcategory = o.optString("subcategory", ""),
                        amount = o.optDouble("amount", 0.0),
                        type = try { TransactionType.valueOf(o.optString("type", "EXPENSE")) } catch (e: Exception) { TransactionType.EXPENSE },
                        accountName = o.optString("accountName", "BRI Utama"),
                        dayOfMonth = o.optInt("dayOfMonth", 1),
                        monthIndex = o.optInt("monthIndex", 1),
                        year = o.optInt("year", 2026),
                        dayOfWeekName = o.optString("dayOfWeekName", "Sen"),
                        note = o.optString("note", ""),
                        label = o.optString("label", ""),
                        recurrence = o.optString("recurrence", "Tidak pernah"),
                        time = o.optString("time", "12:00"),
                        toAccountName = o.optString("toAccountName", ""),
                        transferFee = o.optDouble("transferFee", 0.0),
                        targetAssetId = o.optString("targetAssetId", ""),
                        targetAssetName = o.optString("targetAssetName", ""),
                        targetDebtId = o.optString("targetDebtId", ""),
                        targetDebtName = o.optString("targetDebtName", "")
                    )
                )
            }

            // 2. Parse Wallets
            val importedWallets = mutableListOf<AccountWallet>()
            val wArr = rootObj?.optJSONArray("wallets")
            if (wArr != null) {
                for (i in 0 until wArr.length()) {
                    val o = wArr.getJSONObject(i)
                    val rawIcon = o.optString("iconName", "Bank")
                    val resolvedIcon = if (ctx != null) {
                        ImageUploadHelper.resolveOrRestoreIconPath(ctx, rawIcon, availableLogos)
                    } else rawIcon

                    importedWallets.add(
                        AccountWallet(
                            id = o.optString("id", UUID.randomUUID().toString()),
                            name = o.optString("name", "Dompet"),
                            bankType = o.optString("bankType", "Bank · IDR"),
                            balance = o.optDouble("balance", 0.0),
                            lastCategory = o.optString("lastCategory", ""),
                            lastAmount = o.optDouble("lastAmount", 0.0),
                            lastActivityTime = o.optString("lastActivityTime", ""),
                            currency = o.optString("currency", "IDR"),
                            iconName = resolvedIcon,
                            excludeFromNetWorth = o.optBoolean("excludeFromNetWorth", false),
                            excludeFromBudget = o.optBoolean("excludeFromBudget", false)
                        )
                    )
                }
            }

            // 3. Parse Assets
            val importedAssets = mutableListOf<AssetItem>()
            val aArr = rootObj?.optJSONArray("assets")
            if (aArr != null) {
                for (i in 0 until aArr.length()) {
                    val o = aArr.getJSONObject(i)
                    val recList = mutableListOf<AssetRecord>()
                    val rArr = o.optJSONArray("records")
                    if (rArr != null) {
                        for (j in 0 until rArr.length()) {
                            val ro = rArr.getJSONObject(j)
                            recList.add(
                                AssetRecord(
                                    id = ro.optString("id", UUID.randomUUID().toString()),
                                    assetId = ro.optString("assetId", ""),
                                    tag = ro.optString("tag", "Buy"),
                                    isIncrease = ro.optBoolean("isIncrease", true),
                                    amount = ro.optDouble("amount", 0.0),
                                    sourceWalletName = ro.optString("sourceWalletName", ""),
                                    date = ro.optString("date", "29 Okt 2026"),
                                    time = ro.optString("time", "00.15"),
                                    note = ro.optString("note", ""),
                                    lotCount = ro.optDouble("lotCount", 0.0),
                                    pricePerUnit = ro.optDouble("pricePerUnit", 0.0)
                                )
                            )
                        }
                    }

                    val rawIcon = o.optString("iconUri", "")
                    val resolvedIcon = if (ctx != null) {
                        ImageUploadHelper.resolveOrRestoreIconPath(ctx, rawIcon, availableLogos)
                    } else rawIcon

                    importedAssets.add(
                        AssetItem(
                            id = o.optString("id", UUID.randomUUID().toString()),
                            name = o.optString("name", "Aset"),
                            group = o.optString("group", "Investasi"),
                            assetType = o.optString("assetType", "Bank Account"),
                            value = o.optDouble("value", 0.0),
                            platform = o.optString("platform", "Bibit"),
                            performancePercent = o.optDouble("performancePercent", 10.2),
                            currency = o.optString("currency", "IDR"),
                            createdAtDate = o.optString("createdAtDate", "29 Okt 2026"),
                            createdAtTime = o.optString("createdAtTime", "00.15"),
                            valuationMethod = o.optString("valuationMethod", "Manual"),
                            records = recList,
                            iconUri = resolvedIcon,
                            tickerSymbol = o.optString("tickerSymbol", ""),
                            lotCount = o.optDouble("lotCount", 0.0),
                            averagePrice = o.optDouble("averagePrice", 0.0),
                            currentPrice = o.optDouble("currentPrice", 0.0),
                            dailyPriceChange = o.optDouble("dailyPriceChange", 0.0),
                            dailyPriceChangePercent = o.optDouble("dailyPriceChangePercent", 0.0),
                            lastPriceUpdate = o.optLong("lastPriceUpdate", 0L)
                        )
                    )
                }
            }

            // 4. Parse Debts
            val importedDebts = mutableListOf<DebtItem>()
            val dArr = rootObj?.optJSONArray("debts")
            if (dArr != null) {
                for (i in 0 until dArr.length()) {
                    val o = dArr.getJSONObject(i)
                    val recList = mutableListOf<DebtRecord>()
                    val rArr = o.optJSONArray("records")
                    if (rArr != null) {
                        for (j in 0 until rArr.length()) {
                            val ro = rArr.getJSONObject(j)
                            recList.add(
                                DebtRecord(
                                    id = ro.optString("id", UUID.randomUUID().toString()),
                                    debtId = ro.optString("debtId", ""),
                                    type = ro.optString("type", "Pembayaran"),
                                    amount = ro.optDouble("amount", 0.0),
                                    date = ro.optString("date", "2 Nov 2026"),
                                    time = ro.optString("time", "12.56"),
                                    sourceWalletName = ro.optString("sourceWalletName", ""),
                                    note = ro.optString("note", "")
                                )
                            )
                        }
                    }

                    val rawLogo = o.optString("institutionLogo", "")
                    val resolvedLogo = if (ctx != null) {
                        ImageUploadHelper.resolveOrRestoreIconPath(ctx, rawLogo, availableLogos)
                    } else rawLogo

                    importedDebts.add(
                        DebtItem(
                            id = o.optString("id", UUID.randomUUID().toString()),
                            name = o.optString("name", "Pinjaman"),
                            type = try { DebtType.valueOf(o.optString("type", "BORROWED")) } catch (e: Exception) { DebtType.BORROWED },
                            totalAmount = o.optDouble("totalAmount", 0.0),
                            paidAmount = o.optDouble("paidAmount", 0.0),
                            dueDate = o.optString("dueDate", "1 Des 2027"),
                            startDate = o.optString("startDate", "28 Mar 2026"),
                            monthlyPayment = o.optDouble("monthlyPayment", 0.0),
                            interestRate = o.optDouble("interestRate", 0.0),
                            extraFees = o.optDouble("extraFees", 0.0),
                            categoryIcon = o.optString("categoryIcon", "CreditCard"),
                            institutionLogo = resolvedLogo,
                            tenureMonths = o.optInt("tenureMonths", 0),
                            linkedWalletName = o.optString("linkedWalletName", ""),
                            records = recList
                        )
                    )
                }
            }

            // 5. Parse Categories & Subcategories
            val importedCategories = mutableListOf<MainCategory>()
            val cArr = rootObj?.optJSONArray("categories")
            if (cArr != null) {
                for (i in 0 until cArr.length()) {
                    val co = cArr.getJSONObject(i)
                    val catType = try {
                        TransactionType.valueOf(co.optString("type", "EXPENSE"))
                    } catch (e: Exception) {
                        TransactionType.EXPENSE
                    }
                    val subList = mutableListOf<SubcategoryItem>()
                    val subArr = co.optJSONArray("subcategories")
                    if (subArr != null) {
                        for (j in 0 until subArr.length()) {
                            val so = subArr.getJSONObject(j)
                            subList.add(
                                SubcategoryItem(
                                    id = so.optString("id", UUID.randomUUID().toString()),
                                    name = so.optString("name", "Sub"),
                                    iconName = so.optString("iconName", ""),
                                    isVisible = so.optBoolean("isVisible", true)
                                )
                            )
                        }
                    }
                    importedCategories.add(
                        MainCategory(
                            id = co.optString("id", UUID.randomUUID().toString()),
                            name = co.optString("name", "Kategori"),
                            type = catType,
                            iconName = co.optString("iconName", ""),
                            isVisible = co.optBoolean("isVisible", true),
                            subcategories = subList
                        )
                    )
                }
            }

            // 6. Parse Planned Items (Perencanaan)
            val importedPlans = mutableListOf<PlannedItem>()
            val pArr = rootObj?.optJSONArray("plannedItems")
            if (pArr != null) {
                for (i in 0 until pArr.length()) {
                    val po = pArr.getJSONObject(i)
                    val section = try {
                        PlanSectionType.valueOf(po.optString("section", "EXPENSE"))
                    } catch (e: Exception) {
                        PlanSectionType.EXPENSE
                    }
                    importedPlans.add(
                        PlannedItem(
                            id = po.optString("id", UUID.randomUUID().toString()),
                            name = po.optString("name", "Rencana"),
                            category = po.optString("category", ""),
                            amount = po.optDouble("amount", 0.0),
                            section = section
                        )
                    )
                }
            }
            val importedPlanTitle = rootObj?.optString("planTitle", "") ?: ""

            // 7. Parse Budgets
            val importedBudgets = mutableListOf<BudgetCategory>()
            val bArr = rootObj?.optJSONArray("budgets")
            if (bArr != null) {
                for (i in 0 until bArr.length()) {
                    val bo = bArr.getJSONObject(i)
                    importedBudgets.add(
                        BudgetCategory(
                            id = bo.optString("id", UUID.randomUUID().toString()),
                            category = bo.optString("category", "Kategori"),
                            allocated = bo.optDouble("allocated", 0.0),
                            spent = bo.optDouble("spent", 0.0)
                        )
                    )
                }
            }

            // 8. Parse Recurring Transactions
            val importedRecurring = mutableListOf<RecurringTransaction>()
            val recArr = rootObj?.optJSONArray("recurringTransactions")
            if (recArr != null) {
                for (i in 0 until recArr.length()) {
                    val ro = recArr.getJSONObject(i)
                    importedRecurring.add(
                        RecurringTransaction(
                            id = ro.optString("id", UUID.randomUUID().toString()),
                            title = ro.optString("title", "Transaksi Berulang"),
                            amount = ro.optDouble("amount", 0.0),
                            type = try { TransactionType.valueOf(ro.optString("type", "EXPENSE")) } catch (e: Exception) { TransactionType.EXPENSE },
                            category = ro.optString("category", "Other"),
                            subcategory = ro.optString("subcategory", ""),
                            accountName = ro.optString("accountName", "BRI Utama"),
                            dayOfMonth = ro.optInt("dayOfMonth", 1),
                            frequency = ro.optString("frequency", "Setiap bulan"),
                            startDate = ro.optString("startDate", ""),
                            endDate = ro.optString("endDate", ""),
                            note = ro.optString("note", ""),
                            label = ro.optString("label", ""),
                            isActive = ro.optBoolean("isActive", true),
                            autoDeduct = ro.optBoolean("autoDeduct", true),
                            lastProcessedMonthYear = ro.optString("lastProcessedMonthYear", "")
                        )
                    )
                }
            }

            // 9. Parse Upcoming Schedules
            val importedUpcoming = mutableListOf<UpcomingScheduleItem>()
            val upArr = rootObj?.optJSONArray("upcomingItems")
            if (upArr != null) {
                for (i in 0 until upArr.length()) {
                    val uo = upArr.getJSONObject(i)
                    importedUpcoming.add(
                        UpcomingScheduleItem(
                            id = uo.optString("id", UUID.randomUUID().toString()),
                            title = uo.optString("title", "Jadwal"),
                            accountName = uo.optString("accountName", "BRI Utama"),
                            amount = uo.optDouble("amount", 0.0),
                            isIncome = uo.optBoolean("isIncome", false),
                            dateLabel = uo.optString("dateLabel", "1 Nov"),
                            dayLabel = uo.optString("dayLabel", "Min")
                        )
                    )
                }
            }

            // 10. Parse Labels
            val importedLabels = mutableListOf<String>()
            val lArr = rootObj?.optJSONArray("labels")
            if (lArr != null) {
                for (i in 0 until lArr.length()) {
                    importedLabels.add(lArr.getString(i))
                }
            }

            // 11. Parse Settings
            val sObj = rootObj?.optJSONObject("settings")
            val importedSettings: UserSettings? = if (sObj != null) {
                UserSettings(
                    language = sObj.optString("language", "Indonesia"),
                    isDarkMode = sObj.optBoolean("isDarkMode", true),
                    primaryCurrency = sObj.optString("primaryCurrency", "IDR"),
                    monthlyStartDay = sObj.optInt("monthlyStartDay", 1),
                    textSizePercent = sObj.optInt("textSizePercent", 100),
                    cashAccountsOnly = sObj.optBoolean("cashAccountsOnly", false),
                    showPlannedInBalance = sObj.optBoolean("showPlannedInBalance", true),
                    saveReminder = sObj.optBoolean("saveReminder", false),
                    billReminder = sObj.optBoolean("billReminder", false),
                    budgetAlert = sObj.optBoolean("budgetAlert", true),
                    passcodeEnabled = sObj.optBoolean("passcodeEnabled", false),
                    appPasscode = sObj.optString("appPasscode", "1234"),
                    themeColorName = sObj.optString("themeColorName", "FinTrack Blue")
                )
            } else null

            val importedFilter = rootObj?.optString("selectedWalletFilter", "") ?: ""

            val hasAnyData = importedTxs.isNotEmpty() ||
                    importedWallets.isNotEmpty() ||
                    importedAssets.isNotEmpty() ||
                    importedDebts.isNotEmpty() ||
                    importedCategories.isNotEmpty() ||
                    importedPlans.isNotEmpty() ||
                    importedBudgets.isNotEmpty() ||
                    importedRecurring.isNotEmpty() ||
                    importedSettings != null ||
                    availableLogos.isNotEmpty()

            if (!hasAnyData) {
                return Result.failure(Exception("Tidak ditemukan data yang valid dalam file cadangan JSON"))
            }

            if (replaceExisting) {
                if (importedTxs.isNotEmpty()) _transactions.value = importedTxs
                if (importedWallets.isNotEmpty()) _wallets.value = importedWallets
                if (importedAssets.isNotEmpty()) _assets.value = importedAssets
                if (importedDebts.isNotEmpty()) _debts.value = importedDebts
                if (importedCategories.isNotEmpty()) _categories.value = importedCategories
                if (importedPlans.isNotEmpty()) _plannedItems.value = importedPlans
                if (importedPlanTitle.isNotBlank()) _planTitle.value = importedPlanTitle
                if (importedBudgets.isNotEmpty()) _budgets.value = importedBudgets
                if (importedRecurring.isNotEmpty()) _recurringTransactions.value = importedRecurring
                if (importedUpcoming.isNotEmpty()) _upcomingItems.value = importedUpcoming
                if (importedLabels.isNotEmpty()) _labels.value = importedLabels
                if (importedSettings != null) _settings.value = importedSettings
                if (importedFilter.isNotBlank()) _selectedWalletFilter.value = importedFilter
            } else {
                // Merge Transactions
                if (importedTxs.isNotEmpty()) {
                    val existingTxIds = _transactions.value.map { it.id }.toSet()
                    val newTxs = importedTxs.filterNot { it.id in existingTxIds }
                    _transactions.value = newTxs + _transactions.value
                }
                // Merge Wallets
                if (importedWallets.isNotEmpty()) {
                    val existingWalletNames = _wallets.value.map { it.name.trim().lowercase() }.toSet()
                    val existingWalletIds = _wallets.value.map { it.id }.toSet()
                    val newWallets = importedWallets.filterNot {
                        it.id in existingWalletIds || it.name.trim().lowercase() in existingWalletNames
                    }
                    _wallets.value = _wallets.value + newWallets
                }
                // Merge Assets (including records, average lot & stock positions)
                if (importedAssets.isNotEmpty()) {
                    val currentList = _assets.value.toMutableList()
                    val existingMap = currentList.associateBy { it.id }.toMutableMap()
                    val existingByName = currentList.associateBy { it.name.trim().lowercase() }.toMutableMap()

                    importedAssets.forEach { impAsset ->
                        val target = existingMap[impAsset.id] ?: existingByName[impAsset.name.trim().lowercase()]
                        if (target != null) {
                            val targetRecordIds = target.records.map { it.id }.toSet()
                            val newRecords = impAsset.records.filterNot { it.id in targetRecordIds }
                            val mergedRecords = target.records + newRecords

                            val updatedLot = if (impAsset.lotCount > 0) impAsset.lotCount else target.lotCount
                            val updatedAvg = if (impAsset.averagePrice > 0) impAsset.averagePrice else target.averagePrice
                            val updatedCur = if (impAsset.currentPrice > 0) impAsset.currentPrice else target.currentPrice
                            val updatedTicker = if (impAsset.tickerSymbol.isNotBlank()) impAsset.tickerSymbol else target.tickerSymbol
                            val updatedVal = if (impAsset.value > 0) impAsset.value else target.value

                            val updated = target.copy(
                                records = mergedRecords,
                                lotCount = updatedLot,
                                averagePrice = updatedAvg,
                                currentPrice = updatedCur,
                                tickerSymbol = updatedTicker,
                                value = updatedVal
                            )
                            val idx = currentList.indexOfFirst { it.id == target.id }
                            if (idx >= 0) currentList[idx] = updated
                        } else {
                            currentList.add(impAsset)
                            existingMap[impAsset.id] = impAsset
                            existingByName[impAsset.name.trim().lowercase()] = impAsset
                        }
                    }
                    _assets.value = currentList
                }
                // Merge Debts (including records & payment history)
                if (importedDebts.isNotEmpty()) {
                    val currentDebts = _debts.value.toMutableList()
                    val existingMap = currentDebts.associateBy { it.id }.toMutableMap()
                    val existingByName = currentDebts.associateBy { it.name.trim().lowercase() }.toMutableMap()

                    importedDebts.forEach { impDebt ->
                        val target = existingMap[impDebt.id] ?: existingByName[impDebt.name.trim().lowercase()]
                        if (target != null) {
                            val targetRecordIds = target.records.map { it.id }.toSet()
                            val newRecords = impDebt.records.filterNot { it.id in targetRecordIds }
                            val mergedRecords = target.records + newRecords
                            val updated = target.copy(
                                records = mergedRecords,
                                paidAmount = maxOf(target.paidAmount, impDebt.paidAmount)
                            )
                            val idx = currentDebts.indexOfFirst { it.id == target.id }
                            if (idx >= 0) currentDebts[idx] = updated
                        } else {
                            currentDebts.add(impDebt)
                            existingMap[impDebt.id] = impDebt
                            existingByName[impDebt.name.trim().lowercase()] = impDebt
                        }
                    }
                    _debts.value = currentDebts
                }
                // Merge Categories & Subcategories
                if (importedCategories.isNotEmpty()) {
                    val currentCats = _categories.value.toMutableList()
                    importedCategories.forEach { impCat ->
                        val existingIndex = currentCats.indexOfFirst {
                            it.name.trim().equals(impCat.name.trim(), ignoreCase = true) && it.type == impCat.type
                        }
                        if (existingIndex >= 0) {
                            val existingCat = currentCats[existingIndex]
                            val existingSubNames = existingCat.subcategories.map { it.name.trim().lowercase() }.toSet()
                            val newSubs = impCat.subcategories.filterNot { it.name.trim().lowercase() in existingSubNames }
                            if (newSubs.isNotEmpty()) {
                                currentCats[existingIndex] = existingCat.copy(subcategories = existingCat.subcategories + newSubs)
                            }
                        } else {
                            currentCats.add(impCat)
                        }
                    }
                    _categories.value = currentCats
                }
                // Merge Plans
                if (importedPlans.isNotEmpty()) {
                    val existingPlanNames = _plannedItems.value.map { it.name.trim().lowercase() to it.section }.toSet()
                    val newPlans = importedPlans.filterNot { (it.name.trim().lowercase() to it.section) in existingPlanNames }
                    _plannedItems.value = _plannedItems.value + newPlans
                }
                if (importedPlanTitle.isNotBlank() && _planTitle.value.isBlank()) {
                    _planTitle.value = importedPlanTitle
                }
                // Merge Budgets
                if (importedBudgets.isNotEmpty()) {
                    val existingBudgetCats = _budgets.value.map { it.category.trim().lowercase() }.toSet()
                    val newBudgets = importedBudgets.filterNot { it.category.trim().lowercase() in existingBudgetCats }
                    _budgets.value = _budgets.value + newBudgets
                }
                // Merge Recurring
                if (importedRecurring.isNotEmpty()) {
                    val existingRecIds = _recurringTransactions.value.map { it.id }.toSet()
                    val newRec = importedRecurring.filterNot { it.id in existingRecIds }
                    _recurringTransactions.value = _recurringTransactions.value + newRec
                }
                // Merge Upcoming
                if (importedUpcoming.isNotEmpty()) {
                    val existingUpIds = _upcomingItems.value.map { it.id }.toSet()
                    val newUp = importedUpcoming.filterNot { it.id in existingUpIds }
                    _upcomingItems.value = _upcomingItems.value + newUp
                }
                // Merge Labels
                if (importedLabels.isNotEmpty()) {
                    _labels.value = (_labels.value + importedLabels).distinct()
                }
                // Update Settings if provided
                if (importedSettings != null) {
                    _settings.value = importedSettings
                }
            }

            if (ctx != null) {
                IndonesianInstitutionHelper.syncWithWalletsAndAssets(_wallets.value, _assets.value)
            }
            saveToDisk(ctx)
            Result.success(
                com.example.data.model.BackupRestoreResult(
                    transactionCount = importedTxs.size,
                    walletCount = importedWallets.size,
                    assetCount = importedAssets.size,
                    debtCount = importedDebts.size,
                    categoryCount = importedCategories.size,
                    planCount = importedPlans.size,
                    budgetCount = importedBudgets.size,
                    recurringCount = importedRecurring.size,
                    settingsRestored = importedSettings != null,
                    logoCount = restoredLogoCount
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun resetAllData(createDefaultWallet: Boolean = true) {
        _transactions.value = emptyList()
        _wallets.value = if (createDefaultWallet) {
            listOf(
                AccountWallet(
                    name = "Dompet Utama",
                    balance = 0.0,
                    bankType = "Tunai · IDR",
                    currency = "IDR"
                )
            )
        } else {
            emptyList()
        }
        _assets.value = emptyList()
        _debts.value = emptyList()
        _budgets.value = emptyList()
        _upcomingItems.value = emptyList()
        _plannedItems.value = emptyList()
        _recurringTransactions.value = emptyList()
        _selectedWalletFilter.value = "Semua"
        _categories.value = FinanceStorageHelper.getDefaultCategories()
        _labels.value = listOf("Investasi", "Pribadi", "Keluarga", "Tabungan")
        saveToDisk()
    }
}
