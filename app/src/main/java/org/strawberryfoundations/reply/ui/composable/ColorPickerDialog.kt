package org.strawberryfoundations.reply.ui.composable

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.ui.theme.colorToHex
import org.strawberryfoundations.reply.ui.theme.hexToColor


@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var isVisible by remember { mutableStateOf(false) }
    var color by remember { mutableStateOf(initialColor) }
    var hexInput by remember(color) { mutableStateOf(colorToHex(color)) }
    var hexError by remember { mutableStateOf(false) }

    val materialYouColors = remember(colorScheme) {
        listOf(
            colorScheme.primary, colorScheme.secondary, colorScheme.tertiary,
            colorScheme.error, colorScheme.surfaceVariant, colorScheme.primaryContainer,
            colorScheme.secondaryContainer, colorScheme.outline, colorScheme.inversePrimary
        ).distinct()
    }

    LaunchedEffect(Unit) { isVisible = true }

    val springSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.85f,
        animationSpec = springSpec,
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(250),
        label = "alpha",
        finishedListener = { if (!isVisible) onDismiss() }
    )

    val closeWithAnimation = { isVisible = false }

    Dialog(
        onDismissRequest = closeWithAnimation,
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.select_color),
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(color, shape = CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {

                }

                Spacer(Modifier.height(20.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 5
                ) {
                    materialYouColors.forEach { preset ->
                        key(preset) {
                            val isSelected = color == preset
                            Surface(
                                shape = CircleShape,
                                color = preset,
                                border = if (isSelected) BorderStroke(3.dp, MaterialTheme.colorScheme.onSurface) else null,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable {
                                        color = preset
                                        hexError = false
                                    }
                            ) {}
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { newHex ->
                        hexInput = newHex
                        if (newHex.matches(Regex("#[0-9A-Fa-f]{6}"))) {
                            runCatching { hexToColor(newHex) }.getOrNull()?.let {
                                color = it
                                hexError = false
                            } ?: run { hexError = true }
                        } else { hexError = true }
                    },
                    label = { Text("HEX") },
                    isError = hexError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(150.dp)
                )

                Spacer(Modifier.height(16.dp))

                ColorSlider(stringResource(R.string.red), color.red, Color.Red) { color = color.copy(red = it) }
                ColorSlider(stringResource(R.string.green), color.green, Color.Green) { color = color.copy(green = it) }
                ColorSlider(stringResource(R.string.blue), color.blue, Color.Blue) { color = color.copy(blue = it) }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = closeWithAnimation) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        enabled = !hexError,
                        onClick = {
                            onColorSelected(color)
                            closeWithAnimation()
                        }
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSlider(label: String, value: Float, trackColor: Color, onValueChange: (Float) -> Unit) {
    Column {
        Text(
            text = "$label: ${(value * 255).toInt()}",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = trackColor,
                activeTrackColor = trackColor.copy(alpha = 0.5f)
            )
        )
    }
}