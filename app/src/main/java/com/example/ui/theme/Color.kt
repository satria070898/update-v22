package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

object AppThemeState {
    var currentAccentColor: Color by mutableStateOf(Color(0xFF2F70F2))
    var currentThemeName: String by mutableStateOf("FinTrack Blue")
    var isDarkMode: Boolean by mutableStateOf(true)
}

// 18 Rich & Vibrant Theme Color Options
val AvailableThemePalettes = listOf(
    Pair("FinTrack Blue", Color(0xFF2F70F2)),
    Pair("Zamrud Emerald", Color(0xFF10B981)),
    Pair("Ungu Royal", Color(0xFF8B5CF6)),
    Pair("Amber Sunset", Color(0xFFF59E0B)),
    Pair("Koral Merah", Color(0xFFF43F5E)),
    Pair("Teal Bahari", Color(0xFF14B8A6)),
    Pair("Cyan Electric", Color(0xFF06B6D4)),
    Pair("Rose Gold", Color(0xFFEC4899)),
    Pair("Indigo Midnight", Color(0xFF6366F1)),
    Pair("Hijau Mint", Color(0xFF22C55E)),
    Pair("Oranye Terakota", Color(0xFFEA580C)),
    Pair("Merah Ruby", Color(0xFFE11D48)),
    Pair("Lavender Pastel", Color(0xFFA855F7)),
    Pair("Biru Laut Dalam", Color(0xFF0284C7)),
    Pair("Emas Mewah", Color(0xFFD97706)),
    Pair("Hijau Hutan", Color(0xFF059669)),
    Pair("Magenta Neon", Color(0xFFD946EF)),
    Pair("Hitam Monokrom", Color(0xFF475569))
)

fun getThemeColorByName(name: String): Color {
    return AvailableThemePalettes.find { it.first.equals(name, ignoreCase = true) }?.second
        ?: Color(0xFF2F70F2) // FinTrack Blue default
}

// Background and Surface Colors (Pure Solid Black Background, Slightly Dark Gray for Cards and Toolbars)
val DarkBackground: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFF000000) else Color(0xFFFFFFFF)

val DarkSurface: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFF20232A) else Color(0xFFFFFFFF)

val DarkSurfaceElevated: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFF282C35) else Color(0xFFF1F5F9)

val DarkSurfaceVariant: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFF323640) else Color(0xFFE2E8F0)

val DarkCardBorder: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFF3B404D) else Color(0xFFE2E8F0)

val DarkCardSubtle: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFF242730) else Color(0xFFF8FAFC)

// Dynamic Accent Colors reflecting current selected theme
val AccentBlue: Color
    get() = AppThemeState.currentAccentColor

val AccentBlueBright: Color
    get() = AppThemeState.currentAccentColor

val AccentBlueDark: Color
    get() = AppThemeState.currentAccentColor.copy(alpha = 0.6f)

val AccentBlueContainer: Color
    get() = AppThemeState.currentAccentColor.copy(alpha = 0.2f)

// Financial Indicators
val IncomeGreen = Color(0xFF22C55E)
val IncomeGreenDark = Color(0xFF166534)
val IncomeGreenSoft = Color(0x2622C55E)

val ExpenseRed = Color(0xFFF43F5E)
val ExpenseRedDark = Color(0xFF9F1239)
val ExpenseRedSoft = Color(0x26F43F5E)

val ExpenseHeatTile: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFF2E1A22) else Color(0xFFFEE2E2)

val ExpenseHeatTileActive: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFF451E28) else Color(0xFFFECACA)

// Gamification / Warning Colors
val TrophyYellow = Color(0xFFF59E0B)
val TrophyYellowLight = Color(0xFFFBBF24)
val TrophyYellowSoft = Color(0x33F59E0B)

// Adaptive Text Colors (Crisp White in Dark Mode, Deep Slate in Light Mode)
val TextWhite: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)

val TextMuted: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

val TextSubtle: Color
    get() = if (AppThemeState.isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8)

// Net Worth Segment Colors
val SegmentAccountBlue = Color(0xFF38BDF8)
val SegmentAssetGreen = Color(0xFF34D399)
val SegmentDebtRed = Color(0xFFF87171)

// Dynamic Top Header Background (Solid Black in Dark Mode, Soft Tone in Light Mode)
fun getTopHeaderGradient(themeColor: Color = AccentBlue): Brush {
    return if (AppThemeState.isDarkMode) {
        SolidColor(Color(0xFF000000))
    } else {
        Brush.verticalGradient(
            colors = listOf(
                themeColor.copy(alpha = 0.08f),
                Color(0xFFFFFFFF),
                Color(0xFFFFFFFF)
            )
        )
    }
}

// Dynamic Balance Card Gradient tailored to selected Theme Color and Dark/Light Mode
fun getBalanceCardGradient(themeColor: Color = AccentBlue, isDark: Boolean = AppThemeState.isDarkMode): Brush {
    return if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(
                    red = (themeColor.red * 0.28f + 0.04f).coerceIn(0f, 1f),
                    green = (themeColor.green * 0.28f + 0.04f).coerceIn(0f, 1f),
                    blue = (themeColor.blue * 0.28f + 0.06f).coerceIn(0f, 1f),
                    alpha = 1f
                ),
                Color(
                    red = (themeColor.red * 0.60f).coerceIn(0f, 1f),
                    green = (themeColor.green * 0.60f).coerceIn(0f, 1f),
                    blue = (themeColor.blue * 0.60f).coerceIn(0f, 1f),
                    alpha = 1f
                ),
                Color(
                    red = (themeColor.red * 0.32f + 0.03f).coerceIn(0f, 1f),
                    green = (themeColor.green * 0.32f + 0.03f).coerceIn(0f, 1f),
                    blue = (themeColor.blue * 0.32f + 0.05f).coerceIn(0f, 1f),
                    alpha = 1f
                )
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                themeColor,
                Color(
                    red = (themeColor.red * 0.85f).coerceIn(0f, 1f),
                    green = (themeColor.green * 0.85f).coerceIn(0f, 1f),
                    blue = (themeColor.blue * 0.85f).coerceIn(0f, 1f),
                    alpha = 1f
                ),
                Color(
                    red = (themeColor.red * 0.70f).coerceIn(0f, 1f),
                    green = (themeColor.green * 0.70f).coerceIn(0f, 1f),
                    blue = (themeColor.blue * 0.70f).coerceIn(0f, 1f),
                    alpha = 1f
                )
            )
        )
    }
}
