package com.example.data.repository

import android.content.Context
import android.util.Log
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
import com.example.data.model.UserSettings
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

object FinanceStorageHelper {

    private const val TAG = "FinanceStorageHelper"
    private const val FILE_NAME = "finance_database.json"

    data class PersistentData(
        val transactions: List<Transaction>,
        val wallets: List<AccountWallet>,
        val assets: List<AssetItem>,
        val debts: List<DebtItem>,
        val budgets: List<BudgetCategory>,
        val recurringTransactions: List<RecurringTransaction>,
        val plannedItems: List<PlannedItem>,
        val planTitle: String,
        val labels: List<String>,
        val settings: UserSettings,
        val selectedWalletFilter: String = "Semua",
        val categories: List<MainCategory> = emptyList()
    )

    fun hasSavedData(context: Context): Boolean {
        val file = File(context.filesDir, FILE_NAME)
        return file.exists() && file.length() > 10
    }

    fun loadData(context: Context): PersistentData? {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists() || file.length() <= 10) return null

            val jsonStr = file.readText(StandardCharsets.UTF_8)
            val root = JSONObject(jsonStr)

            // Transactions
            val txList = mutableListOf<Transaction>()
            val txArr = root.optJSONArray("transactions")
            if (txArr != null) {
                for (i in 0 until txArr.length()) {
                    val o = txArr.getJSONObject(i)
                    txList.add(
                        Transaction(
                            id = o.optString("id", java.util.UUID.randomUUID().toString()),
                            title = o.optString("title", ""),
                            category = o.optString("category", "Other"),
                            subcategory = o.optString("subcategory", ""),
                            amount = o.optDouble("amount", 0.0),
                            type = parseTxType(o.optString("type", "EXPENSE")),
                            accountName = o.optString("accountName", "BRI Utama"),
                            dayOfMonth = o.optInt("dayOfMonth", 1),
                            monthIndex = o.optInt("monthIndex", 10),
                            year = o.optInt("year", 2026),
                            dayOfWeekName = o.optString("dayOfWeekName", "Sen"),
                            note = o.optString("note", ""),
                            label = o.optString("label", ""),
                            recurrence = o.optString("recurrence", "Tidak pernah"),
                            time = o.optString("time", "18:51"),
                            toAccountName = o.optString("toAccountName", o.optString("targetAccountName", "")),
                            transferFee = o.optDouble("transferFee", 0.0),
                            targetAssetId = o.optString("targetAssetId", ""),
                            targetAssetName = o.optString("targetAssetName", ""),
                            targetDebtId = o.optString("targetDebtId", ""),
                            targetDebtName = o.optString("targetDebtName", "")
                        )
                    )
                }
            }

            // Wallets
            val walletList = mutableListOf<AccountWallet>()
            val wArr = root.optJSONArray("wallets")
            if (wArr != null) {
                for (i in 0 until wArr.length()) {
                    val o = wArr.getJSONObject(i)
                    walletList.add(
                        AccountWallet(
                            id = o.optString("id", java.util.UUID.randomUUID().toString()),
                            name = o.optString("name", "Dompet"),
                            bankType = o.optString("bankType", "Bank · IDR"),
                            balance = o.optDouble("balance", 0.0),
                            lastCategory = o.optString("lastCategory", ""),
                            lastAmount = o.optDouble("lastAmount", 0.0),
                            lastActivityTime = o.optString("lastActivityTime", ""),
                            currency = o.optString("currency", "IDR"),
                            iconName = o.optString("iconName", "Bank"),
                            excludeFromNetWorth = o.optBoolean("excludeFromNetWorth", false),
                            excludeFromBudget = o.optBoolean("excludeFromBudget", false)
                        )
                    )
                }
            }

            // Assets
            val assetList = mutableListOf<AssetItem>()
            val aArr = root.optJSONArray("assets")
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
                                    id = ro.optString("id", java.util.UUID.randomUUID().toString()),
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

                    assetList.add(
                        AssetItem(
                            id = o.optString("id", java.util.UUID.randomUUID().toString()),
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
                            iconUri = o.optString("iconUri", ""),
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

            // Debts
            val debtList = mutableListOf<DebtItem>()
            val dArr = root.optJSONArray("debts")
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
                                    id = ro.optString("id", java.util.UUID.randomUUID().toString()),
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

                    debtList.add(
                        DebtItem(
                            id = o.optString("id", java.util.UUID.randomUUID().toString()),
                            name = o.optString("name", "Pinjaman"),
                            type = parseDebtType(o.optString("type", "BORROWED")),
                            totalAmount = o.optDouble("totalAmount", 0.0),
                            paidAmount = o.optDouble("paidAmount", 0.0),
                            dueDate = o.optString("dueDate", "1 Des 2027"),
                            startDate = o.optString("startDate", "28 Mar 2026"),
                            monthlyPayment = o.optDouble("monthlyPayment", 0.0),
                            interestRate = o.optDouble("interestRate", 0.0),
                            extraFees = o.optDouble("extraFees", 0.0),
                            categoryIcon = o.optString("categoryIcon", "CreditCard"),
                            institutionLogo = o.optString("institutionLogo", ""),
                            tenureMonths = o.optInt("tenureMonths", 0),
                            linkedWalletName = o.optString("linkedWalletName", ""),
                            records = recList
                        )
                    )
                }
            }

            // Budgets
            val budgetList = mutableListOf<BudgetCategory>()
            val bArr = root.optJSONArray("budgets")
            if (bArr != null) {
                for (i in 0 until bArr.length()) {
                    val o = bArr.getJSONObject(i)
                    budgetList.add(
                        BudgetCategory(
                            id = o.optString("id", java.util.UUID.randomUUID().toString()),
                            category = o.optString("category", "General"),
                            allocated = o.optDouble("allocated", 0.0),
                            spent = o.optDouble("spent", 0.0)
                        )
                    )
                }
            }

            // Recurring
            val recurList = mutableListOf<RecurringTransaction>()
            val rArr = root.optJSONArray("recurringTransactions")
            if (rArr != null) {
                for (i in 0 until rArr.length()) {
                    val o = rArr.getJSONObject(i)
                    recurList.add(
                        RecurringTransaction(
                            id = o.optString("id", java.util.UUID.randomUUID().toString()),
                            title = o.optString("title", ""),
                            amount = o.optDouble("amount", 0.0),
                            type = parseTxType(o.optString("type", "EXPENSE")),
                            category = o.optString("category", "Bills & Fees"),
                            subcategory = o.optString("subcategory", ""),
                            accountName = o.optString("accountName", "BRI Utama"),
                            dayOfMonth = o.optInt("dayOfMonth", 1),
                            frequency = o.optString("frequency", "Setiap bulan"),
                            startDate = o.optString("startDate", ""),
                            endDate = o.optString("endDate", ""),
                            note = o.optString("note", ""),
                            label = o.optString("label", ""),
                            isActive = o.optBoolean("isActive", true),
                            autoDeduct = o.optBoolean("autoDeduct", true),
                            lastProcessedMonthYear = o.optString("lastProcessedMonthYear", "")
                        )
                    )
                }
            }

            // Categories & Subcategories
            val categoryList = mutableListOf<MainCategory>()
            val cArr = root.optJSONArray("categories")
            if (cArr != null && cArr.length() > 0) {
                for (i in 0 until cArr.length()) {
                    val co = cArr.getJSONObject(i)
                    val subList = mutableListOf<SubcategoryItem>()
                    val subArr = co.optJSONArray("subcategories")
                    if (subArr != null) {
                        for (j in 0 until subArr.length()) {
                            val so = subArr.getJSONObject(j)
                            subList.add(
                                SubcategoryItem(
                                    id = so.optString("id", java.util.UUID.randomUUID().toString()),
                                    name = so.optString("name", ""),
                                    iconName = so.optString("iconName", ""),
                                    isVisible = so.optBoolean("isVisible", true)
                                )
                            )
                        }
                    }
                    categoryList.add(
                        MainCategory(
                            id = co.optString("id", java.util.UUID.randomUUID().toString()),
                            name = co.optString("name", "Kategori"),
                            type = parseTxType(co.optString("type", "EXPENSE")),
                            iconName = co.optString("iconName", ""),
                            isVisible = co.optBoolean("isVisible", true),
                            subcategories = subList
                        )
                    )
                }
            } else {
                categoryList.addAll(getDefaultCategories())
            }

            // Planned Items
            val planList = mutableListOf<PlannedItem>()
            val pArr = root.optJSONArray("plannedItems")
            if (pArr != null) {
                for (i in 0 until pArr.length()) {
                    val o = pArr.getJSONObject(i)
                    planList.add(
                        PlannedItem(
                            id = o.optString("id", java.util.UUID.randomUUID().toString()),
                            name = o.optString("name", ""),
                            section = parsePlanSection(o.optString("section", "EXPENSE")),
                            amount = o.optDouble("amount", 0.0)
                        )
                    )
                }
            }

            // Plan Title
            val planTitle = root.optString("planTitle", "Pembagian Gaji")

            // Labels
            val labelsList = mutableListOf<String>()
            val lArr = root.optJSONArray("labels")
            if (lArr != null) {
                for (i in 0 until lArr.length()) {
                    labelsList.add(lArr.getString(i))
                }
            } else {
                labelsList.addAll(listOf("Investasi", "Susu Egra", "Tabungan", "Pribadi", "Keluarga"))
            }

            // Settings
            val sObj = root.optJSONObject("settings")
            val settings = if (sObj != null) {
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
                    themeColorName = sObj.optString("themeColorName", "FinTrack Blue"),
                    numberFormat = sObj.optString("numberFormat", "1.000.000 (Titik)"),
                    dateFormat = sObj.optString("dateFormat", "DD/MM/YYYY"),
                    defaultHideBalance = sObj.optBoolean("defaultHideBalance", false),
                    dailyReminderTime = sObj.optString("dailyReminderTime", "20:00"),
                    dailyReminderEnabled = sObj.optBoolean("dailyReminderEnabled", true),
                    hapticFeedbackEnabled = sObj.optBoolean("hapticFeedbackEnabled", true)
                )
            } else {
                UserSettings()
            }

            val selectedWalletFilter = root.optString("selectedWalletFilter", "Semua")

            PersistentData(
                transactions = txList,
                wallets = walletList,
                assets = assetList,
                debts = debtList,
                budgets = budgetList,
                recurringTransactions = recurList,
                plannedItems = planList,
                planTitle = planTitle,
                labels = labelsList,
                settings = settings,
                selectedWalletFilter = selectedWalletFilter,
                categories = categoryList
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load database: ${e.message}", e)
            null
        }
    }

    fun saveData(context: Context, data: PersistentData) {
        try {
            val root = JSONObject()

            // Transactions
            val txArr = JSONArray()
            data.transactions.forEach { tx ->
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

            // Wallets
            val wArr = JSONArray()
            data.wallets.forEach { w ->
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
                }
                wArr.put(o)
            }
            root.put("wallets", wArr)

            // Assets
            val aArr = JSONArray()
            data.assets.forEach { a ->
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

            // Debts
            val dArr = JSONArray()
            data.debts.forEach { d ->
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

            // Budgets
            val bArr = JSONArray()
            data.budgets.forEach { b ->
                val o = JSONObject().apply {
                    put("id", b.id)
                    put("category", b.category)
                    put("allocated", b.allocated)
                    put("spent", b.spent)
                }
                bArr.put(o)
            }
            root.put("budgets", bArr)

            // Recurring
            val rArr = JSONArray()
            data.recurringTransactions.forEach { r ->
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
                rArr.put(o)
            }
            root.put("recurringTransactions", rArr)

            // Categories
            val cArr = JSONArray()
            data.categories.forEach { cat ->
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

            // Planned Items
            val pArr = JSONArray()
            data.plannedItems.forEach { p ->
                val o = JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("section", p.section.name)
                    put("amount", p.amount)
                }
                pArr.put(o)
            }
            root.put("plannedItems", pArr)

            // Plan Title
            root.put("planTitle", data.planTitle)

            // Labels
            val lArr = JSONArray()
            data.labels.forEach { lArr.put(it) }
            root.put("labels", lArr)

            // Settings
            val sObj = JSONObject().apply {
                put("language", data.settings.language)
                put("isDarkMode", data.settings.isDarkMode)
                put("primaryCurrency", data.settings.primaryCurrency)
                put("monthlyStartDay", data.settings.monthlyStartDay)
                put("textSizePercent", data.settings.textSizePercent)
                put("cashAccountsOnly", data.settings.cashAccountsOnly)
                put("showPlannedInBalance", data.settings.showPlannedInBalance)
                put("saveReminder", data.settings.saveReminder)
                put("billReminder", data.settings.billReminder)
                put("budgetAlert", data.settings.budgetAlert)
                put("passcodeEnabled", data.settings.passcodeEnabled)
                put("appPasscode", data.settings.appPasscode)
                put("themeColorName", data.settings.themeColorName)
                put("numberFormat", data.settings.numberFormat)
                put("dateFormat", data.settings.dateFormat)
                put("defaultHideBalance", data.settings.defaultHideBalance)
                put("dailyReminderTime", data.settings.dailyReminderTime)
                put("dailyReminderEnabled", data.settings.dailyReminderEnabled)
                put("hapticFeedbackEnabled", data.settings.hapticFeedbackEnabled)
            }
            root.put("settings", sObj)
            root.put("selectedWalletFilter", data.selectedWalletFilter)

            val file = File(context.filesDir, FILE_NAME)
            val tempFile = File(context.filesDir, "$FILE_NAME.tmp")
            tempFile.writeText(root.toString(), StandardCharsets.UTF_8)
            if (tempFile.exists()) {
                tempFile.renameTo(file)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save database: ${e.message}", e)
        }
    }

    private fun parseTxType(typeStr: String): TransactionType {
        return try {
            TransactionType.valueOf(typeStr)
        } catch (e: Exception) {
            TransactionType.EXPENSE
        }
    }

    private fun parseDebtType(typeStr: String): DebtType {
        return try {
            DebtType.valueOf(typeStr)
        } catch (e: Exception) {
            DebtType.BORROWED
        }
    }

    private fun parsePlanSection(secStr: String): PlanSectionType {
        return try {
            PlanSectionType.valueOf(secStr)
        } catch (e: Exception) {
            PlanSectionType.EXPENSE
        }
    }

    fun getDefaultCategories(): List<MainCategory> {
        return listOf(
            MainCategory(
                id = "cat_food",
                name = "Food & Drink",
                type = TransactionType.EXPENSE,
                iconName = "Restaurant",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_resto", name = "Restaurants", iconName = "Restaurant"),
                    SubcategoryItem(id = "sub_cafe", name = "Cafe & Coffee", iconName = "LocalCafe"),
                    SubcategoryItem(id = "sub_delivery", name = "Delivery", iconName = "TwoWheeler"),
                    SubcategoryItem(id = "sub_snacks", name = "Snacks", iconName = "Icecream")
                )
            ),
            MainCategory(
                id = "cat_transport",
                name = "Transport",
                type = TransactionType.EXPENSE,
                iconName = "DirectionsBus",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_fuel", name = "Fuel", iconName = "LocalGasStation"),
                    SubcategoryItem(id = "sub_toll", name = "Toll", iconName = "Toll"),
                    SubcategoryItem(id = "sub_taxi", name = "Taxi", iconName = "LocalTaxi"),
                    SubcategoryItem(id = "sub_transit", name = "Public Transit", iconName = "DirectionsSubway")
                )
            ),
            MainCategory(
                id = "cat_shopping",
                name = "Shopping",
                type = TransactionType.EXPENSE,
                iconName = "ShoppingCart",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_clothes", name = "Pakaian", iconName = "Checkroom"),
                    SubcategoryItem(id = "sub_elec", name = "Elektronik", iconName = "Devices"),
                    SubcategoryItem(id = "sub_grocery", name = "Groceries", iconName = "Storefront"),
                    SubcategoryItem(id = "sub_home", name = "Kebutuhan Rumah", iconName = "Home")
                )
            ),
            MainCategory(
                id = "cat_bills",
                name = "Bills & Fees",
                type = TransactionType.EXPENSE,
                iconName = "ReceiptLong",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_electricity", name = "Listrik", iconName = "Bolt"),
                    SubcategoryItem(id = "sub_water", name = "Air", iconName = "WaterDrop"),
                    SubcategoryItem(id = "sub_wifi", name = "Internet & Wifi", iconName = "Wifi"),
                    SubcategoryItem(id = "sub_pulsa", name = "Pulsa & Data", iconName = "PhoneAndroid"),
                    SubcategoryItem(id = "sub_installment", name = "Cicilan", iconName = "CreditCard")
                )
            ),
            MainCategory(
                id = "cat_entertainment",
                name = "Entertainment",
                type = TransactionType.EXPENSE,
                iconName = "SportsEsports",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_cinema", name = "Bioskop", iconName = "Movie"),
                    SubcategoryItem(id = "sub_games", name = "Game", iconName = "SportsEsports"),
                    SubcategoryItem(id = "sub_subscription", name = "Langganan", iconName = "Subscriptions"),
                    SubcategoryItem(id = "sub_vacation", name = "Liburan", iconName = "Flight")
                )
            ),
            MainCategory(
                id = "cat_health",
                name = "Kesehatan",
                type = TransactionType.EXPENSE,
                iconName = "LocalHospital",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_doctor", name = "Dokter", iconName = "LocalHospital"),
                    SubcategoryItem(id = "sub_medicine", name = "Obat & Vitamin", iconName = "Medication"),
                    SubcategoryItem(id = "sub_insurance", name = "Asuransi", iconName = "Shield")
                )
            ),
            MainCategory(
                id = "cat_education",
                name = "Pendidikan",
                type = TransactionType.EXPENSE,
                iconName = "School",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_course", name = "Kursus", iconName = "LaptopMac"),
                    SubcategoryItem(id = "sub_books", name = "Buku", iconName = "MenuBook"),
                    SubcategoryItem(id = "sub_tuition", name = "SPP / Kuliah", iconName = "School")
                )
            ),
            MainCategory(
                id = "cat_other_exp",
                name = "Other",
                type = TransactionType.EXPENSE,
                iconName = "MoreHoriz",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_misc", name = "Lain-lain", iconName = "Category"),
                    SubcategoryItem(id = "sub_charity", name = "Sedekah / Donasi", iconName = "VolunteerActivism"),
                    SubcategoryItem(id = "sub_tax", name = "Pajak", iconName = "Receipt")
                )
            ),
            // INCOME
            MainCategory(
                id = "cat_salary",
                name = "Salary",
                type = TransactionType.INCOME,
                iconName = "Payments",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_base_salary", name = "Gaji Pokok", iconName = "Payments"),
                    SubcategoryItem(id = "sub_allowance", name = "Tunjangan", iconName = "AccountBalanceWallet"),
                    SubcategoryItem(id = "sub_overtime", name = "Lembur", iconName = "Timer"),
                    SubcategoryItem(id = "sub_bonus", name = "Bonus / THR", iconName = "Celebration")
                )
            ),
            MainCategory(
                id = "cat_invest_income",
                name = "Investasi",
                type = TransactionType.INCOME,
                iconName = "TrendingUp",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_dividend", name = "Dividen", iconName = "ShowChart"),
                    SubcategoryItem(id = "sub_capital_gain", name = "Capital Gain", iconName = "TrendingUp"),
                    SubcategoryItem(id = "sub_interest", name = "Bunga Bank", iconName = "Savings")
                )
            ),
            MainCategory(
                id = "cat_business",
                name = "Bisnis / Freelance",
                type = TransactionType.INCOME,
                iconName = "Work",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_project", name = "Proyek", iconName = "Work"),
                    SubcategoryItem(id = "sub_selling", name = "Jual Barang", iconName = "Storefront"),
                    SubcategoryItem(id = "sub_commission", name = "Komisi", iconName = "AttachMoney")
                )
            ),
            MainCategory(
                id = "cat_gift",
                name = "Pemberian / Hadiah",
                type = TransactionType.INCOME,
                iconName = "CardGiftcard",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_gift_item", name = "Hadiah", iconName = "CardGiftcard"),
                    SubcategoryItem(id = "sub_cashback", name = "Cashback & Reward", iconName = "Redeem")
                )
            ),
            MainCategory(
                id = "cat_extra",
                name = "Extra Income",
                type = TransactionType.INCOME,
                iconName = "AttachMoney",
                subcategories = listOf(
                    SubcategoryItem(id = "sub_sampingan", name = "Sampingan", iconName = "AttachMoney")
                )
            )
        )
    }
}
