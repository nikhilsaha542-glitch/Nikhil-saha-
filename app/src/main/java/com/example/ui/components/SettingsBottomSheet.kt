package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalNovaColors

data class ScientificConstantItem(
    val symbol: String,
    val name: String,
    val valueFormatted: String,
    val unit: String
)

val SCIENTIFIC_CONSTANTS = listOf(
    ScientificConstantItem("π", "Pi (Archimedes)", "3.1415926535", "ratio"),
    ScientificConstantItem("e", "Euler's Number", "2.7182818284", "base"),
    ScientificConstantItem("c", "Speed of Light", "299,792,458", "m/s"),
    ScientificConstantItem("h", "Planck Constant", "6.62607015 × 10⁻³⁴", "J·s"),
    ScientificConstantItem("G", "Gravitational Constant", "6.67430 × 10⁻¹¹", "m³/(kg·s²)"),
    ScientificConstantItem("N_A", "Avogadro Constant", "6.02214076 × 10²³", "mol⁻¹")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    isDarkTheme: Boolean,
    hapticEnabled: Boolean,
    onToggleTheme: () -> Unit,
    onToggleHaptic: () -> Unit,
    onInsertConstant: (String) -> Unit,
    onOpenInstall: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = LocalNovaColors.current
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceElevated)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(scrollState)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = colors.neonAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.settings_title),
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close settings",
                        tint = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Appearance Section
            SettingsSectionHeader(title = stringResource(R.string.theme_title))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceElevated)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = colors.neonAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isDarkTheme) stringResource(R.string.theme_dark) else stringResource(R.string.theme_light),
                                color = colors.textPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isDarkTheme) "Matte obsidian with neon accents" else "Clean bright background",
                                color = colors.textDim,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.neonAccent,
                            checkedTrackColor = colors.neonAccent.copy(alpha = 0.35f),
                            uncheckedThumbColor = colors.textSecondary,
                            uncheckedTrackColor = colors.keypadSurface
                        ),
                        modifier = Modifier.testTag("switch_dark_theme")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Haptic Feedback Section
            SettingsSectionHeader(title = stringResource(R.string.haptics_title))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceElevated)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = colors.neonSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.haptics_title),
                                color = colors.textPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(R.string.haptics_summary),
                                color = colors.textDim,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = { onToggleHaptic() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.neonSecondary,
                            checkedTrackColor = colors.neonSecondary.copy(alpha = 0.35f),
                            uncheckedThumbColor = colors.textSecondary,
                            uncheckedTrackColor = colors.keypadSurface
                        ),
                        modifier = Modifier.testTag("switch_haptic_feedback")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Scientific Constants Reference Sheet
            SettingsSectionHeader(title = stringResource(R.string.constants_title))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (item in SCIENTIFIC_CONSTANTS) {
                    ConstantCard(
                        item = item,
                        onInsert = {
                            onInsertConstant(item.symbol)
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Install / Export App Banner
            SettingsSectionHeader(title = "INSTALL & SHARE")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.neonAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable {
                        onDismiss()
                        onOpenInstall()
                    }
                    .padding(16.dp)
                    .testTag("banner_install_app")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colors.neonAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Download,
                                contentDescription = null,
                                tint = colors.neonAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Install on Phone / Get APK",
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Download APK or share install link",
                                color = colors.textDim,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.equalsGradient)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Install",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceElevated.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Nova Calculator Pro • v1.0",
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "High-precision offline scientific instrument",
                        color = colors.textDim,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    val colors = LocalNovaColors.current
    Text(
        text = title.uppercase(),
        color = colors.textDim,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun ConstantCard(
    item: ScientificConstantItem,
    onInsert: () -> Unit
) {
    val colors = LocalNovaColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceElevated)
            .clickable { onInsert() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("constant_card_${item.symbol}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.keypadSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.symbol,
                    color = colors.neonAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = item.name,
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${item.valueFormatted} ${item.unit}",
                    color = colors.textDim,
                    fontSize = 11.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colors.neonAccent.copy(alpha = 0.15f))
                .border(1.dp, colors.neonAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Insert",
                color = colors.neonAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
