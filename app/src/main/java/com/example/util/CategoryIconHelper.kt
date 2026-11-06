package com.example.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.model.TransactionType

data class CategoryIconOption(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val group: String
)

object CategoryIconHelper {

    val iconOptions: List<CategoryIconOption> = listOf(
        // Makanan & Minuman
        CategoryIconOption("Restaurant", "Restoran", Icons.Default.Restaurant, "Makanan & Minuman"),
        CategoryIconOption("Fastfood", "Cepat Saji", Icons.Default.Fastfood, "Makanan & Minuman"),
        CategoryIconOption("LocalCafe", "Kafe & Kopi", Icons.Default.LocalCafe, "Makanan & Minuman"),
        CategoryIconOption("LocalPizza", "Pizza & Kuliner", Icons.Default.LocalPizza, "Makanan & Minuman"),
        CategoryIconOption("Icecream", "Snack & Es Krim", Icons.Default.Icecream, "Makanan & Minuman"),
        CategoryIconOption("LocalBar", "Minuman / Bar", Icons.Default.LocalBar, "Makanan & Minuman"),

        // Transportasi
        CategoryIconOption("DirectionsCar", "Mobil & Kendaraan", Icons.Default.DirectionsCar, "Transportasi"),
        CategoryIconOption("TwoWheeler", "Motor & Ojol", Icons.Default.TwoWheeler, "Transportasi"),
        CategoryIconOption("LocalGasStation", "Bensin & BBM", Icons.Default.LocalGasStation, "Transportasi"),
        CategoryIconOption("DirectionsBus", "Bus Umum", Icons.Default.DirectionsBus, "Transportasi"),
        CategoryIconOption("DirectionsSubway", "Kereta & MRT", Icons.Default.DirectionsSubway, "Transportasi"),
        CategoryIconOption("LocalTaxi", "Taksi & Sewa", Icons.Default.LocalTaxi, "Transportasi"),
        CategoryIconOption("Flight", "Pesawat & Wisata", Icons.Default.Flight, "Transportasi"),
        CategoryIconOption("Toll", "Tol & Parkir", Icons.Default.Toll, "Transportasi"),

        // Belanja
        CategoryIconOption("ShoppingBag", "Belanja & Mall", Icons.Default.ShoppingBag, "Belanja"),
        CategoryIconOption("ShoppingCart", "Supermarket", Icons.Default.ShoppingCart, "Belanja"),
        CategoryIconOption("Storefront", "Pasar / Warung", Icons.Default.Storefront, "Belanja"),
        CategoryIconOption("Checkroom", "Pakaian & Fashion", Icons.Default.Checkroom, "Belanja"),
        CategoryIconOption("Devices", "Gadget & Elektronik", Icons.Default.Devices, "Belanja"),

        // Tagihan & Biaya
        CategoryIconOption("Receipt", "Tagihan Umum", Icons.Default.Receipt, "Tagihan"),
        CategoryIconOption("Bolt", "Listrik PLN", Icons.Default.Bolt, "Tagihan"),
        CategoryIconOption("WaterDrop", "Air PDAM", Icons.Default.WaterDrop, "Tagihan"),
        CategoryIconOption("Wifi", "Wifi & Internet", Icons.Default.Wifi, "Tagihan"),
        CategoryIconOption("PhoneAndroid", "Pulsa & Paket Data", Icons.Default.PhoneAndroid, "Tagihan"),
        CategoryIconOption("CreditCard", "Cicilan / Kartu Kredit", Icons.Default.CreditCard, "Tagihan"),

        // Hiburan & Hobi
        CategoryIconOption("SportsEsports", "Game & Steam", Icons.Default.SportsEsports, "Hiburan"),
        CategoryIconOption("Movie", "Bioskop & Film", Icons.Default.Movie, "Hiburan"),
        CategoryIconOption("Subscriptions", "Langganan Streaming", Icons.Default.Subscriptions, "Hiburan"),
        CategoryIconOption("Headphones", "Musik & Podcast", Icons.Default.Headphones, "Hiburan"),
        CategoryIconOption("FitnessCenter", "Gym & Olahraga", Icons.Default.FitnessCenter, "Hiburan"),
        CategoryIconOption("SportsSoccer", "Olahraga / Sepakbola", Icons.Default.SportsSoccer, "Hiburan"),
        CategoryIconOption("TheaterComedy", "Konser & Event", Icons.Default.TheaterComedy, "Hiburan"),

        // Kesehatan & Medis
        CategoryIconOption("LocalHospital", "Rumah Sakit & Dokter", Icons.Default.LocalHospital, "Kesehatan"),
        CategoryIconOption("Medication", "Obat & Farmasi", Icons.Default.Medication, "Kesehatan"),
        CategoryIconOption("Healing", "Perawatan Medis", Icons.Default.Healing, "Kesehatan"),
        CategoryIconOption("Shield", "Asuransi & Proteksi", Icons.Default.Shield, "Kesehatan"),

        // Pendidikan
        CategoryIconOption("School", "Sekolah & Kuliah", Icons.Default.School, "Pendidikan"),
        CategoryIconOption("MenuBook", "Buku & Alat Tulis", Icons.Default.MenuBook, "Pendidikan"),
        CategoryIconOption("AutoStories", "Kursus & Modul", Icons.Default.AutoStories, "Pendidikan"),

        // Rumah & Keluarga
        CategoryIconOption("Home", "Rumah Tangga", Icons.Default.Home, "Rumah & Sosial"),
        CategoryIconOption("VolunteerActivism", "Sedekah / Donasi", Icons.Default.VolunteerActivism, "Rumah & Sosial"),
        CategoryIconOption("CardGiftcard", "Hadiah / Kado", Icons.Default.CardGiftcard, "Rumah & Sosial"),

        // Pemasukan
        CategoryIconOption("Payments", "Gaji Pokok", Icons.Default.Payments, "Pemasukan"),
        CategoryIconOption("MonetizationOn", "Bonus & THR", Icons.Default.MonetizationOn, "Pemasukan"),
        CategoryIconOption("TrendingUp", "Investasi & Saham", Icons.AutoMirrored.Filled.TrendingUp, "Pemasukan"),
        CategoryIconOption("ShowChart", "Dividen / Bunga", Icons.Default.ShowChart, "Pemasukan"),
        CategoryIconOption("Work", "Proyek & Freelance", Icons.Default.Work, "Pemasukan"),
        CategoryIconOption("PointOfSale", "Bisnis & Penjualan", Icons.Default.PointOfSale, "Pemasukan"),
        CategoryIconOption("Savings", "Tabungan & Bunga", Icons.Default.Savings, "Pemasukan"),
        CategoryIconOption("Celebration", "Hadiah & Apresiasi", Icons.Default.Celebration, "Pemasukan"),

        // Transfer & Lainnya
        CategoryIconOption("SwapHoriz", "Transfer Akun", Icons.Default.SwapHoriz, "Lainnya"),
        CategoryIconOption("AccountBalance", "Bank & Keuangan", Icons.Default.AccountBalance, "Lainnya"),
        CategoryIconOption("AccountBalanceWallet", "Dompet Digital", Icons.Default.AccountBalanceWallet, "Lainnya"),
        CategoryIconOption("CurrencyExchange", "Valas / Tukar Uang", Icons.Default.CurrencyExchange, "Lainnya"),
        CategoryIconOption("Category", "Lain-lain", Icons.Default.Category, "Lainnya"),
        CategoryIconOption("MoreHoriz", "Lainnya", Icons.Default.MoreHoriz, "Lainnya")
    )

    private val iconMap: Map<String, ImageVector> = iconOptions.associate { it.key.lowercase() to it.icon }

    fun getCategoryIcon(categoryName: String, iconKey: String = ""): ImageVector {
        if (iconKey.isNotBlank()) {
            iconMap[iconKey.lowercase()]?.let { return it }
        }

        val name = categoryName.lowercase()
        return when {
            // Makanan
            name.contains("food") || name.contains("makan") || name.contains("resto") ||
                    name.contains("kuliner") || name.contains("sarapan") || name.contains("lunch") ||
                    name.contains("dinner") -> Icons.Default.Restaurant
            name.contains("cafe") || name.contains("coffee") || name.contains("kopi") -> Icons.Default.LocalCafe
            name.contains("snack") || name.contains("jajan") || name.contains("ice") || name.contains("es krim") -> Icons.Default.Icecream
            name.contains("fast") || name.contains("burger") || name.contains("pizza") -> Icons.Default.Fastfood
            name.contains("bar") || name.contains("minum") || name.contains("drink") -> Icons.Default.LocalBar

            // Transportasi
            name.contains("fuel") || name.contains("bensin") || name.contains("spbu") || name.contains("pertamax") || name.contains("pertalite") -> Icons.Default.LocalGasStation
            name.contains("toll") || name.contains("tol") -> Icons.Default.Toll
            name.contains("taxi") || name.contains("taksi") || name.contains("gocar") || name.contains("grabcar") -> Icons.Default.LocalTaxi
            name.contains("motor") || name.contains("ojol") || name.contains("goride") || name.contains("grabike") || name.contains("delivery") -> Icons.Default.TwoWheeler
            name.contains("transit") || name.contains("kereta") || name.contains("mrt") || name.contains("krl") || name.contains("lrt") -> Icons.Default.DirectionsSubway
            name.contains("bus") || name.contains("transjakarta") -> Icons.Default.DirectionsBus
            name.contains("flight") || name.contains("pesawat") || name.contains("tiket") -> Icons.Default.Flight
            name.contains("transport") || name.contains("mobil") || name.contains("kendaraan") -> Icons.Default.DirectionsCar

            // Belanja
            name.contains("groceries") || name.contains("pasar") || name.contains("warung") || name.contains("sayur") -> Icons.Default.Storefront
            name.contains("market") || name.contains("supermarket") || name.contains("alfamart") || name.contains("indomaret") -> Icons.Default.ShoppingCart
            name.contains("clothes") || name.contains("pakaian") || name.contains("baju") || name.contains("celana") || name.contains("sepatu") -> Icons.Default.Checkroom
            name.contains("elektronik") || name.contains("gadget") || name.contains("device") || name.contains("hp") || name.contains("laptop") -> Icons.Default.Devices
            name.contains("shop") || name.contains("belanja") -> Icons.Default.ShoppingBag

            // Tagihan
            name.contains("listrik") || name.contains("pln") || name.contains("token") -> Icons.Default.Bolt
            name.contains("air") || name.contains("pdam") -> Icons.Default.WaterDrop
            name.contains("wifi") || name.contains("internet") || name.contains("indihome") || name.contains("biznet") -> Icons.Default.Wifi
            name.contains("pulsa") || name.contains("paket data") || name.contains("telkomsel") || name.contains("indosat") || name.contains("xl") -> Icons.Default.PhoneAndroid
            name.contains("bill") || name.contains("tagihan") || name.contains("pajak") || name.contains("fee") -> Icons.Default.Receipt
            name.contains("cicilan") || name.contains("kredit") || name.contains("paylater") -> Icons.Default.CreditCard

            // Hiburan
            name.contains("game") || name.contains("steam") || name.contains("esports") || name.contains("playstation") -> Icons.Default.SportsEsports
            name.contains("bioskop") || name.contains("movie") || name.contains("cinema") || name.contains("film") || name.contains("xxi") -> Icons.Default.Movie
            name.contains("streaming") || name.contains("netflix") || name.contains("youtube") || name.contains("spotify") || name.contains("langganan") || name.contains("subscription") -> Icons.Default.Subscriptions
            name.contains("music") || name.contains("musik") || name.contains("lagu") -> Icons.Default.Headphones
            name.contains("gym") || name.contains("fitness") || name.contains("olahraga") || name.contains("workout") -> Icons.Default.FitnessCenter
            name.contains("bola") || name.contains("futsal") || name.contains("badminton") -> Icons.Default.SportsSoccer
            name.contains("hiburan") || name.contains("entertain") || name.contains("rekreasi") || name.contains("liburan") -> Icons.Default.Celebration

            // Kesehatan
            name.contains("dokter") || name.contains("rumah sakit") || name.contains("rs") || name.contains("klinik") -> Icons.Default.LocalHospital
            name.contains("obat") || name.contains("vitamin") || name.contains("apotek") || name.contains("pharmacy") -> Icons.Default.Medication
            name.contains("asuransi") || name.contains("bpjs") || name.contains("insurance") -> Icons.Default.Shield
            name.contains("sehat") || name.contains("health") || name.contains("medis") -> Icons.Default.Healing

            // Pendidikan
            name.contains("spp") || name.contains("kuliah") || name.contains("sekolah") || name.contains("tuition") -> Icons.Default.School
            name.contains("buku") || name.contains("book") || name.contains("novel") -> Icons.Default.MenuBook
            name.contains("kursus") || name.contains("course") || name.contains("belajar") || name.contains("pendidikan") || name.contains("education") -> Icons.Default.AutoStories

            // Rumah & Donasi
            name.contains("rumah") || name.contains("kos") || name.contains("kontrakan") || name.contains("home") -> Icons.Default.Home
            name.contains("sedekah") || name.contains("donasi") || name.contains("zakat") || name.contains("infaq") || name.contains("amal") -> Icons.Default.VolunteerActivism
            name.contains("hadiah") || name.contains("kado") || name.contains("gift") -> Icons.Default.CardGiftcard

            // Pemasukan
            name.contains("salary") || name.contains("gaji") || name.contains("upah") || name.contains("payroll") -> Icons.Default.Payments
            name.contains("bonus") || name.contains("thr") || name.contains("insentif") || name.contains("tip") -> Icons.Default.MonetizationOn
            name.contains("invest") || name.contains("saham") || name.contains("reksadana") || name.contains("kripto") || name.contains("crypto") -> Icons.AutoMirrored.Filled.TrendingUp
            name.contains("dividen") || name.contains("dividend") || name.contains("bunga") -> Icons.Default.ShowChart
            name.contains("bisnis") || name.contains("freelance") || name.contains("proyek") || name.contains("usaha") -> Icons.Default.Work
            name.contains("jual") || name.contains("dagang") || name.contains("toko") || name.contains("sales") -> Icons.Default.PointOfSale
            name.contains("extra") || name.contains("sampingan") || name.contains("tabung") -> Icons.Default.Savings
            name.contains("cashback") || name.contains("reward") || name.contains("point") -> Icons.Default.Celebration

            // Transfer, Aset & Utang
            name.contains("utang") || name.contains("hutang") || name.contains("pinjam") || name.contains("cicilan") -> Icons.Default.Handshake
            name.contains("transfer") || name.contains("kirim") || name.contains("antar akun") -> Icons.Default.SwapHoriz
            name.contains("asset") || name.contains("aset") -> Icons.AutoMirrored.Filled.TrendingUp
            name.contains("top up") || name.contains("topup") -> Icons.Default.AccountBalanceWallet

            else -> Icons.Default.Category
        }
    }

    fun guessIconKeyForCategory(categoryName: String): String {
        val name = categoryName.lowercase()
        return when {
            name.contains("food") || name.contains("makan") || name.contains("resto") || name.contains("kuliner") -> "Restaurant"
            name.contains("cafe") || name.contains("coffee") || name.contains("kopi") -> "LocalCafe"
            name.contains("fast") || name.contains("burger") || name.contains("pizza") -> "Fastfood"
            name.contains("snack") || name.contains("ice") || name.contains("es krim") -> "Icecream"
            name.contains("fuel") || name.contains("bensin") || name.contains("spbu") -> "LocalGasStation"
            name.contains("toll") || name.contains("tol") -> "Toll"
            name.contains("motor") || name.contains("ojol") -> "TwoWheeler"
            name.contains("mobil") || name.contains("kendaraan") || name.contains("transport") -> "DirectionsCar"
            name.contains("bus") -> "DirectionsBus"
            name.contains("kereta") || name.contains("mrt") || name.contains("krl") -> "DirectionsSubway"
            name.contains("pesawat") || name.contains("flight") -> "Flight"
            name.contains("shop") || name.contains("belanja") -> "ShoppingBag"
            name.contains("market") || name.contains("supermarket") || name.contains("pasar") -> "ShoppingCart"
            name.contains("baju") || name.contains("pakaian") || name.contains("fashion") -> "Checkroom"
            name.contains("elektronik") || name.contains("gadget") || name.contains("hp") -> "Devices"
            name.contains("listrik") || name.contains("pln") -> "Bolt"
            name.contains("air") || name.contains("pdam") -> "WaterDrop"
            name.contains("wifi") || name.contains("internet") -> "Wifi"
            name.contains("pulsa") || name.contains("paket data") -> "PhoneAndroid"
            name.contains("bill") || name.contains("tagihan") -> "Receipt"
            name.contains("kredit") || name.contains("paylater") -> "CreditCard"
            name.contains("game") || name.contains("gaming") -> "SportsEsports"
            name.contains("film") || name.contains("bioskop") || name.contains("movie") -> "Movie"
            name.contains("langganan") || name.contains("netflix") || name.contains("spotify") -> "Subscriptions"
            name.contains("gym") || name.contains("fitness") || name.contains("olahraga") -> "FitnessCenter"
            name.contains("dokter") || name.contains("rs") || name.contains("rumah sakit") -> "LocalHospital"
            name.contains("obat") || name.contains("apotek") || name.contains("vitamin") -> "Medication"
            name.contains("asuransi") || name.contains("bpjs") -> "Shield"
            name.contains("sekolah") || name.contains("kuliah") || name.contains("spp") -> "School"
            name.contains("buku") || name.contains("book") -> "MenuBook"
            name.contains("rumah") || name.contains("kos") || name.contains("kontrakan") -> "Home"
            name.contains("sedekah") || name.contains("donasi") || name.contains("zakat") -> "VolunteerActivism"
            name.contains("hadiah") || name.contains("kado") -> "CardGiftcard"
            name.contains("salary") || name.contains("gaji") -> "Payments"
            name.contains("bonus") || name.contains("thr") -> "MonetizationOn"
            name.contains("invest") || name.contains("saham") -> "ShowChart"
            name.contains("bisnis") || name.contains("usaha") -> "Work"
            name.contains("transfer") -> "SwapHoriz"
            name.contains("tabung") || name.contains("savings") -> "Savings"
            else -> "Category"
        }
    }

    private val fallbackColors = listOf(
        Color(0xFF3B82F6), // Royal Blue
        Color(0xFF10B981), // Emerald Green
        Color(0xFFF59E0B), // Amber Gold
        Color(0xFFEC4899), // Pink Rose
        Color(0xFF8B5CF6), // Purple Violet
        Color(0xFF06B6D4), // Cyan
        Color(0xFFF97316), // Vivid Orange
        Color(0xFF6366F1), // Indigo
        Color(0xFF14B8A6), // Teal
        Color(0xFFEF4444)  // Ruby Red
    )

    fun getCategoryColor(categoryName: String, type: TransactionType = TransactionType.EXPENSE): Color {
        val name = categoryName.lowercase()

        if (type == TransactionType.INCOME) {
            return when {
                name.contains("salary") || name.contains("gaji") || name.contains("upah") -> Color(0xFF10B981) // Emerald Green
                name.contains("bonus") || name.contains("thr") || name.contains("insentif") -> Color(0xFFF59E0B) // Amber Gold
                name.contains("invest") || name.contains("saham") || name.contains("reksadana") || name.contains("crypto") -> Color(0xFF3B82F6) // Royal Blue
                name.contains("dividen") || name.contains("bunga") -> Color(0xFF8B5CF6) // Purple
                name.contains("bisnis") || name.contains("usaha") || name.contains("toko") || name.contains("freelance") -> Color(0xFFEC4899) // Rose Pink
                name.contains("tabung") || name.contains("savings") -> Color(0xFF06B6D4) // Vivid Cyan
                name.contains("hadiah") || name.contains("reward") || name.contains("cashback") -> Color(0xFFD946EF) // Magenta
                else -> Color(0xFF10B981) // Vibrant Mint
            }
        }

        if (name.contains("utang") || name.contains("hutang") || name.contains("pinjam") || name.contains("cicilan")) {
            return Color(0xFFFF5722)
        }

        if (type == TransactionType.TRANSFER || name.contains("transfer") || name.contains("asset") || name.contains("aset")) {
            return if (name.contains("asset") || name.contains("aset")) Color(0xFFF59E0B) else Color(0xFF0EA5E9)
        }

        return when {
            // Makanan & Minuman
            name.contains("food") || name.contains("makan") || name.contains("resto") || name.contains("kuliner") -> Color(0xFFFF6D00) // Deep Orange
            name.contains("cafe") || name.contains("coffee") || name.contains("kopi") -> Color(0xFFD97706) // Caramel Amber
            name.contains("fast") || name.contains("burger") || name.contains("pizza") -> Color(0xFFFF5252) // Coral Red
            name.contains("snack") || name.contains("jajan") || name.contains("ice") || name.contains("es krim") -> Color(0xFFF43F5E) // Bright Rose
            name.contains("bar") || name.contains("minum") || name.contains("drink") -> Color(0xFF8B5CF6) // Purple
            
            // Transportasi
            name.contains("fuel") || name.contains("bensin") || name.contains("spbu") || name.contains("pertamax") || name.contains("pertalite") -> Color(0xFF00BCD4) // Vivid Cyan
            name.contains("toll") || name.contains("tol") -> Color(0xFF0284C7) // Sky Blue
            name.contains("motor") || name.contains("ojol") || name.contains("goride") || name.contains("grabike") -> Color(0xFF84CC16) // Lime Green
            name.contains("taxi") || name.contains("taksi") || name.contains("mobil") || name.contains("transport") -> Color(0xFF0EA5E9) // Electric Blue
            name.contains("transit") || name.contains("kereta") || name.contains("mrt") || name.contains("krl") -> Color(0xFF6366F1) // Indigo
            name.contains("bus") || name.contains("transjakarta") -> Color(0xFF3B82F6) // Blue
            name.contains("flight") || name.contains("pesawat") -> Color(0xFF0284C7) // Sky Blue
            
            // Belanja
            name.contains("groceries") || name.contains("pasar") || name.contains("sayur") -> Color(0xFF14B8A6) // Teal
            name.contains("market") || name.contains("supermarket") || name.contains("alfamart") || name.contains("indomaret") -> Color(0xFF10B981) // Emerald
            name.contains("clothes") || name.contains("pakaian") || name.contains("baju") || name.contains("fashion") -> Color(0xFFEC4899) // Rose Pink
            name.contains("elektronik") || name.contains("gadget") || name.contains("device") || name.contains("hp") || name.contains("laptop") -> Color(0xFF9333EA) // Deep Purple
            name.contains("shop") || name.contains("belanja") || name.contains("mall") -> Color(0xFFF43F5E) // Hot Pink
            
            // Tagihan
            name.contains("listrik") || name.contains("pln") || name.contains("token") -> Color(0xFFEAB308) // Amber
            name.contains("air") || name.contains("pdam") -> Color(0xFF0284C7) // Sky Blue
            name.contains("wifi") || name.contains("internet") || name.contains("indihome") -> Color(0xFF3B82F6) // Royal Blue
            name.contains("pulsa") || name.contains("paket data") || name.contains("telkomsel") -> Color(0xFFEF4444) // Bright Red
            name.contains("bill") || name.contains("tagihan") || name.contains("pajak") || name.contains("fee") -> Color(0xFFF59E0B) // Gold Amber
            name.contains("cicilan") || name.contains("kredit") || name.contains("paylater") -> Color(0xFFE11D48) // Crimson
            
            // Hiburan & Hobi
            name.contains("game") || name.contains("steam") || name.contains("esports") -> Color(0xFF8B5CF6) // Violet Purple
            name.contains("bioskop") || name.contains("movie") || name.contains("cinema") || name.contains("film") -> Color(0xFFF43F5E) // Rose Red
            name.contains("streaming") || name.contains("netflix") || name.contains("spotify") || name.contains("langganan") -> Color(0xFFE11D48) // Red/Violet
            name.contains("music") || name.contains("musik") -> Color(0xFFD946EF) // Magenta
            name.contains("gym") || name.contains("fitness") || name.contains("olahraga") || name.contains("workout") -> Color(0xFF06B6D4) // Vivid Cyan
            name.contains("bola") || name.contains("futsal") || name.contains("badminton") -> Color(0xFF10B981) // Green
            name.contains("hiburan") || name.contains("entertain") || name.contains("liburan") -> Color(0xFFF59E0B) // Amber
            
            // Kesehatan
            name.contains("dokter") || name.contains("rumah sakit") || name.contains("rs") || name.contains("klinik") -> Color(0xFFEF4444) // Bright Red
            name.contains("obat") || name.contains("vitamin") || name.contains("apotek") || name.contains("pharmacy") -> Color(0xFFF43F5E) // Rose
            name.contains("asuransi") || name.contains("bpjs") || name.contains("insurance") -> Color(0xFF3B82F6) // Shield Blue
            name.contains("sehat") || name.contains("health") || name.contains("medis") -> Color(0xFF10B981) // Health Green
            
            // Pendidikan
            name.contains("spp") || name.contains("kuliah") || name.contains("sekolah") -> Color(0xFF2563EB) // Blue
            name.contains("buku") || name.contains("book") || name.contains("kursus") || name.contains("belajar") -> Color(0xFF3B82F6) // Royal Blue
            
            // Rumah & Donasi
            name.contains("rumah") || name.contains("kos") || name.contains("kontrakan") || name.contains("home") -> Color(0xFF6366F1) // Indigo
            name.contains("sedekah") || name.contains("donasi") || name.contains("zakat") || name.contains("infaq") || name.contains("amal") -> Color(0xFF10B981) // Emerald Green
            name.contains("hadiah") || name.contains("kado") || name.contains("gift") -> Color(0xFFEC4899) // Pink
            
            // Lainnya: Always return a vivid palette color instead of dull gray
            else -> {
                val index = Math.abs(categoryName.hashCode()) % fallbackColors.size
                fallbackColors[index]
            }
        }
    }

    /**
     * Requirement: Color of subcategory icon must always match its parent category's color.
     */
    fun getSubcategoryColor(categoryName: String, subcategoryName: String = "", type: TransactionType = TransactionType.EXPENSE): Color {
        return getCategoryColor(categoryName, type)
    }
}
