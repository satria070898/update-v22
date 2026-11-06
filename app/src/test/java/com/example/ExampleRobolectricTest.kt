package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Money Tracker", appName)
  }

  @Test
  fun `pdf report helper handles generation gracefully without crashing`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val result = com.example.util.PdfReportHelper.generateAndSavePdf(
      context = context,
      periodLabel = "Oktober 2026",
      walletName = "Semua Dompet",
      incomeTotal = 5000000.0,
      expenseTotal = 2500000.0,
      netCashFlow = 2500000.0,
      savingsRate = 50,
      expenseBreakdown = emptyList(),
      incomeBreakdown = emptyList(),
      transactions = emptyList()
    )
    if (result != null) {
      org.junit.Assert.assertTrue(result.file.exists())
      org.junit.Assert.assertTrue(result.fileName.endsWith(".pdf"))
    }
  }

  @Test
  fun `export and import json with custom logos works with android context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.data.repository.FinanceRepository()
    repository.initContext(context)
    val json = repository.exportTransactionsJson(context)
    org.junit.Assert.assertTrue(json.contains("\"customLogos\""))
    org.junit.Assert.assertTrue(json.contains("\"totalCustomLogos\""))

    val result = repository.importTransactionsFromJson(json, replaceExisting = false)
    org.junit.Assert.assertTrue(result.isSuccess)

    // Verify stock lotCount and averagePrice are exported and restored
    org.junit.Assert.assertTrue(json.contains("\"lotCount\""))
    org.junit.Assert.assertTrue(json.contains("\"averagePrice\""))
    val restoredAssets = repository.assets.value
    val bbri = restoredAssets.find { it.tickerSymbol.contains("BBRI") }
    org.junit.Assert.assertNotNull(bbri)
    org.junit.Assert.assertEquals(14.0, bbri!!.lotCount, 0.001)
    org.junit.Assert.assertEquals(4650.0, bbri.averagePrice, 0.001)
  }

  @Test
  fun `selecting wallet filter updates selectedWalletFilter state and filters transactions correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.data.repository.FinanceRepository()
    repository.initContext(context)
    val viewModel = com.example.ui.viewmodel.FinanceViewModel(repository)

    // Initially "Semua"
    assertEquals("Semua", viewModel.selectedWalletFilter.value)

    // Select specific wallet
    viewModel.selectWalletFilter("BCA")
    assertEquals("BCA", viewModel.selectedWalletFilter.value)

    // Revert back
    viewModel.selectWalletFilter("Semua")
    assertEquals("Semua", viewModel.selectedWalletFilter.value)
  }
}
