package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.FinanceRepository
import com.example.util.TradingViewHelper
import com.example.util.TradingViewQuote
import com.example.util.TransactionMathHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MonthOption(
    val monthIndex: Int,
    val label: String,
    val year: Int
) {
    val first: Int get() = monthIndex
    val second: String get() = label
}

class FinanceViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    companion object {
        fun generatePastYearMonthOptions(): List<MonthOption> {
            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
            val list = mutableListOf<MonthOption>()
            // Start from 11 months ago up to the current month (total 12 months, ending on current month)
            val cal = Calendar.getInstance().apply {
                add(Calendar.MONTH, -11)
            }
            for (i in 0..11) {
                val m = cal.get(Calendar.MONTH) + 1
                val y = cal.get(Calendar.YEAR)
                val label = "${monthNames[m - 1]} $y"
                list.add(MonthOption(monthIndex = m, label = label, year = y))
                cal.add(Calendar.MONTH, 1)
            }
            return list
        }
    }

    private val _isRefreshingStockPrices = MutableStateFlow(false)
    val isRefreshingStockPrices: StateFlow<Boolean> = _isRefreshingStockPrices.asStateFlow()

    private val _lastStockPriceRefreshTime = MutableStateFlow(System.currentTimeMillis())
    val lastStockPriceRefreshTime: StateFlow<Long> = _lastStockPriceRefreshTime.asStateFlow()

    private val _liveStockQuotes = MutableStateFlow<Map<String, TradingViewQuote>>(TradingViewHelper.getAllCachedQuotes())
    val liveStockQuotes: StateFlow<Map<String, TradingViewQuote>> = _liveStockQuotes.asStateFlow()

    init {
        // Automatically check and process any due recurring transactions upon initialization
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val currentMonth = cal.get(Calendar.MONTH) + 1
        val currentYear = cal.get(Calendar.YEAR)
        repository.checkAndExecuteDueRecurringTransactions(currentDay, currentMonth, currentYear)

        // Initial real-time stock price sync from TradingView
        refreshStockPrices()

        // Background live stock price ticker polling (every 10s for real-time automatic portfolio movement)
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(10_000)
                try {
                    val quotes = repository.refreshStockPricesFromTradingView()
                    if (quotes.isNotEmpty()) {
                        _liveStockQuotes.value = quotes
                        _lastStockPriceRefreshTime.value = System.currentTimeMillis()
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun initStorage(context: android.content.Context) {
        repository.initContext(context)
    }

    // Filter states dynamically ending at current month and year
    private val _selectedMonthIndex = MutableStateFlow(
        Calendar.getInstance().get(Calendar.MONTH) + 1
    )
    val selectedMonthIndex: StateFlow<Int> = _selectedMonthIndex.asStateFlow()

    private val _selectedYear = MutableStateFlow(
        Calendar.getInstance().get(Calendar.YEAR)
    )
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    val selectedWalletFilter: StateFlow<String> = repository.selectedWalletFilter

    private val _isBalanceHidden = MutableStateFlow(false)
    val isBalanceHidden: StateFlow<Boolean> = _isBalanceHidden.asStateFlow()

    private val _summaryTab = MutableStateFlow(1) // 0: Statistik, 1: Laporan, 2: Kalender
    val summaryTab: StateFlow<Int> = _summaryTab.asStateFlow()

    private val _summaryPeriod = MutableStateFlow(0) // 0: Bulanan, 1: Tahunan, 2: Kustom
    val summaryPeriod: StateFlow<Int> = _summaryPeriod.asStateFlow()

    private val _accountSubTab = MutableStateFlow(1) // 0: Nilai Bersih, 1: Akun, 2: Aset, 3: Utang (default 1: Akun)
    val accountSubTab: StateFlow<Int> = _accountSubTab.asStateFlow()

    private val _selectedWalletIdForDetail = MutableStateFlow<String?>(null)
    val selectedWalletIdForDetail: StateFlow<String?> = _selectedWalletIdForDetail.asStateFlow()

    private val _selectedAssetIdForDetail = MutableStateFlow<String?>(null)
    val selectedAssetIdForDetail: StateFlow<String?> = _selectedAssetIdForDetail.asStateFlow()

    private val _selectedDebtIdForDetail = MutableStateFlow<String?>(null)
    val selectedDebtIdForDetail: StateFlow<String?> = _selectedDebtIdForDetail.asStateFlow()

    fun selectDebtForDetail(id: String?) {
        _selectedDebtIdForDetail.value = id
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    val wallets: StateFlow<List<AccountWallet>> = repository.wallets
    val assets: StateFlow<List<AssetItem>> = repository.assets
    val debts: StateFlow<List<DebtItem>> = repository.debts
    val upcomingItems: StateFlow<List<UpcomingScheduleItem>> = repository.upcomingItems
    val budgets: StateFlow<List<BudgetCategory>> = repository.budgets
    val settings: StateFlow<UserSettings> = repository.settings
    val labels: StateFlow<List<String>> = repository.labels
    val categories: StateFlow<List<MainCategory>> = repository.categories
    val recurringTransactions: StateFlow<List<RecurringTransaction>> = repository.recurringTransactions
    val planTitle: StateFlow<String> = repository.planTitle
    val plannedItems: StateFlow<List<PlannedItem>> = repository.plannedItems

    val plannedIncomeTotal: StateFlow<Double> = repository.plannedItems.map { list ->
        list.filter { it.section == PlanSectionType.INCOME }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5758000.0)

    val plannedExpenseTotal: StateFlow<Double> = repository.plannedItems.map { list ->
        list.filter { it.section == PlanSectionType.EXPENSE }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4150000.0)

    val plannedSavingsTotal: StateFlow<Double> = repository.plannedItems.map { list ->
        list.filter { it.section == PlanSectionType.SAVINGS }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1600000.0)

    val unallocatedAmount: StateFlow<Double> = combine(
        plannedIncomeTotal,
        plannedExpenseTotal,
        plannedSavingsTotal
    ) { income, expense, savings ->
        income - (expense + savings)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8000.0)

    // Filtered transactions
    val allTransactions: StateFlow<List<Transaction>> = repository.transactions
    val transactions: StateFlow<List<Transaction>> = combine(
        repository.transactions,
        _selectedMonthIndex,
        _selectedYear,
        repository.selectedWalletFilter,
        _searchQuery
    ) { txs, month, year, wallet, query ->
        txs.filter { tx ->
            val matchMonth = tx.monthIndex == month
            val matchYear = tx.year == year
            val matchWallet = wallet == "Semua" ||
                    tx.accountName.equals(wallet, ignoreCase = true) ||
                    (tx.type == TransactionType.TRANSFER && tx.toAccountName.equals(wallet, ignoreCase = true))
            val matchQuery = query.isBlank() ||
                    tx.title.contains(query, ignoreCase = true) ||
                    tx.category.contains(query, ignoreCase = true) ||
                    tx.accountName.contains(query, ignoreCase = true) ||
                    tx.toAccountName.contains(query, ignoreCase = true) ||
                    tx.targetAssetName.contains(query, ignoreCase = true) ||
                    tx.targetDebtName.contains(query, ignoreCase = true) ||
                    tx.note.contains(query, ignoreCase = true)
            matchMonth && matchYear && matchWallet && matchQuery
        }.sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Total Wallet Balance
    val totalWalletBalance: StateFlow<Double> = repository.wallets.combine(repository.selectedWalletFilter) { list, filter ->
        if (filter == "Semua") {
            list.sumOf { it.balance }
        } else {
            list.find { it.name.equals(filter, ignoreCase = true) }?.balance ?: 0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 458228.0)

    // Combined all-wallet balance
    val totalAllWalletsBalance: StateFlow<Double> = repository.wallets.map { list ->
        list.sumOf { it.balance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 458228.0)

    // Monthly Income
    val monthlyIncome: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6182200.0)

    // Monthly Expense
    val monthlyExpense: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1852462.0)

    // Monthly Transfer (Transfer Antar Akun & Transfer Aset)
    val monthlyTransfer: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.TRANSFER }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Total Assets (calculates real market value for stocks based on real-time prices)
    val totalAssetsValue: StateFlow<Double> = repository.assets.map { list ->
        list.sumOf { it.realValue }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 15773865.0)

    // Stock Assets & Real-Time Portfolio Valuations
    val stockAssets: StateFlow<List<AssetItem>> = repository.assets.map { list ->
        list.filter { it.isStock || it.tickerSymbol.isNotBlank() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalStockPortfolioRealValue: StateFlow<Double> = stockAssets.map { list ->
        list.sumOf { it.realValue }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalStockCostBasis: StateFlow<Double> = stockAssets.map { list ->
        list.sumOf { it.costBasis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalStockUnrealizedGainLoss: StateFlow<Double> = combine(
        totalStockPortfolioRealValue,
        totalStockCostBasis
    ) { realVal, cost ->
        realVal - cost
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalStockPerformancePercent: StateFlow<Double> = combine(
        totalStockPortfolioRealValue,
        totalStockCostBasis
    ) { realVal, cost ->
        if (cost > 0.0) ((realVal - cost) / cost) * 100.0 else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Total Debts (remaining)
    val totalDebtsValue: StateFlow<Double> = repository.debts.map { list ->
        list.filter { it.type == DebtType.BORROWED }.sumOf { it.totalAmount - it.paidAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8050000.0)

    val totalLentValue: StateFlow<Double> = repository.debts.map { list ->
        list.filter { it.type == DebtType.LENT }.sumOf { it.totalAmount - it.paidAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Net Worth = Total Balance + Total Assets - Total Debts
    val netWorthValue: StateFlow<Double> = combine(
        totalAllWalletsBalance,
        totalAssetsValue,
        totalDebtsValue
    ) { balance, assets, debts ->
        balance + assets - debts
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8182093.0)

    // Calendar daily expenses mapping (day -> total expense)
    val calendarDailyExpenses: StateFlow<Map<Int, Double>> = combine(
        repository.transactions,
        _selectedMonthIndex,
        _selectedYear
    ) { txs, month, year ->
        val map = mutableMapOf<Int, Double>()
        txs.filter { it.monthIndex == month && it.year == year && it.type == TransactionType.EXPENSE }.forEach { tx ->
            map[tx.dayOfMonth] = (map[tx.dayOfMonth] ?: 0.0) + tx.amount
        }
        map
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Month options dynamically reflecting 1 year back ending on the current month
    private val _monthOptions = MutableStateFlow(generatePastYearMonthOptions())
    val monthOptions: StateFlow<List<MonthOption>> = _monthOptions.asStateFlow()

    fun selectMonth(month: Int) {
        val match = _monthOptions.value.lastOrNull { it.monthIndex == month }
        if (match != null) {
            _selectedMonthIndex.value = match.monthIndex
            _selectedYear.value = match.year
        } else {
            _selectedMonthIndex.value = month
        }
    }

    fun selectYear(year: Int) {
        _selectedYear.value = year
    }

    fun selectMonthAndYear(month: Int, year: Int) {
        _selectedMonthIndex.value = month
        _selectedYear.value = year
    }

    fun selectWalletFilter(walletName: String) {
        repository.selectWalletFilter(walletName)
    }

    fun toggleBalanceVisibility() {
        _isBalanceHidden.value = !_isBalanceHidden.value
    }

    fun setSummaryTab(tab: Int) {
        _summaryTab.value = tab
    }

    fun setSummaryPeriod(period: Int) {
        _summaryPeriod.value = period
    }

    fun setAccountSubTab(tab: Int) {
        _accountSubTab.value = tab
    }

    fun selectWalletForDetail(walletId: String?) {
        _selectedWalletIdForDetail.value = walletId
    }

    fun selectAssetForDetail(assetId: String?) {
        _selectedAssetIdForDetail.value = assetId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearch() {
        _isSearchOpen.value = !_isSearchOpen.value
        if (!_isSearchOpen.value) {
            _searchQuery.value = ""
        }
    }

    fun openSearch(query: String = "") {
        _isSearchOpen.value = true
        _searchQuery.value = query
    }

    fun addTransaction(tx: Transaction) {
        repository.addTransaction(tx)
    }

    fun updateTransaction(tx: Transaction) {
        repository.updateTransaction(tx)
    }

    fun deleteTransaction(txId: String) {
        repository.deleteTransaction(txId)
    }

    fun batchDeleteTransactions(txIds: Set<String>) {
        repository.batchDeleteTransactions(txIds)
    }

    fun batchUpdateCategory(txIds: Set<String>, newCategory: String, newSubcategory: String = "") {
        repository.batchUpdateCategory(txIds, newCategory, newSubcategory)
    }

    fun batchUpdateLabel(txIds: Set<String>, newLabel: String) {
        repository.batchUpdateLabel(txIds, newLabel)
    }

    fun addWallet(wallet: AccountWallet) {
        repository.addWallet(wallet)
    }

    fun addWallet(name: String, bankType: String, initialBalance: Double) {
        repository.addWallet(
            AccountWallet(
                name = name,
                bankType = bankType,
                balance = initialBalance
            )
        )
    }

    fun updateWallet(wallet: AccountWallet) {
        repository.updateWallet(wallet)
    }

    fun deleteWallet(walletId: String) {
        repository.deleteWallet(walletId)
    }

    fun addAsset(asset: AssetItem) {
        repository.addAsset(asset)
    }

    fun addAsset(name: String, group: String, value: Double, platform: String) {
        repository.addAsset(
            AssetItem(
                name = name,
                group = group,
                value = value,
                platform = platform
            )
        )
    }

    fun updateAsset(asset: AssetItem) {
        repository.updateAsset(asset)
    }

    fun updateStockCurrentPrice(assetId: String, newPrice: Double) {
        repository.updateStockCurrentPrice(assetId, newPrice)
    }

    fun refreshStockPrices() {
        viewModelScope.launch {
            _isRefreshingStockPrices.value = true
            try {
                val quotes = repository.refreshStockPricesFromTradingView()
                if (quotes.isNotEmpty()) {
                    _liveStockQuotes.value = quotes
                    _lastStockPriceRefreshTime.value = System.currentTimeMillis()
                }
            } catch (_: Exception) {
            } finally {
                _isRefreshingStockPrices.value = false
            }
        }
    }

    fun refreshSingleStock(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshingStockPrices.value = true
            try {
                TradingViewHelper.fetchSingleQuote(symbol)
                val quotes = repository.refreshStockPricesFromTradingView()
                if (quotes.isNotEmpty()) {
                    _liveStockQuotes.value = quotes
                    _lastStockPriceRefreshTime.value = System.currentTimeMillis()
                }
            } catch (_: Exception) {
            } finally {
                _isRefreshingStockPrices.value = false
            }
        }
    }

    fun deleteAsset(assetId: String) {
        repository.deleteAsset(assetId)
    }

    fun addAssetRecord(assetId: String, record: AssetRecord, updateWalletBalance: Boolean = true) {
        repository.addAssetRecord(assetId, record, updateWalletBalance)
    }

    fun updateAssetRecord(assetId: String, oldRecord: AssetRecord, updatedRecord: AssetRecord, updateWalletBalance: Boolean = true) {
        repository.updateAssetRecord(assetId, oldRecord, updatedRecord, updateWalletBalance)
    }

    fun deleteAssetRecord(assetId: String, recordId: String, revertWalletBalance: Boolean = true) {
        repository.deleteAssetRecord(assetId, recordId, revertWalletBalance)
    }

    fun addDebt(name: String, totalAmount: Double, dueDate: String, type: DebtType = DebtType.BORROWED, paidAmount: Double = 0.0) {
        repository.addDebt(
            DebtItem(
                name = name,
                type = type,
                totalAmount = totalAmount,
                paidAmount = paidAmount,
                dueDate = dueDate
            )
        )
    }

    fun addDebt(debt: DebtItem, depositToWalletName: String? = null) {
        repository.addDebt(debt, depositToWalletName)
    }

    fun updateDebt(debt: DebtItem) {
        repository.updateDebt(debt)
    }

    fun markDebtFullyPaid(debtId: String) {
        repository.markDebtFullyPaid(debtId)
    }

    fun deleteDebt(debtId: String, deleteLinkedTransactions: Boolean = false) {
        repository.deleteDebt(debtId, deleteLinkedTransactions)
    }

    fun payDebt(debtId: String, amount: Double, walletName: String? = null, note: String = "Pembayaran cicilan") {
        repository.payDebt(debtId, amount, walletName, note)
    }

    fun addDebtRecord(debtId: String, record: DebtRecord, affectWalletName: String? = null) {
        repository.addDebtRecord(debtId, record, affectWalletName)
    }

    fun updateDebtRecord(debtId: String, record: DebtRecord) {
        repository.updateDebtRecord(debtId, record)
    }

    fun deleteDebtRecord(debtId: String, recordId: String) {
        repository.deleteDebtRecord(debtId, recordId)
    }

    fun addBudget(category: String, allocated: Double) {
        repository.addBudget(
            BudgetCategory(
                category = category,
                allocated = allocated,
                spent = 0.0
            )
        )
    }

    fun updateSettings(newSettings: UserSettings) {
        repository.updateSettings(newSettings)
    }

    fun addLabel(name: String) {
        repository.addLabel(name)
    }

    fun deleteLabel(name: String) {
        repository.deleteLabel(name)
    }

    fun addRecurringTransaction(recurring: RecurringTransaction) {
        val cal = java.util.Calendar.getInstance()
        val currentDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val currentMonth = cal.get(java.util.Calendar.MONTH) + 1
        val currentYear = cal.get(java.util.Calendar.YEAR)
        repository.addRecurringTransaction(
            recurring = recurring,
            checkImmediateProcess = true,
            currentDay = currentDay,
            currentMonth = currentMonth,
            currentYear = currentYear
        )
    }

    fun updateRecurringTransaction(recurring: RecurringTransaction) {
        repository.updateRecurringTransaction(recurring)
    }

    fun deleteRecurringTransaction(id: String) {
        repository.deleteRecurringTransaction(id)
    }

    fun toggleRecurringActive(id: String) {
        repository.toggleRecurringActive(id)
    }

    fun processRecurringTransaction(id: String) {
        val cal = java.util.Calendar.getInstance()
        val currentMonth = cal.get(java.util.Calendar.MONTH) + 1
        val currentYear = cal.get(java.util.Calendar.YEAR)
        repository.processRecurringTransaction(id, currentMonth, currentYear)
    }

    fun checkAndExecuteDueRecurringTransactions(): Int {
        val cal = java.util.Calendar.getInstance()
        val currentDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val currentMonth = cal.get(java.util.Calendar.MONTH) + 1
        val currentYear = cal.get(java.util.Calendar.YEAR)
        return repository.checkAndExecuteDueRecurringTransactions(currentDay, currentMonth, currentYear)
    }

    fun updatePlanTitle(newTitle: String) {
        repository.updatePlanTitle(newTitle)
    }

    fun addPlannedItem(item: PlannedItem) {
        repository.addPlannedItem(item)
    }

    fun updatePlannedItem(item: PlannedItem) {
        repository.updatePlannedItem(item)
    }

    fun deletePlannedItem(id: String) {
        repository.deletePlannedItem(id)
    }

    fun exportTransactionsJson(context: android.content.Context? = null): String {
        return repository.exportTransactionsJson(context)
    }

    fun importTransactionsFromJson(
        jsonStr: String,
        replaceExisting: Boolean,
        context: android.content.Context? = null
    ): Result<com.example.data.model.BackupRestoreResult> {
        return repository.importTransactionsFromJson(jsonStr, replaceExisting, context)
    }

    fun addCategory(category: MainCategory) {
        repository.addCategory(category)
    }

    fun updateCategory(category: MainCategory) {
        repository.updateCategory(category)
    }

    fun deleteCategory(categoryId: String) {
        repository.deleteCategory(categoryId)
    }

    fun addSubcategory(categoryId: String, subcategory: SubcategoryItem) {
        repository.addSubcategory(categoryId, subcategory)
    }

    fun updateSubcategory(categoryId: String, subcategory: SubcategoryItem) {
        repository.updateSubcategory(categoryId, subcategory)
    }

    fun deleteSubcategory(categoryId: String, subcategoryId: String) {
        repository.deleteSubcategory(categoryId, subcategoryId)
    }

    fun toggleCategoryVisibility(categoryId: String) {
        repository.toggleCategoryVisibility(categoryId)
    }

    fun toggleSubcategoryVisibility(categoryId: String, subcategoryId: String) {
        repository.toggleSubcategoryVisibility(categoryId, subcategoryId)
    }

    fun resetAllData(createDefaultWallet: Boolean = true) {
        repository.resetAllData(createDefaultWallet)
    }
}
