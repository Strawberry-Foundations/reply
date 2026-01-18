package org.strawberryfoundations.reply.ui.composable

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.room.entities.Exercise
import org.strawberryfoundations.reply.room.entities.ExerciseGroup
import org.strawberryfoundations.reply.room.entities.getExerciseGroupEmoji
import org.strawberryfoundations.reply.room.entities.getExerciseGroupStringResource
import org.strawberryfoundations.reply.ui.theme.colorToHex
import org.strawberryfoundations.reply.ui.theme.hexToColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseDialog(
    exercise: Exercise,
    onSave: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(exercise.name) }
    var altName by remember { mutableStateOf(exercise.altName ?: "") }
    var note by remember { mutableStateOf(exercise.note) }
    var weight by remember { mutableStateOf(exercise.weight) }
    var selectedGroup by remember { mutableStateOf(exercise.group) }
    var selectedColor by remember { mutableStateOf(exercise.color) }
    var showColorPicker by remember { mutableStateOf(false) }
    var shouldSave by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 1000.dp,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessLow
        ),
        label = "slideAnimation"
    )

    val closeWithAnimation = {
        isVisible = false
    }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            kotlinx.coroutines.delay(450)
            if (shouldSave) {
                onSave(
                    exercise.copy(
                        name = name,
                        altName = altName,
                        note = note,
                        weight = weight,
                        group = selectedGroup,
                        color = selectedColor
                    )
                )
            }
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = closeWithAnimation,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = offsetY),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            EditExerciseContent(
                name = name,
                altName = altName,
                note = note,
                weight = weight,
                selectedGroup = selectedGroup,
                selectedColor = selectedColor,
                onNameChange = { name = it },
                onAltNameChange = { altName = it },
                onNoteChange = { note = it },
                onWeightChange = { weight = it },
                onGroupChange = { selectedGroup = it },
                onColorClick = { showColorPicker = true },
                onSave = {
                    shouldSave = true
                    closeWithAnimation()
                },
                onDismiss = closeWithAnimation
            )
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = hexToColor(selectedColor),
            onColorSelected = { color ->
                selectedColor = colorToHex(color)
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseContent(
    name: String,
    altName: String,
    note: String,
    weight: Double?,
    selectedGroup: ExerciseGroup,
    selectedColor: String,
    onNameChange: (String) -> Unit,
    onAltNameChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onWeightChange: (Double?) -> Unit,
    onGroupChange: (ExerciseGroup) -> Unit,
    onColorClick: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.edit_exercise)) },
            navigationIcon = {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.cancel))
                }
            },
            actions = {
                IconButton(onClick = onSave) {
                    Icon(Icons.Rounded.Save, contentDescription = stringResource(R.string.save))
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = altName,
                onValueChange = onAltNameChange,
                label = { Text(stringResource(R.string.alt_name)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Label, null, modifier = Modifier.size(18.dp)) }
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.group),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExerciseGroup.entries.forEach { group ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = if (selectedGroup == group)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                            onClick = { onGroupChange(group) }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = getExerciseGroupEmoji(group),
                                    fontSize = 28.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = getExerciseGroupStringResource(group),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.color),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    onClick = onColorClick
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = hexToColor(selectedColor)
                        ) {}

                        Text(
                            text = stringResource(R.string.select_color),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = weight?.toString() ?: "",
                    onValueChange = { onWeightChange(it.toDoubleOrNull() ?: weight) },
                    label = { Text(stringResource(R.string.weight_kg)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(2.5, 5.0, 10.0).forEach { amount ->
                        InputChip(
                            selected = false,
                            onClick = { onWeightChange(weight?.plus(amount)) },
                            label = { Text("+$amount") },
                            colors = InputChipDefaults.inputChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text(stringResource(R.string.note)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}