package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.AngleMode
import com.example.ui.theme.LocalNovaColors
import com.example.ui.viewmodel.CalculatorUiState
import kotlinx.coroutines.delay

@Composable
fun CalculatorTopBar(
    angleMode: AngleMode,
    isMemorySet: Boolean,
    onToggleAngleMode: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenInstall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalNovaColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Identity with Neon Logo
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceElevated)
                    .border(1.5.dp, colors.neonAccent.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "N",
                    color = colors.neonAccent,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "NOVA",
                    color = colors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "PRO CALCULATOR",
                    color = colors.neonAccent,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Actions: DEG/RAD toggle, Memory pill, History, Install, Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Memory Indicator Badge
            if (isMemorySet) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.neonSecondary.copy(alpha = 0.2f))
                        .border(1.dp, colors.neonSecondary, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("indicator_memory"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = colors.neonSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // DEG/RAD Toggle Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.neonAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable { onToggleAngleMode() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("toggle_angle_mode"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (angleMode == AngleMode.DEG) "DEG" else "RAD",
                    color = colors.neonAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Install App Button
            IconButton(
                onClick = onOpenInstall,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colors.neonAccent.copy(alpha = 0.15f))
                    .border(1.dp, colors.neonAccent.copy(alpha = 0.5f), CircleShape)
                    .testTag("btn_install_app")
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Download,
                    contentDescription = "Install App",
                    tint = colors.neonAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            // History Button
            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceElevated.copy(alpha = 0.7f))
                    .testTag("btn_history")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Calculation History",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Settings Button
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceElevated.copy(alpha = 0.7f))
                    .testTag("btn_settings")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CalculatorDisplay(
    uiState: CalculatorUiState,
    onCopyResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalNovaColors.current
    val exprScrollState = rememberScrollState()
    var justCopied by remember { mutableStateOf(false) }

    // Scroll expression to end on change
    LaunchedEffect(uiState.expression) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Small Expression Text Above Result
        val displayedExpr = if (uiState.expression.isEmpty()) "0" else uiState.expression
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(exprScrollState),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayedExpr,
                color = if (uiState.expression.isEmpty()) colors.textDim else colors.textSecondary,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.testTag("display_expression")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Large Result Display or Error
        val (resultText, resultColor, fontSize) = when {
            uiState.errorMessage != null -> {
                Triple(uiState.errorMessage, Color(0xFFFF5252), 32.sp)
            }
            uiState.finalResult != null -> {
                val len = uiState.finalResult.length
                val size = if (len > 12) 34.sp else if (len > 8) 42.sp else 50.sp
                Triple(uiState.finalResult, colors.textPrimary, size)
            }
            uiState.liveResult != null -> {
                val len = uiState.liveResult.length
                val size = if (len > 12) 32.sp else if (len > 8) 40.sp else 46.sp
                Triple("= ${uiState.liveResult}", colors.neonAccent.copy(alpha = 0.85f), size)
            }
            else -> {
                Triple("", colors.textDim, 46.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Copy Result Button
            val hasContentToCopy = (uiState.finalResult != null || uiState.liveResult != null) && uiState.errorMessage == null
            if (hasContentToCopy) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceElevated.copy(alpha = 0.6f))
                        .border(1.dp, colors.surfaceElevated, RoundedCornerShape(12.dp))
                        .clickable {
                            onCopyResult()
                            justCopied = true
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("btn_copy_result"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (justCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy Result",
                            tint = if (justCopied) colors.neonAccent else colors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (justCopied) "Copied" else "Copy",
                            color = if (justCopied) colors.neonAccent else colors.textSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            LaunchedEffect(justCopied) {
                if (justCopied) {
                    delay(1500)
                    justCopied = false
                }
            }

            // Main Answer Text
            val scrollStateResult = rememberScrollState()
            Row(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .horizontalScroll(scrollStateResult),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = resultText,
                    color = resultColor,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.testTag("display_result")
                )
            }
        }
    }
}
