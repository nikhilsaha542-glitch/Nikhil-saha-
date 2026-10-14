package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalNovaColors

@Composable
fun ScientificPanel(
    isExpanded: Boolean,
    isMemorySet: Boolean,
    onToggleExpand: () -> Unit,
    onScientificKey: (String) -> Unit,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalNovaColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp)
    ) {
        // Expand/Collapse Bar & Quick Memory Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Memory Bar: MC, MR, M+, M-
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                KeypadButton(
                    text = "MC",
                    type = KeypadButtonType.MEMORY,
                    fontSize = 12.sp,
                    cornerRadius = 10.dp,
                    modifier = Modifier.size(width = 44.dp, height = 32.dp),
                    testTag = "btn_mc",
                    contentDescription = "Memory Clear",
                    onClick = onMemoryClear
                )
                KeypadButton(
                    text = "MR",
                    type = KeypadButtonType.MEMORY,
                    fontSize = 12.sp,
                    cornerRadius = 10.dp,
                    customTextColor = if (isMemorySet) colors.neonSecondary else null,
                    modifier = Modifier.size(width = 44.dp, height = 32.dp),
                    testTag = "btn_mr",
                    contentDescription = "Memory Recall",
                    onClick = onMemoryRecall
                )
                KeypadButton(
                    text = "M+",
                    type = KeypadButtonType.MEMORY,
                    fontSize = 12.sp,
                    cornerRadius = 10.dp,
                    modifier = Modifier.size(width = 44.dp, height = 32.dp),
                    testTag = "btn_m_plus",
                    contentDescription = "Memory Plus",
                    onClick = onMemoryAdd
                )
                KeypadButton(
                    text = "M−",
                    type = KeypadButtonType.MEMORY,
                    fontSize = 12.sp,
                    cornerRadius = 10.dp,
                    modifier = Modifier.size(width = 44.dp, height = 32.dp),
                    testTag = "btn_m_minus",
                    contentDescription = "Memory Minus",
                    onClick = onMemorySubtract
                )
            }

            // Expand/Collapse Toggle Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceElevated.copy(alpha = 0.8f))
                    .clickable { onToggleExpand() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("btn_toggle_scientific"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isExpanded) "Basic" else "Sci Mode",
                        color = colors.neonAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isExpanded) "Collapse scientific panel" else "Expand scientific panel",
                        tint = colors.neonAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Expandable Scientific Function Keypad Grid
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Row 1: sin, cos, tan, log, ln
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val row1 = listOf("sin", "cos", "tan", "log", "ln")
                    for (fn in row1) {
                        KeypadButton(
                            text = fn,
                            type = KeypadButtonType.SCIENTIFIC,
                            fontSize = 14.sp,
                            cornerRadius = 14.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            testTag = "btn_sci_$fn",
                            contentDescription = "$fn function",
                            onClick = { onScientificKey(fn) }
                        )
                    }
                }

                // Row 2: √, x², xʸ, π, e
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val row2 = listOf("√", "x²", "xʸ", "π", "e")
                    for (fn in row2) {
                        KeypadButton(
                            text = fn,
                            type = KeypadButtonType.SCIENTIFIC,
                            fontSize = 15.sp,
                            cornerRadius = 14.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            testTag = "btn_sci_$fn",
                            contentDescription = "$fn key",
                            onClick = { onScientificKey(fn) }
                        )
                    }
                }

                // Row 3: 1/x, n!, (, ), EXP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val row3 = listOf(
                        Triple("1/x", "1/x", "1/x key"),
                        Triple("n!", "!", "Factorial key"),
                        Triple("(", "(", "Open parenthesis"),
                        Triple(")", ")", "Close parenthesis"),
                        Triple("EXP", "EXP", "Exponential notation")
                    )
                    for ((display, key, desc) in row3) {
                        KeypadButton(
                            text = display,
                            type = KeypadButtonType.SCIENTIFIC,
                            fontSize = 14.sp,
                            cornerRadius = 14.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            testTag = "btn_sci_$display",
                            contentDescription = desc,
                            onClick = { onScientificKey(key) }
                        )
                    }
                }
            }
        }
    }
}
