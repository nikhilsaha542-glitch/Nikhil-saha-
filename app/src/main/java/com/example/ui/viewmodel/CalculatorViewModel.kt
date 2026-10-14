package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.HistoryEntity
import com.example.data.repository.HistoryRepository
import com.example.domain.engine.AngleMode
import com.example.domain.engine.CalculationEngine
import com.example.domain.engine.CalculationResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val liveResult: String? = null,
    val finalResult: String? = null,
    val isEvaluated: Boolean = false,
    val angleMode: AngleMode = AngleMode.DEG,
    val memoryValue: Double = 0.0,
    val isScientificExpanded: Boolean = true,
    val isDarkTheme: Boolean = true,
    val hapticEnabled: Boolean = true,
    val errorMessage: String? = null
) {
    val isMemorySet: Boolean get() = memoryValue != 0.0
}

sealed class CalculatorEvent {
    data class ShowToast(val message: String) : CalculatorEvent()
    data class CopyToClipboard(val text: String) : CalculatorEvent()
    object TriggerHaptic : CalculatorEvent()
    object TriggerEqualsEffect : CalculatorEvent()
}

class CalculatorViewModel(
    private val historyRepository: HistoryRepository,
    private val calculationEngine: CalculationEngine = CalculationEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CalculatorEvent>()
    val events: SharedFlow<CalculatorEvent> = _events.asSharedFlow()

    val historyList: StateFlow<List<HistoryEntity>> = historyRepository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onNumber(digit: String) {
        emitHaptic()
        _uiState.update { state ->
            val newExpr = if (state.isEvaluated) {
                // If user just pressed equals and enters a new number, start a fresh expression
                digit
            } else {
                state.expression + digit
            }
            state.copy(
                expression = newExpr,
                isEvaluated = false,
                errorMessage = null,
                liveResult = calculationEngine.evaluateLivePreview(newExpr, state.angleMode)
            )
        }
    }

    fun onDecimal() {
        emitHaptic()
        _uiState.update { state ->
            val expr = if (state.isEvaluated) "0." else state.expression
            val tokens = expr.split("+", "−", "-", "×", "*", "÷", "/", "(", "^")
            val currentNumber = tokens.lastOrNull() ?: ""

            if (currentNumber.contains(".")) {
                state // already has a decimal in current number segment
            } else {
                val updated = if (expr.isEmpty() || expr.last() in listOf('+', '−', '-', '×', '*', '÷', '/', '(', '^')) {
                    expr + "0."
                } else {
                    expr + "."
                }
                state.copy(
                    expression = updated,
                    isEvaluated = false,
                    errorMessage = null,
                    liveResult = calculationEngine.evaluateLivePreview(updated, state.angleMode)
                )
            }
        }
    }

    fun onOperator(op: String) {
        emitHaptic()
        _uiState.update { state ->
            val currentExpr = if (state.isEvaluated && state.finalResult != null && state.errorMessage == null) {
                // Continue from final result
                state.finalResult
            } else {
                state.expression
            }

            if (currentExpr.isEmpty()) {
                if (op == "−" || op == "-") {
                    return@update state.copy(expression = "−", isEvaluated = false)
                }
                return@update state
            }

            val lastChar = currentExpr.last()
            val operators = listOf('+', '−', '-', '×', '*', '÷', '/', '^', '%')

            val updated = if (lastChar in operators) {
                // Replace previous operator with new one
                currentExpr.dropLast(1) + op
            } else {
                currentExpr + op
            }

            state.copy(
                expression = updated,
                isEvaluated = false,
                errorMessage = null,
                liveResult = calculationEngine.evaluateLivePreview(updated, state.angleMode)
            )
        }
    }

    fun onScientificFunction(func: String) {
        emitHaptic()
        _uiState.update { state ->
            val baseExpr = if (state.isEvaluated) "" else state.expression

            val insertion = when (func) {
                "sin", "cos", "tan", "asin", "acos", "atan", "log", "ln", "abs" -> "$func("
                "√" -> "√("
                "x²" -> "^2"
                "xʸ" -> "^"
                "1/x" -> "1/("
                "!" -> "!"
                "π" -> "π"
                "e" -> "e"
                "EXP" -> "E"
                "(" -> "("
                ")" -> ")"
                else -> func
            }

            val updated = baseExpr + insertion
            state.copy(
                expression = updated,
                isEvaluated = false,
                errorMessage = null,
                liveResult = calculationEngine.evaluateLivePreview(updated, state.angleMode)
            )
        }
    }

    fun onToggleSign() {
        emitHaptic()
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isEmpty()) {
                return@update state.copy(expression = "−")
            }

            // Find last number or token
            val operators = charArrayOf('+', '−', '-', '×', '*', '÷', '/', '(')
            val lastOpIndex = expr.lastIndexOfAny(operators)

            val updated = if (lastOpIndex == -1) {
                // Whole expression is a number
                if (expr.startsWith("−") || expr.startsWith("-")) {
                    expr.substring(1)
                } else {
                    "−$expr"
                }
            } else {
                val prefix = expr.substring(0, lastOpIndex + 1)
                val target = expr.substring(lastOpIndex + 1)
                if (target.startsWith("−") || target.startsWith("-")) {
                    prefix + target.substring(1)
                } else {
                    prefix + "−" + target
                }
            }

            state.copy(
                expression = updated,
                isEvaluated = false,
                liveResult = calculationEngine.evaluateLivePreview(updated, state.angleMode)
            )
        }
    }

    fun onParenthesis() {
        emitHaptic()
        _uiState.update { state ->
            val expr = state.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val shouldClose = openCount > closeCount &&
                    expr.isNotEmpty() &&
                    (expr.last().isDigit() || expr.last() in listOf(')', 'π', 'e', '!'))

            val addition = if (shouldClose) ")" else "("
            val updated = expr + addition
            state.copy(
                expression = updated,
                isEvaluated = false,
                liveResult = calculationEngine.evaluateLivePreview(updated, state.angleMode)
            )
        }
    }

    fun onEquals() {
        emitHaptic()
        viewModelScope.launch {
            _events.emit(CalculatorEvent.TriggerEqualsEffect)
        }

        val state = _uiState.value
        val expr = state.expression.trim()
        if (expr.isEmpty()) return

        when (val result = calculationEngine.evaluate(expr, state.angleMode)) {
            is CalculationResult.Success -> {
                _uiState.update {
                    it.copy(
                        finalResult = result.formatted,
                        liveResult = null,
                        isEvaluated = true,
                        errorMessage = null
                    )
                }
                // Save to history in Room DB
                viewModelScope.launch {
                    historyRepository.addCalculation(
                        expression = expr,
                        result = result.formatted,
                        angleMode = state.angleMode.name
                    )
                }
            }
            is CalculationResult.Error -> {
                _uiState.update {
                    it.copy(
                        errorMessage = result.message,
                        isEvaluated = true
                    )
                }
            }
            CalculationResult.Empty -> Unit
        }
    }

    fun onClear() {
        emitHaptic()
        _uiState.update {
            it.copy(
                expression = "",
                liveResult = null,
                finalResult = null,
                isEvaluated = false,
                errorMessage = null
            )
        }
    }

    fun onBackspace() {
        emitHaptic()
        _uiState.update { state ->
            if (state.expression.isEmpty()) {
                return@update state.copy(finalResult = null, isEvaluated = false)
            }

            // Check if removing multi-char function tags like "sin(", "asin(", "log(" etc.
            val expr = state.expression
            val multiCharTags = listOf("asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "log(", "ln(", "abs(", "√(")
            val matchedTag = multiCharTags.firstOrNull { expr.endsWith(it) }

            val newExpr = if (matchedTag != null) {
                expr.dropLast(matchedTag.length)
            } else {
                expr.dropLast(1)
            }

            state.copy(
                expression = newExpr,
                isEvaluated = false,
                errorMessage = null,
                liveResult = calculationEngine.evaluateLivePreview(newExpr, state.angleMode)
            )
        }
    }

    fun onToggleAngleMode() {
        emitHaptic()
        _uiState.update { state ->
            val nextMode = if (state.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
            val updatedLive = calculationEngine.evaluateLivePreview(state.expression, nextMode)
            state.copy(
                angleMode = nextMode,
                liveResult = updatedLive
            )
        }
    }

    fun onToggleScientificExpanded() {
        emitHaptic()
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun onToggleTheme() {
        emitHaptic()
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun onToggleHaptic() {
        _uiState.update { it.copy(hapticEnabled = !it.hapticEnabled) }
    }

    // Memory operations: MC, MR, M+, M-
    fun onMemoryClear() {
        emitHaptic()
        _uiState.update { it.copy(memoryValue = 0.0) }
        postToast("Memory Cleared")
    }

    fun onMemoryRecall() {
        emitHaptic()
        val mem = _uiState.value.memoryValue
        val memStr = calculationEngine.formatResult(mem)
        _uiState.update { state ->
            val updated = state.expression + memStr
            state.copy(
                expression = updated,
                isEvaluated = false,
                liveResult = calculationEngine.evaluateLivePreview(updated, state.angleMode)
            )
        }
    }

    fun onMemoryAdd() {
        emitHaptic()
        val currVal = getCurrentNumericValue()
        if (currVal != null) {
            _uiState.update { it.copy(memoryValue = it.memoryValue + currVal) }
            postToast("Added to Memory (${calculationEngine.formatResult(_uiState.value.memoryValue)})")
        }
    }

    fun onMemorySubtract() {
        emitHaptic()
        val currVal = getCurrentNumericValue()
        if (currVal != null) {
            _uiState.update { it.copy(memoryValue = it.memoryValue - currVal) }
            postToast("Subtracted from Memory (${calculationEngine.formatResult(_uiState.value.memoryValue)})")
        }
    }

    private fun getCurrentNumericValue(): Double? {
        val state = _uiState.value
        if (state.finalResult != null && state.errorMessage == null) {
            return state.finalResult.toDoubleOrNull()
        }
        if (state.liveResult != null) {
            return state.liveResult.toDoubleOrNull()
        }
        val evaluated = calculationEngine.evaluate(state.expression, state.angleMode)
        return if (evaluated is CalculationResult.Success) evaluated.value else null
    }

    fun onCopyResult() {
        emitHaptic()
        val state = _uiState.value
        val textToCopy = state.finalResult ?: state.liveResult ?: state.expression
        if (textToCopy.isNotBlank()) {
            viewModelScope.launch {
                _events.emit(CalculatorEvent.CopyToClipboard(textToCopy))
                _events.emit(CalculatorEvent.ShowToast("Copied \"$textToCopy\" to clipboard"))
            }
        }
    }

    fun onHistoryItemSelect(item: HistoryEntity, useResult: Boolean) {
        emitHaptic()
        val text = if (useResult) item.result else item.expression
        _uiState.update { state ->
            state.copy(
                expression = text,
                isEvaluated = false,
                errorMessage = null,
                liveResult = calculationEngine.evaluateLivePreview(text, state.angleMode)
            )
        }
    }

    fun onDeleteHistoryItem(item: HistoryEntity) {
        viewModelScope.launch {
            historyRepository.deleteItem(item)
        }
    }

    fun onClearAllHistory() {
        emitHaptic()
        viewModelScope.launch {
            historyRepository.clearHistory()
            postToast("Calculation history cleared")
        }
    }

    fun insertConstant(symbol: String) {
        emitHaptic()
        _uiState.update { state ->
            val updated = state.expression + symbol
            state.copy(
                expression = updated,
                isEvaluated = false,
                liveResult = calculationEngine.evaluateLivePreview(updated, state.angleMode)
            )
        }
    }

    private fun emitHaptic() {
        if (_uiState.value.hapticEnabled) {
            viewModelScope.launch {
                _events.emit(CalculatorEvent.TriggerHaptic)
            }
        }
    }

    private fun postToast(msg: String) {
        viewModelScope.launch {
            _events.emit(CalculatorEvent.ShowToast(msg))
        }
    }
}

class CalculatorViewModelFactory(
    private val historyRepository: HistoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            return CalculatorViewModel(historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
