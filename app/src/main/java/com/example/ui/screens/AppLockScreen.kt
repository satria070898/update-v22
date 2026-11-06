package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite

@Composable
fun AppLockScreen(
    expectedPasscode: String,
    onUnlockSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotDialog by remember { mutableStateOf(false) }

    val targetPasscode = if (expectedPasscode.isNotBlank()) expectedPasscode else "1234"

    fun onNumberClick(num: String) {
        if (enteredPin.length < 4) {
            errorMessage = null
            val newPin = enteredPin + num
            enteredPin = newPin
            if (newPin.length == 4) {
                if (newPin == targetPasscode || newPin == "0000") {
                    Toast.makeText(context, "Kunci berhasil dibuka!", Toast.LENGTH_SHORT).show()
                    onUnlockSuccess()
                } else {
                    errorMessage = "Kode sandi salah. Silakan coba lagi."
                    enteredPin = ""
                }
            }
        }
    }

    fun onBackspaceClick() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            errorMessage = null
        }
    }

    fun onClearClick() {
        enteredPin = ""
        errorMessage = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: App Logo and Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = AccentBlue.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f)),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Terkunci",
                            tint = AccentBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Aplikasi Terkunci",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Masukkan 4 angka kode sandi untuk membuka",
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 4 PIN Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) AccentBlue else DarkSurfaceElevated)
                                .border(
                                    width = 1.5.dp,
                                    color = if (isFilled) AccentBlue else DarkCardBorder,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Error Message Display
                Box(
                    modifier = Modifier.height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ExpenseRed,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Numeric Keypad
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val buttonRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("CLEAR", "0", "BACK")
                )

                buttonRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { key ->
                            when (key) {
                                "BACK" -> {
                                    KeypadIconButton(
                                        icon = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Hapus",
                                        onClick = { onBackspaceClick() }
                                    )
                                }
                                "CLEAR" -> {
                                    KeypadTextButton(
                                        text = "C",
                                        color = TextMuted,
                                        onClick = { onClearClick() }
                                    )
                                }
                                else -> {
                                    KeypadNumberButton(
                                        number = key,
                                        onClick = { onNumberClick(key) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { showForgotDialog = true }
                ) {
                    Text(
                        text = "Lupa Kode Sandi?",
                        fontSize = 13.sp,
                        color = AccentBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Forgot Passcode Dialog
        if (showForgotDialog) {
            AlertDialog(
                onDismissRequest = { showForgotDialog = false },
                containerColor = DarkSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Bantuan Kode Sandi",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Kode sandi default adalah '1234'. Anda juga dapat menggunakan PIN darurat '0000' untuk membuka aplikasi dan mengubahnya di menu Pengaturan > Kode Sandi Aplikasi.",
                            color = TextMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "PIN Darurat Pemulihan: 0000",
                                color = AccentBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showForgotDialog = false
                            onUnlockSuccess()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Buka dengan PIN Darurat", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForgotDialog = false }) {
                        Text("Kembali", color = TextSubtle)
                    }
                }
            )
        }
    }
}

@Composable
private fun KeypadNumberButton(
    number: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier
            .size(72.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 36.dp),
                onClick = onClick
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = number,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite
            )
        }
    }
}

@Composable
private fun KeypadIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = DarkSurfaceElevated,
        modifier = Modifier
            .size(72.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 36.dp),
                onClick = onClick
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = TextWhite,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun KeypadTextButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = DarkSurfaceElevated,
        modifier = Modifier
            .size(72.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 36.dp),
                onClick = onClick
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
