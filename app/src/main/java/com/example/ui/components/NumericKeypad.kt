package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonPink

@Composable
fun NumericKeypad(
    onNumber: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onEquals: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onToggleSign: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: AC, ⌫, %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                text = "AC",
                type = KeypadButtonType.ACTION,
                fontSize = 20.sp,
                customTextColor = NeonPink,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_ac",
                contentDescription = "All Clear",
                onClick = onClear
            )
            KeypadButton(
                text = "⌫",
                type = KeypadButtonType.ACTION,
                fontSize = 22.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_backspace",
                contentDescription = "Delete character, long press to clear all",
                onLongClick = onClear,
                onClick = onBackspace
            )
            KeypadButton(
                text = "%",
                type = KeypadButtonType.OPERATOR,
                fontSize = 22.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_percent",
                contentDescription = "Percentage",
                onClick = { onOperator("%") }
            )
            KeypadButton(
                text = "÷",
                type = KeypadButtonType.OPERATOR,
                fontSize = 26.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_divide",
                contentDescription = "Divide",
                onClick = { onOperator("÷") }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                text = "7",
                type = KeypadButtonType.NUMBER,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_7",
                contentDescription = "Number 7",
                onClick = { onNumber("7") }
            )
            KeypadButton(
                text = "8",
                type = KeypadButtonType.NUMBER,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_8",
                contentDescription = "Number 8",
                onClick = { onNumber("8") }
            )
            KeypadButton(
                text = "9",
                type = KeypadButtonType.NUMBER,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_9",
                contentDescription = "Number 9",
                onClick = { onNumber("9") }
            )
            KeypadButton(
                text = "×",
                type = KeypadButtonType.OPERATOR,
                fontSize = 26.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_multiply",
                contentDescription = "Multiply",
                onClick = { onOperator("×") }
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                text = "4",
                type = KeypadButtonType.NUMBER,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_4",
                contentDescription = "Number 4",
                onClick = { onNumber("4") }
            )
            KeypadButton(
                text = "5",
                type = KeypadButtonType.NUMBER,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_5",
                contentDescription = "Number 5",
                onClick = { onNumber("5") }
            )
            KeypadButton(
                text = "6",
                type = KeypadButtonType.NUMBER,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_6",
                contentDescription = "Number 6",
                onClick = { onNumber("6") }
            )
            KeypadButton(
                text = "−",
                type = KeypadButtonType.OPERATOR,
                fontSize = 26.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_minus",
                contentDescription = "Subtract",
                onClick = { onOperator("−") }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                text = "1",
                type = KeypadButtonType.NUMBER,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_1",
                contentDescription = "Number 1",
                onClick = { onNumber("1") }
            )
            KeypadButton(
                text = "2",
                type = KeypadButtonType.NUMBER,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_2",
                contentDescription = "Number 2",
                onClick = { onNumber("2") }
            )
            KeypadButton(
                text = "3",
                type = KeypadButtonType.NUMBER,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_3",
                contentDescription = "Number 3",
                onClick = { onNumber("3") }
            )
            KeypadButton(
                text = "+",
                type = KeypadButtonType.OPERATOR,
                fontSize = 26.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_plus",
                contentDescription = "Add",
                onClick = { onOperator("+") }
            )
        }

        // Row 5: ±, 0, ., = (Highlighted with neon gradient)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                text = "±",
                type = KeypadButtonType.NUMBER,
                fontSize = 22.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_plus_minus",
                contentDescription = "Plus minus toggle",
                onClick = onToggleSign
            )
            KeypadButton(
                text = "0",
                type = KeypadButtonType.NUMBER,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_0",
                contentDescription = "Number 0",
                onClick = { onNumber("0") }
            )
            KeypadButton(
                text = ".",
                type = KeypadButtonType.NUMBER,
                fontSize = 26.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_decimal",
                contentDescription = "Decimal point",
                onClick = onDecimal
            )
            KeypadButton(
                text = "=",
                type = KeypadButtonType.EQUALS,
                fontSize = 28.sp,
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                testTag = "btn_equals",
                contentDescription = "Calculate equals",
                onClick = onEquals
            )
        }
    }
}
