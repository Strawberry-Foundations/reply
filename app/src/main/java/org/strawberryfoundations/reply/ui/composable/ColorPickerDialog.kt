package org.strawberryfoundations.reply.ui.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

    val materialYouColors = remember(colorScheme) {
        listOf(
            colorScheme.primary,
            colorScheme.secondary,
            colorScheme.tertiary,
            colorScheme.error,
            colorScheme.surface,
            colorScheme.background,
            colorScheme.primaryContainer,
            colorScheme.secondaryContainer,
            colorScheme.tertiaryContainer,
            colorScheme.outline,
            colorScheme.inversePrimary,
        ).distinct()
    }

    var color by remember { mutableStateOf(initialColor) }
    var hexInput by remember(color) { mutableStateOf(colorToHex(color)) }
    var hexError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = { Text(stringResource(R.string.select_color), style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(60.dp)
                        .background(color, shape = CircleShape)
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    materialYouColors.forEach { preset ->
                        key(preset) {
                            val isSelected = color == preset
                            val onClickPreset = remember(preset) {
                                {
                                    color = preset
                                    hexError = false
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = preset,
                                border = if (isSelected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
                                shadowElevation = if (isSelected) 6.dp else 2.dp,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable(onClick = onClickPreset)
                            ) {}
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { newHex ->
                        hexInput = newHex
                        if (newHex.matches(Regex("#[0-9A-Fa-f]{6}", RegexOption.IGNORE_CASE))) {
                            runCatching { hexToColor(newHex) }.getOrNull()?.let { parsedColor ->
                                color = parsedColor
                                hexError = false
                            } ?: run { hexError = true }
                        } else {
                            hexError = true
                        }
                    },
                    label = { Text("HEX") },
                    isError = hexError,
                    singleLine = true,
                    modifier = Modifier.width(120.dp),
                    supportingText = {
                        if (hexError) Text(stringResource(R.string.invalid), color = MaterialTheme.colorScheme.error)
                    }
                )
                Spacer(Modifier.height(10.dp))
                Text("${stringResource(R.string.red)}: ${(color.red * 255).toInt()}")
                Slider(
                    value = color.red,
                    onValueChange = { color = color.copy(red = it); hexError = false },
                    colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red)
                )
                Text("${stringResource(R.string.green)}: ${(color.green * 255).toInt()}")
                Slider(
                    value = color.green,
                    onValueChange = { color = color.copy(green = it); hexError = false },
                    colors = SliderDefaults.colors(thumbColor = Color.Green, activeTrackColor = Color.Green)
                )
                Text("${stringResource(R.string.blue)}: ${(color.blue * 255).toInt()}")
                Slider(
                    value = color.blue,
                    onValueChange = { color = color.copy(blue = it); hexError = false },
                    colors = SliderDefaults.colors(thumbColor = Color.Blue, activeTrackColor = Color.Blue)
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !hexError,
                onClick = { onColorSelected(color) }
            ) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}