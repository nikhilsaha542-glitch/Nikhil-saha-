package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalNovaColors

enum class KeypadButtonType {
    NUMBER,
    OPERATOR,
    ACTION,
    SCIENTIFIC,
    EQUALS,
    MEMORY
}

@Composable
fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    type: KeypadButtonType = KeypadButtonType.NUMBER,
    fontSize: TextUnit = 22.sp,
    cornerRadius: Dp = 20.dp,
    customColor: Color? = null,
    customTextColor: Color? = null,
    testTag: String = "btn_$text",
    contentDescription: String = text,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val colors = LocalNovaColors.current
    var isPressedState by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressedState) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "button_scale"
    )

    val shape = RoundedCornerShape(cornerRadius)

    val (bgModifier, textColor) = when (type) {
        KeypadButtonType.EQUALS -> {
            Pair(
                Modifier
                    .background(colors.equalsGradient, shape)
                    .shadow(elevation = 8.dp, shape = shape, spotColor = colors.neonAccent),
                Color.White
            )
        }
        KeypadButtonType.OPERATOR -> {
            Pair(
                Modifier
                    .background(customColor ?: colors.actionKey, shape)
                    .border(1.dp, colors.neonAccent.copy(alpha = 0.35f), shape),
                customTextColor ?: colors.operatorKeyText
            )
        }
        KeypadButtonType.ACTION -> {
            Pair(
                Modifier
                    .background(customColor ?: colors.actionKey, shape)
                    .border(1.dp, colors.actionKeyText.copy(alpha = 0.3f), shape),
                customTextColor ?: colors.actionKeyText
            )
        }
        KeypadButtonType.SCIENTIFIC -> {
            Pair(
                Modifier
                    .background(customColor ?: colors.scientificKey, shape)
                    .border(1.dp, colors.neonAccent.copy(alpha = 0.18f), shape),
                customTextColor ?: colors.scientificKeyText
            )
        }
        KeypadButtonType.MEMORY -> {
            Pair(
                Modifier
                    .background(customColor ?: colors.surfaceElevated, shape)
                    .border(1.dp, colors.textDim.copy(alpha = 0.2f), shape),
                customTextColor ?: colors.textSecondary
            )
        }
        KeypadButtonType.NUMBER -> {
            Pair(
                Modifier
                    .background(customColor ?: colors.numberKey, shape)
                    .border(1.dp, colors.surfaceElevated.copy(alpha = 0.4f), shape),
                customTextColor ?: colors.numberKeyText
            )
        }
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .scale(scale)
            .clip(shape)
            .then(bgModifier)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressedState = true
                        tryAwaitRelease()
                        isPressedState = false
                    },
                    onTap = { onClick() },
                    onLongPress = {
                        onLongClick?.invoke() ?: onClick()
                    }
                )
            }
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Button
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (type == KeypadButtonType.EQUALS || type == KeypadButtonType.OPERATOR) FontWeight.Bold else FontWeight.Medium
        )
    }
}
