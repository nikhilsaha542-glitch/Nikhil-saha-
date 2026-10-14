package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.CalculatorTopBar
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.NumericKeypad
import com.example.ui.components.ScientificPanel
import com.example.ui.components.SettingsBottomSheet
import com.example.ui.theme.LocalNovaColors
import com.example.ui.viewmodel.CalculatorEvent
import com.example.ui.viewmodel.CalculatorViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val colors = LocalNovaColors.current
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showHistorySheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showInstallDialog by remember { mutableStateOf(false) }

    // Haptic feedback provider
    val vibrator = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    // Collect events (haptics, clipboard, toasts)
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CalculatorEvent.TriggerHaptic -> {
                    if (uiState.hapticEnabled) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
                                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                            } else {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            }
                        } catch (e: Exception) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        }
                    }
                }
                is CalculatorEvent.TriggerEqualsEffect -> {
                    if (uiState.hapticEnabled) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
                                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                            } else {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            }
                        } catch (e: Exception) {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        }
                    }
                }
                is CalculatorEvent.CopyToClipboard -> {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Nova Calculator Result", event.text)
                    clipboard.setPrimaryClip(clip)
                }
                is CalculatorEvent.ShowToast -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp) // Responsive constraint for large screens/tablets
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top App Bar
                CalculatorTopBar(
                    angleMode = uiState.angleMode,
                    isMemorySet = uiState.isMemorySet,
                    onToggleAngleMode = { viewModel.onToggleAngleMode() },
                    onOpenHistory = { showHistorySheet = true },
                    onOpenSettings = { showSettingsSheet = true },
                    onOpenInstall = { showInstallDialog = true }
                )

                // Large Display (Expression & Result), weight(1f) to flexibly adapt across phone sizes
                CalculatorDisplay(
                    uiState = uiState,
                    onCopyResult = { viewModel.onCopyResult() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                // Scientific Keypad Panel (Collapsible / Expandable)
                ScientificPanel(
                    isExpanded = uiState.isScientificExpanded,
                    isMemorySet = uiState.isMemorySet,
                    onToggleExpand = { viewModel.onToggleScientificExpanded() },
                    onScientificKey = { key -> viewModel.onScientificFunction(key) },
                    onMemoryClear = { viewModel.onMemoryClear() },
                    onMemoryRecall = { viewModel.onMemoryRecall() },
                    onMemoryAdd = { viewModel.onMemoryAdd() },
                    onMemorySubtract = { viewModel.onMemorySubtract() }
                )

                // Main Numeric & Operator Keypad
                NumericKeypad(
                    onNumber = { num -> viewModel.onNumber(num) },
                    onOperator = { op -> viewModel.onOperator(op) },
                    onDecimal = { viewModel.onDecimal() },
                    onEquals = { viewModel.onEquals() },
                    onClear = { viewModel.onClear() },
                    onBackspace = { viewModel.onBackspace() },
                    onToggleSign = { viewModel.onToggleSign() }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    // History Bottom Sheet
    if (showHistorySheet) {
        HistoryBottomSheet(
            historyList = historyList,
            onDismiss = { showHistorySheet = false },
            onSelectExpression = { item -> viewModel.onHistoryItemSelect(item, useResult = false) },
            onSelectResult = { item -> viewModel.onHistoryItemSelect(item, useResult = true) },
            onDeleteItem = { item -> viewModel.onDeleteHistoryItem(item) },
            onClearAll = { viewModel.onClearAllHistory() }
        )
    }

    // Settings Bottom Sheet
    if (showSettingsSheet) {
        SettingsBottomSheet(
            isDarkTheme = uiState.isDarkTheme,
            hapticEnabled = uiState.hapticEnabled,
            onToggleTheme = { viewModel.onToggleTheme() },
            onToggleHaptic = { viewModel.onToggleHaptic() },
            onInsertConstant = { constant -> viewModel.insertConstant(constant) },
            onOpenInstall = { showInstallDialog = true },
            onDismiss = { showSettingsSheet = false }
        )
    }

    // Install Dialog
    if (showInstallDialog) {
        com.example.ui.components.InstallDialog(
            onDismiss = { showInstallDialog = false }
        )
    }
}
