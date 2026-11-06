package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.util.CategoryIconHelper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MainCategory
import com.example.data.model.SubcategoryItem
import com.example.data.model.TransactionType
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.FinanceViewModel

@Composable
fun ManageCategoriesDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()

    // 0: Pengeluaran, 1: Pendapatan
    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog states
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddSubcategoryDialog by remember { mutableStateOf<MainCategory?>(null) }
    var categoryToEdit by remember { mutableStateOf<MainCategory?>(null) }
    var subcategoryToEdit by remember { mutableStateOf<Pair<MainCategory, SubcategoryItem>?>(null) }
    var categoryToDelete by remember { mutableStateOf<MainCategory?>(null) }
    var subcategoryToDelete by remember { mutableStateOf<Pair<MainCategory, SubcategoryItem>?>(null) }

    val expenseCategories = categories.filter { it.type == TransactionType.EXPENSE }
    val incomeCategories = categories.filter { it.type == TransactionType.INCOME }

    val totalExpenseSubcategories = expenseCategories.sumOf { it.subcategories.size }
    val totalIncomeSubcategories = incomeCategories.sumOf { it.subcategories.size }

    val displayedCategories = if (selectedTab == 0) expenseCategories else incomeCategories

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Bar matching Screenshot
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_close_categories_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextWhite
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Kategori",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.size(36.dp))
                }

                // Two Segment Tabs: [ ↓ Pengeluaran (N) ]  [ ↑ Pendapatan (N) ]
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131720)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Pengeluaran Tab
                        val isExpenseSelected = selectedTab == 0
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 0 }
                                .testTag("tab_categories_expense"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isExpenseSelected) Color(0xFF1C273C) else Color.Transparent,
                            border = if (isExpenseSelected) BorderStroke(1.dp, Color(0xFF2B3D5B)) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = if (isExpenseSelected) AccentBlue else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Pengeluaran ($totalExpenseSubcategories)",
                                    fontSize = 13.sp,
                                    fontWeight = if (isExpenseSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isExpenseSelected) AccentBlue else TextMuted
                                )
                            }
                        }

                        // Pendapatan Tab
                        val isIncomeSelected = selectedTab == 1
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 1 }
                                .testTag("tab_categories_income"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isIncomeSelected) Color(0xFF162B28) else Color.Transparent,
                            border = if (isIncomeSelected) BorderStroke(1.dp, Color(0xFF21463F)) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = if (isIncomeSelected) IncomeGreen else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Pendapatan ($totalIncomeSubcategories)",
                                    fontSize = 13.sp,
                                    fontWeight = if (isIncomeSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isIncomeSelected) IncomeGreen else TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List of Categories and Subcategories
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedCategories, key = { it.id }) { cat ->
                        var isExpanded by remember { mutableStateOf(true) }
                        var showMenu by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Main Category Item Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF171D27))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isExpanded = !isExpanded }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Category Icon matching meaning with distinctive color
                                    val catColor = CategoryIconHelper.getCategoryColor(cat.name, cat.type)
                                    val catIcon = CategoryIconHelper.getCategoryIcon(cat.name, cat.iconName)
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(catColor.copy(alpha = 0.2f))
                                            .border(1.dp, catColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = catIcon,
                                            contentDescription = cat.name,
                                            tint = catColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = cat.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (cat.isVisible) TextWhite else TextMuted
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${cat.subcategories.size} subkategori",
                                            fontSize = 12.sp,
                                            color = TextMuted
                                        )
                                    }

                                    // Action icons matching Screenshot: Eye, Dots Menu, Drag Handle
                                    IconButton(
                                        onClick = { viewModel.toggleCategoryVisibility(cat.id) },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (cat.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Visibilitas",
                                            tint = if (cat.isVisible) TextWhite.copy(alpha = 0.8f) else TextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Box {
                                        IconButton(
                                            onClick = { showMenu = true },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = "Menu",
                                                tint = TextWhite.copy(alpha = 0.8f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showMenu,
                                            onDismissRequest = { showMenu = false },
                                            modifier = Modifier.background(DarkSurfaceElevated)
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Tambah Subkategori", color = TextWhite) },
                                                leadingIcon = { Icon(Icons.Default.Add, null, tint = AccentBlue) },
                                                onClick = {
                                                    showMenu = false
                                                    showAddSubcategoryDialog = cat
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Ubah Kategori", color = TextWhite) },
                                                leadingIcon = { Icon(Icons.Default.Edit, null, tint = TextMuted) },
                                                onClick = {
                                                    showMenu = false
                                                    categoryToEdit = cat
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Hapus Kategori", color = ExpenseRed) },
                                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = ExpenseRed) },
                                                onClick = {
                                                    showMenu = false
                                                    categoryToDelete = cat
                                                }
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.DragHandle,
                                        contentDescription = "Urutkan",
                                        tint = TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Subcategories underneath
                            if (isExpanded) {
                                cat.subcategories.forEach { sub ->
                                    var showSubMenu by remember { mutableStateOf(false) }

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 14.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131822))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Vertical Orange/Amber Indicator Bar `|`
                                            Box(
                                                modifier = Modifier
                                                    .width(3.dp)
                                                    .height(26.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(Color(0xFFD97706))
                                            )

                                            Spacer(modifier = Modifier.width(10.dp))

                                            // Subcategory Icon container - Color matches parent category
                                            val subColor = CategoryIconHelper.getSubcategoryColor(cat.name, sub.name, cat.type)
                                            val subIcon = CategoryIconHelper.getCategoryIcon(sub.name, sub.iconName)
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(subColor.copy(alpha = 0.2f))
                                                    .border(0.8.dp, subColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = subIcon,
                                                    contentDescription = sub.name,
                                                    tint = subColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Text(
                                                text = sub.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (sub.isVisible) TextWhite else TextMuted,
                                                modifier = Modifier.weight(1f)
                                            )

                                            // Action icons: Eye, Menu, Drag Handle
                                            IconButton(
                                                onClick = { viewModel.toggleSubcategoryVisibility(cat.id, sub.id) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (sub.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Visibilitas",
                                                    tint = if (sub.isVisible) TextWhite.copy(alpha = 0.8f) else TextMuted,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            Box {
                                                IconButton(
                                                    onClick = { showSubMenu = true },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.MoreVert,
                                                        contentDescription = "Menu",
                                                        tint = TextWhite.copy(alpha = 0.8f),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }

                                                DropdownMenu(
                                                    expanded = showSubMenu,
                                                    onDismissRequest = { showSubMenu = false },
                                                    modifier = Modifier.background(DarkSurfaceElevated)
                                                ) {
                                                    DropdownMenuItem(
                                                        text = { Text("Ubah Nama", color = TextWhite) },
                                                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = TextMuted) },
                                                        onClick = {
                                                            showSubMenu = false
                                                            subcategoryToEdit = Pair(cat, sub)
                                                        }
                                                    )
                                                    DropdownMenuItem(
                                                        text = { Text("Hapus Subkategori", color = ExpenseRed) },
                                                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = ExpenseRed) },
                                                        onClick = {
                                                            showSubMenu = false
                                                            subcategoryToDelete = Pair(cat, sub)
                                                        }
                                                    )
                                                }
                                            }

                                            Icon(
                                                imageVector = Icons.Default.DragHandle,
                                                contentDescription = "Urutkan",
                                                tint = TextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }

            // Floating Action Button (+) matching Screenshot
            FloatingActionButton(
                onClick = { showAddCategoryDialog = true },
                containerColor = AccentBlue,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 20.dp)
                    .size(56.dp)
                    .testTag("fab_add_category")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Kategori",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

    // Modal 1: Tambah Kategori Baru
    if (showAddCategoryDialog) {
        var newCatName by remember { mutableStateOf("") }
        var newCatType by remember { mutableStateOf(if (selectedTab == 0) TransactionType.EXPENSE else TransactionType.INCOME) }
        var selectedIcon by remember { mutableStateOf("Category") }
        var showIconPickerForAdd by remember { mutableStateOf(false) }

        val previewColor = CategoryIconHelper.getCategoryColor(newCatName.ifBlank { "Umum" }, newCatType)
        val previewIcon = CategoryIconHelper.getCategoryIcon(newCatName, selectedIcon)

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            containerColor = DarkSurface,
            title = {
                Text("Tambah Kategori Baru", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 17.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newCatName,
                        onValueChange = { 
                            newCatName = it
                            if (selectedIcon == "Category" && it.isNotBlank()) {
                                // Find best match if user hasn't explicitly chosen custom icon
                                val bestMatch = CategoryIconHelper.iconOptions.firstOrNull { opt ->
                                    it.contains(opt.label, ignoreCase = true) || it.contains(opt.key, ignoreCase = true)
                                }
                                if (bestMatch != null) {
                                    selectedIcon = bestMatch.key
                                }
                            }
                        },
                        label = { Text("Nama Kategori", color = TextSubtle) },
                        placeholder = { Text("Contoh: Makanan, Transportasi", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        )
                    )

                    // Type Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceElevated, RoundedCornerShape(10.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { newCatType = TransactionType.EXPENSE },
                            shape = RoundedCornerShape(8.dp),
                            color = if (newCatType == TransactionType.EXPENSE) ExpenseRed else Color.Transparent
                        ) {
                            Text(
                                "Pengeluaran",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { newCatType = TransactionType.INCOME },
                            shape = RoundedCornerShape(8.dp),
                            color = if (newCatType == TransactionType.INCOME) IncomeGreen else Color.Transparent
                        ) {
                            Text(
                                "Pendapatan",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    // Icon Selector Row
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Ikon Kategori", fontSize = 12.sp, color = TextSubtle)
                            Text(
                                text = "Ganti Ikon",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentBlue,
                                modifier = Modifier.clickable { showIconPickerForAdd = true }
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, previewColor.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showIconPickerForAdd = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(previewColor.copy(alpha = 0.2f))
                                            .border(1.dp, previewColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = previewIcon,
                                            contentDescription = null,
                                            tint = previewColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = selectedIcon.ifBlank { "Pilih Ikon" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                }
                                Text("Ubah", fontSize = 11.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCatName.isNotBlank()) {
                            viewModel.addCategory(
                                MainCategory(
                                    name = newCatName.trim(),
                                    type = newCatType,
                                    iconName = selectedIcon,
                                    subcategories = listOf(
                                        SubcategoryItem(name = "Umum", iconName = selectedIcon)
                                    )
                                )
                            )
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )

        if (showIconPickerForAdd) {
            CategoryIconPickerModal(
                selectedIconKey = selectedIcon,
                onSelectIcon = { selectedIcon = it },
                onDismiss = { showIconPickerForAdd = false }
            )
        }
    }

    // Modal 2: Tambah Subkategori
    showAddSubcategoryDialog?.let { parentCat ->
        var subName by remember { mutableStateOf("") }
        var subIcon by remember { mutableStateOf(parentCat.iconName) }
        var showSubIconPicker by remember { mutableStateOf(false) }

        val previewIcon = CategoryIconHelper.getCategoryIcon(subName.ifBlank { parentCat.name }, subIcon)

        AlertDialog(
            onDismissRequest = { showAddSubcategoryDialog = null },
            containerColor = DarkSurface,
            title = {
                Text("Tambah Subkategori untuk ${parentCat.name}", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Kategori Induk: ${parentCat.name}", fontSize = 12.sp, color = TextMuted)
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { 
                            subName = it
                            val detected = CategoryIconHelper.guessIconKeyForCategory(it)
                            if (detected != "Category") {
                                subIcon = detected
                            }
                        },
                        label = { Text("Nama Subkategori", color = TextSubtle) },
                        placeholder = { Text("Contoh: Kopi, Bensin, Restoran", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        )
                    )

                    // Pilihan Ikon Subkategori
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Ikon Subkategori", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                        Surface(
                            onClick = { showSubIconPicker = true },
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val parentColor = CategoryIconHelper.getCategoryColor(parentCat.name, parentCat.type)
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(parentColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = previewIcon,
                                            contentDescription = null,
                                            tint = parentColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = subIcon.ifBlank { "Pilih Ikon" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                }
                                Text("Ubah", fontSize = 11.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subName.isNotBlank()) {
                            viewModel.addSubcategory(
                                parentCat.id,
                                SubcategoryItem(name = subName.trim(), iconName = subIcon)
                            )
                            showAddSubcategoryDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Tambah", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubcategoryDialog = null }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )

        if (showSubIconPicker) {
            CategoryIconPickerModal(
                selectedIconKey = subIcon,
                onSelectIcon = { subIcon = it },
                onDismiss = { showSubIconPicker = false }
            )
        }
    }

    // Modal 3: Ubah Nama & Ikon Kategori
    categoryToEdit?.let { cat ->
        var editName by remember { mutableStateOf(cat.name) }
        var editIcon by remember { mutableStateOf(cat.iconName) }
        var showEditIconPicker by remember { mutableStateOf(false) }

        val previewIcon = CategoryIconHelper.getCategoryIcon(editName, editIcon)
        val previewColor = CategoryIconHelper.getCategoryColor(editName, cat.type)

        AlertDialog(
            onDismissRequest = { categoryToEdit = null },
            containerColor = DarkSurface,
            title = { Text("Ubah Kategori", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama Kategori", color = TextSubtle) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        )
                    )

                    // Pilihan Ikon
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Ikon Kategori", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                        Surface(
                            onClick = { showEditIconPicker = true },
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(previewColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = previewIcon,
                                            contentDescription = null,
                                            tint = previewColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = editIcon.ifBlank { "Pilih Ikon" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                }
                                Text("Ubah", fontSize = 11.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            viewModel.updateCategory(cat.copy(name = editName.trim(), iconName = editIcon))
                            categoryToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToEdit = null }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )

        if (showEditIconPicker) {
            CategoryIconPickerModal(
                selectedIconKey = editIcon,
                onSelectIcon = { editIcon = it },
                onDismiss = { showEditIconPicker = false }
            )
        }
    }

    // Modal 4: Ubah Subkategori
    subcategoryToEdit?.let { (parentCat, sub) ->
        var editSubName by remember { mutableStateOf(sub.name) }
        var editSubIcon by remember { mutableStateOf(sub.iconName) }
        var showEditSubIconPicker by remember { mutableStateOf(false) }

        val previewIcon = CategoryIconHelper.getCategoryIcon(editSubName, editSubIcon)

        AlertDialog(
            onDismissRequest = { subcategoryToEdit = null },
            containerColor = DarkSurface,
            title = { Text("Ubah Subkategori", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editSubName,
                        onValueChange = { editSubName = it },
                        label = { Text("Nama Subkategori", color = TextSubtle) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        )
                    )

                    // Pilihan Ikon
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Ikon Subkategori", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                        Surface(
                            onClick = { showEditSubIconPicker = true },
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val parentColor = CategoryIconHelper.getCategoryColor(parentCat.name, parentCat.type)
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(parentColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = previewIcon,
                                            contentDescription = null,
                                            tint = parentColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = editSubIcon.ifBlank { "Pilih Ikon" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                }
                                Text("Ubah", fontSize = 11.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editSubName.isNotBlank()) {
                            viewModel.updateSubcategory(parentCat.id, sub.copy(name = editSubName.trim(), iconName = editSubIcon))
                            subcategoryToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { subcategoryToEdit = null }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )

        if (showEditSubIconPicker) {
            CategoryIconPickerModal(
                selectedIconKey = editSubIcon,
                onSelectIcon = { editSubIcon = it },
                onDismiss = { showEditSubIconPicker = false }
            )
        }
    }

    // Modal 5: Konfirmasi Hapus Kategori
    categoryToDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            containerColor = DarkSurface,
            title = { Text("Hapus Kategori?", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus kategori \"${cat.name}\" beserta seluruh ${cat.subcategories.size} subkategorinya?",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(cat.id)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )
    }

    // Modal 6: Konfirmasi Hapus Subkategori
    subcategoryToDelete?.let { (parentCat, sub) ->
        AlertDialog(
            onDismissRequest = { subcategoryToDelete = null },
            containerColor = DarkSurface,
            title = { Text("Hapus Subkategori?", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = {
                Text("Hapus subkategori \"${sub.name}\" dari kategori ${parentCat.name}?", color = TextMuted)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSubcategory(parentCat.id, sub.id)
                        subcategoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { subcategoryToDelete = null }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )
    }
}

@Composable
fun CategoryIconPickerModal(
    selectedIconKey: String,
    onSelectIcon: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var activeGroup by remember { androidx.compose.runtime.mutableStateOf("Semua") }
    val allOptions = CategoryIconHelper.iconOptions
    val groups = listOf("Semua", "Makanan & Minuman", "Transportasi", "Belanja", "Tagihan", "Hiburan", "Kesehatan", "Pendidikan", "Pemasukan", "Lainnya")

    val filtered = remember(activeGroup) {
        if (activeGroup == "Semua") allOptions else allOptions.filter { it.group.contains(activeGroup, ignoreCase = true) || activeGroup.contains(it.group, ignoreCase = true) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pilih Ikon Kategori",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Group Pills
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(groups) { group ->
                        val isSel = activeGroup == group
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSel) AccentBlue else DarkSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSel) AccentBlue else DarkCardBorder),
                            modifier = Modifier.clickable { activeGroup = group }
                        ) {
                            Text(
                                text = group,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else TextWhite,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(280.dp)
                ) {
                    items(filtered) { opt ->
                        val isSelected = selectedIconKey.equals(opt.key, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentBlue.copy(alpha = 0.25f) else DarkSurfaceElevated)
                                .border(
                                    BorderStroke(1.2.dp, if (isSelected) AccentBlue else DarkCardBorder),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onSelectIcon(opt.key)
                                    onDismiss()
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = opt.icon,
                                    contentDescription = opt.label,
                                    tint = if (isSelected) AccentBlue else TextWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = opt.label,
                                    fontSize = 9.sp,
                                    color = if (isSelected) AccentBlue else TextMuted,
                                    maxLines = 1,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated)
                ) {
                    Text("Tutup", color = TextWhite, fontSize = 13.sp)
                }
            }
        }
    }
}

fun getCategoryColor(name: String, type: TransactionType): Color {
    return CategoryIconHelper.getCategoryColor(name, type)
}

fun getSubcategoryIcon(name: String): ImageVector {
    return CategoryIconHelper.getCategoryIcon(name)
}
