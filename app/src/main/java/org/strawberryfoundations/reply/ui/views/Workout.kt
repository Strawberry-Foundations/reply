package org.strawberryfoundations.reply.ui.views

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.default.Check
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.core.AppSettings
import org.strawberryfoundations.reply.room.entities.Exercise
import org.strawberryfoundations.reply.room.entities.ExerciseGroup
import org.strawberryfoundations.reply.room.entities.getExerciseGroupEmoji
import org.strawberryfoundations.reply.room.entities.getExerciseGroupStringResource
import org.strawberryfoundations.reply.room.ExerciseViewModel
import org.strawberryfoundations.reply.ui.composable.ColorPickerDialog
import org.strawberryfoundations.reply.ui.composable.DeleteExerciseDialog
import org.strawberryfoundations.reply.ui.composable.NoteEditDialog
import org.strawberryfoundations.reply.ui.theme.colorToHex
import org.strawberryfoundations.reply.ui.theme.customFont
import org.strawberryfoundations.reply.ui.theme.darkenColor
import org.strawberryfoundations.reply.ui.theme.hexToColor
import java.util.Locale


@Composable
fun rememberFormattedStep(step: Double): String {
    return remember(step) {
        if (step.rem(1.0) == 0.0) {
            step.toInt().toString()
        } else {
            if (step == 0.625) "0.625" else String.format(Locale.US, "%.2f", step).trimEnd('0').trimEnd('.')
        }
    }
}

@SuppressLint("DefaultLocale", "UnusedMaterial3ScaffoldPaddingParameter", "FrequentlyChangingValue")
@OptIn(ExperimentalLayoutApi::class, ExperimentalAnimationApi::class, ExperimentalTextApi::class)
@Composable
fun TrainingView(
    viewModel: ExerciseViewModel = viewModel(),
    settings: AppSettings,
    onExerciseClick: (Long) -> Unit,
) {
    // Basic variable initialization
    val haptic = LocalHapticFeedback.current
    val exercises by viewModel.trainings.collectAsState()
    val exerciseGroups = remember { listOf(null) + ExerciseGroup.entries }
    var selectedGroup by remember { mutableStateOf<ExerciseGroup?>(null) }
    var expandedItemIndex by remember { mutableIntStateOf(-1) }

    val filteredExercises by remember(exercises, selectedGroup) {
        derivedStateOf {
            if (selectedGroup == null) {
                exercises.toList()
            } else {
                exercises.filter { it.group == selectedGroup }
            }
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var trainingToDelete by remember { mutableStateOf<Exercise?>(null) }

    val colorScheme = MaterialTheme.colorScheme
    val themeColorHexMap = remember(colorScheme) {
        mapOf(
            colorToHex(colorScheme.primary) to colorScheme.primary,
            colorToHex(colorScheme.secondary) to colorScheme.secondary,
            colorToHex(colorScheme.tertiary) to colorScheme.tertiary,
            colorToHex(colorScheme.error) to colorScheme.error,
            colorToHex(colorScheme.surface) to colorScheme.surface,
            colorToHex(colorScheme.background) to colorScheme.background,
            colorToHex(colorScheme.primaryContainer) to colorScheme.primaryContainer,
            colorToHex(colorScheme.secondaryContainer) to colorScheme.secondaryContainer,
            colorToHex(colorScheme.tertiaryContainer) to colorScheme.tertiaryContainer,
            colorToHex(colorScheme.outline) to colorScheme.outline,
            colorToHex(colorScheme.inversePrimary) to colorScheme.inversePrimary
        )
    }
    val defaultColorHex = remember(colorScheme.primaryContainer) {
        colorToHex(colorScheme.primaryContainer)
    }

    val listState = rememberLazyListState()

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            AnimatedVisibility(visible = expandedItemIndex == -1) {
                val strNewTrainingName = stringResource(R.string.add_training)

                // Add Training Button
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.insert(
                            Exercise(
                                name = strNewTrainingName,
                                color = defaultColorHex,
                                group = selectedGroup ?: ExerciseGroup.OTHER
                            )
                        )
                              },
                    text = {
                        Text(
                            text = stringResource(R.string.add_training),
                            style = MaterialTheme.typography.labelLarge
                        )
                           },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_training)
                        )
                           },
                    expanded = listState.firstVisibleItemIndex == 0,
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Group Filters
                itemsIndexed(exerciseGroups) { _, category ->
                    val isSelected = selectedGroup == category

                    val label = if (category == null) {
                        "${stringResource(R.string.all)} 🏋"
                    } else {
                        val emoji = getExerciseGroupEmoji(category)
                        val stringRes = getExerciseGroupStringResource(category)
                        "$stringRes $emoji"
                    }

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.06f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "scale"
                    )

                    Button(
                        onClick = {
                            if (settings.useHapticFeedback) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            selectedGroup = category
                        },
                        colors = if (isSelected) {
                            ButtonDefaults.buttonColors()
                        } else {
                            ButtonDefaults.filledTonalButtonColors()
                        },
                        shape = if (isSelected) {
                            RoundedCornerShape(16.dp)
                        } else {
                            RoundedCornerShape(24.dp)
                        },
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .sizeIn(minWidth = 44.dp, minHeight = 36.dp),
                    ) {
                        if (isSelected) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = MaterialSymbols.Default.Check,
                                    contentDescription = "selected",
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
            }

            // Exercises
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    itemsIndexed(
                        items = filteredExercises,
                        key = { _, training -> training.id }
                    ) { index, exercise ->
                        val cardColor = remember(
                            key1 = exercise.color,
                            key2 = themeColorHexMap,
                            key3 = colorScheme.surface
                        ) {
                            themeColorHexMap[exercise.color]
                                ?: if (exercise.color.isNotBlank()) hexToColor(exercise.color)
                                else colorScheme.surface
                        }
                        val borderColor = remember(cardColor) { darkenColor(cardColor, 0.70f) }
                        val textColor = remember(cardColor) {
                            if (cardColor.luminance() > 0.55f) Color.Black else Color.White
                        }

                        val isExpanded = expandedItemIndex == index
                        val cardElevation by animateDpAsState(
                            targetValue = if (isExpanded) 12.dp else 6.dp,
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMedium,
                                dampingRatio = Spring.DampingRatioLowBouncy
                            ),
                        )

                        val cardScale by animateFloatAsState(
                            targetValue = if (isExpanded) 1.015f else 1f,
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMedium,
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            ),
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .graphicsLayer { scaleX = cardScale; scaleY = cardScale }
                                .combinedClickable(
                                    onClick = { onExerciseClick(exercise.id) },
                                    onLongClick = {
                                        expandedItemIndex = if (isExpanded) -1 else index
                                        if (settings.useHapticFeedback) {
                                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                        }
                                    }
                                ),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = cardElevation,
                                pressedElevation = cardElevation * 2f
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize(
                                        animationSpec = spring(
                                            stiffness = Spring.StiffnessMedium,
                                            dampingRatio = Spring.DampingRatioNoBouncy
                                        )
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .heightIn(min = 60.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 18.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = exercise.name,
                                            color = textColor,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontSize = 16.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Surface(
                                            color = borderColor,
                                            contentColor = textColor,
                                            shape = CircleShape,
                                            shadowElevation = 2.dp
                                        ) {
                                            Text(
                                                "${exercise.weight ?: 0.0} kg",
                                                style = customFont.numeralMedium,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                // Expanded Group card
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically(animationSpec = spring(
                                        stiffness = Spring.StiffnessMedium,
                                        dampingRatio = Spring.DampingRatioNoBouncy
                                    )),
                                    exit = shrinkVertically(animationSpec = spring(
                                        stiffness = Spring.StiffnessMedium,
                                        dampingRatio = Spring.DampingRatioNoBouncy
                                    ))
                                ) {
                                    var name by remember(exercise.name) { mutableStateOf(exercise.name) }
                                    var weight by remember(exercise.weight) { mutableStateOf(exercise.weight) }
                                    var note by remember(exercise.note) { mutableStateOf(exercise.note) }
                                    var group by remember(exercise.group) { mutableStateOf(exercise.group) }
                                    var groupExpanded by remember { mutableStateOf(false) }

                                    var itemColorHex by remember(exercise.color, defaultColorHex) {
                                        mutableStateOf(exercise.color.ifBlank { defaultColorHex })
                                    }
                                    var colorDialogOpen by remember { mutableStateOf(false) }
                                    var colorPickerInitialColor by remember(itemColorHex) {
                                        mutableStateOf(hexToColor(itemColorHex))
                                    }

                                    var showNoteDialog by remember { mutableStateOf(false) }
                                    var noteInputForDialog by remember(note) { mutableStateOf(note) }

                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        if (note.isNotBlank()) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp)
                                                    .padding(top = 12.dp, bottom = 8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.Notes,
                                                    contentDescription = stringResource(R.string.note),
                                                    tint = textColor,
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clickable {
                                                            noteInputForDialog = note
                                                            showNoteDialog = true
                                                        }
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    text = note,
                                                    color = textColor.copy(alpha = 0.85f),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(
                                                            cardColor.copy(alpha = 0.93f),
                                                            borderColor.copy(alpha = 0.90f)
                                                        )
                                                    ),
                                                )
                                                .padding(12.dp)
                                        ) {
                                            Surface(
                                                color = Color.White.copy(alpha = 0.18f),
                                                shape = RoundedCornerShape(20.dp),
                                                shadowElevation = 0.dp,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(Modifier.padding(12.dp)) {
                                                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                                                        focusedContainerColor = Color.Transparent,
                                                        unfocusedContainerColor = Color.Transparent,
                                                        focusedBorderColor = textColor.copy(alpha = 0.7f),
                                                        unfocusedBorderColor = textColor.copy(alpha = 0.4f),
                                                        cursorColor = textColor,
                                                        focusedTextColor = textColor,
                                                        unfocusedTextColor = textColor,
                                                        disabledTextColor = textColor.copy(alpha = 0.7f),
                                                        focusedLabelColor = textColor.copy(alpha = 0.7f),
                                                        unfocusedLabelColor = textColor.copy(alpha = 0.4f)
                                                    )

                                                    OutlinedTextField(
                                                        value = name,
                                                        onValueChange = { name = it },
                                                        label = {
                                                            Text(
                                                                text = stringResource(R.string.name),
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                                },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = textFieldColors,
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))

                                                    Box {
                                                        OutlinedTextField(
                                                            value = "${getExerciseGroupEmoji(group)} " +
                                                                    getExerciseGroupStringResource(group),
                                                            onValueChange = {},
                                                            label = {
                                                                Text(
                                                                    text = stringResource(R.string.group),
                                                                    style = MaterialTheme.typography.bodyMedium
                                                                )
                                                                    },
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable(
                                                                    indication = null,
                                                                    interactionSource = remember { MutableInteractionSource() }
                                                                ) { groupExpanded = !groupExpanded },
                                                            readOnly = true,
                                                            trailingIcon = {
                                                                Icon(
                                                                    imageVector = Icons.Filled.ArrowDropDown,
                                                                    contentDescription = stringResource(R.string.group),
                                                                    tint = textColor
                                                                )
                                                            },
                                                            colors = textFieldColors,
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                        Box(
                                                            modifier = Modifier
                                                                .matchParentSize()
                                                                .clickable {
                                                                    groupExpanded = !groupExpanded
                                                                }
                                                        )
                                                        DropdownMenu(
                                                            expanded = groupExpanded,
                                                            onDismissRequest = { groupExpanded = false },
                                                            modifier = Modifier
                                                                .background(
                                                                    color = cardColor,
                                                                )
                                                                .clip(RoundedCornerShape(12.dp)),
                                                            shape = RoundedCornerShape(12.dp)
                                                        ) {
                                                            val groupOptions = remember { ExerciseGroup.entries.toTypedArray() }

                                                            groupOptions.forEachIndexed { index, option ->
                                                                val emoji = getExerciseGroupEmoji(option)
                                                                val groupText = getExerciseGroupStringResource(option)

                                                                DropdownMenuItem(
                                                                    text = {
                                                                        Row(
                                                                            verticalAlignment = Alignment.CenterVertically,
                                                                            modifier = Modifier.fillMaxWidth()
                                                                        ) {
                                                                            Text(
                                                                                text = emoji,
                                                                                style = MaterialTheme.typography.bodyMedium,
                                                                                modifier = Modifier.padding(end = 8.dp)
                                                                            )
                                                                            Text(
                                                                                text = groupText,
                                                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                                                    fontWeight = if (group == option) FontWeight.Bold else FontWeight.Normal
                                                                                ),
                                                                                color = textColor
                                                                            )
                                                                            Spacer(Modifier.weight(1f))
                                                                            if (group == option) {
                                                                                Icon(
                                                                                    Icons.Filled.Check,
                                                                                    contentDescription = null,
                                                                                    tint = MaterialTheme.colorScheme.primary,
                                                                                    modifier = Modifier.size(18.dp)
                                                                                )
                                                                            }
                                                                        }
                                                                    },
                                                                    onClick = {
                                                                        group = option
                                                                        groupExpanded = false
                                                                    },
                                                                    modifier = Modifier
                                                                        .background(
                                                                            color = if (group == option)
                                                                                MaterialTheme.colorScheme.primaryContainer.copy(
                                                                                    alpha = 0.3f
                                                                                )
                                                                            else Color.Transparent,
                                                                            shape = RoundedCornerShape(
                                                                                8.dp
                                                                            )
                                                                        )
                                                                        .padding(horizontal = 4.dp)
                                                                )
                                                                if (index < groupOptions.size - 1) {
                                                                    androidx.compose.material3.HorizontalDivider(
                                                                        color = Color.Gray.copy(alpha = 0.2f),
                                                                        thickness = 0.5.dp,
                                                                        modifier = Modifier.padding(horizontal = 12.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    OutlinedTextField(
                                                        value = weight?.toString() ?: "",
                                                        onValueChange = { weight = it.toDoubleOrNull() },
                                                        label = {
                                                            Text(
                                                                text = stringResource(R.string.weight),
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                                },
                                                        singleLine = true,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = textFieldColors,
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    val steps = remember { settings.weightSteps.sorted() }

                                                    FlowRow(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        steps.forEach { stepValue ->
                                                            val currentStepFormatted = rememberFormattedStep(step = stepValue)

                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                            ) {
                                                                IconButton(
                                                                    onClick = {
                                                                        val current = weight ?: 0.0
                                                                        val newWeight = (current - stepValue).coerceAtLeast(0.0)
                                                                        weight = if (newWeight.rem(1.0) == 0.0) newWeight.toInt().toDouble() else String.format(Locale.US, "%.3f", newWeight).trimEnd('0').trimEnd('.').toDouble()

                                                                        if (settings.useHapticFeedback) {
                                                                            haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                                                        }
                                                                    },
                                                                    modifier = Modifier.size(32.dp)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Filled.Remove,
                                                                        contentDescription = stringResource(R.string.decrease_weight_by, currentStepFormatted),
                                                                        tint = Color.Red.copy(red = 1f, green = 0.2f, blue = 0.2f)
                                                                    )
                                                                }

                                                                Text(
                                                                    text = currentStepFormatted,
                                                                    modifier = Modifier.padding(horizontal = 4.dp),
                                                                    fontSize = 14.sp,
                                                                    fontWeight = FontWeight.Medium,
                                                                    color = textColor
                                                                )

                                                                IconButton(
                                                                    onClick = {
                                                                        val current = weight ?: 0.0
                                                                        val newWeight = current + stepValue
                                                                        weight = if (newWeight.rem(1.0) == 0.0) newWeight.toInt().toDouble() else String.format(Locale.US, "%.3f", newWeight).trimEnd('0').trimEnd('.').toDouble()

                                                                        if (settings.useHapticFeedback) {
                                                                            haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                                                        }
                                                                    },
                                                                    modifier = Modifier.size(32.dp)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Filled.Add,
                                                                        contentDescription = stringResource(R.string.increase_weight_by, currentStepFormatted),
                                                                        tint = Color.Green.copy(red = 0.2f, green = 0.8f, blue = 0.2f)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            if (colorDialogOpen) {
                                                ColorPickerDialog(
                                                    initialColor = colorPickerInitialColor,
                                                    onColorSelected = { selectedColor ->
                                                        colorPickerInitialColor = selectedColor
                                                        itemColorHex = colorToHex(selectedColor)
                                                        colorDialogOpen = false
                                                    },
                                                    onDismiss = { colorDialogOpen = false }
                                                )
                                            }
                                            if (showNoteDialog) {
                                                NoteEditDialog(
                                                    initialNote = noteInputForDialog,
                                                    onNoteSave = { updatedNote ->
                                                        note = updatedNote
                                                        noteInputForDialog = updatedNote
                                                        showNoteDialog = false
                                                    },
                                                    onDismiss = { showNoteDialog = false }
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clickable {
                                                            colorPickerInitialColor =
                                                                hexToColor(itemColorHex)
                                                            colorDialogOpen = true
                                                        },
                                                    shape = CircleShape,
                                                    color = hexToColor(itemColorHex),
                                                ) {
                                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            Icons.Filled.ColorLens,
                                                            contentDescription = stringResource(R.string.select_color),
                                                            tint = textColor,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(Modifier.width(8.dp))
                                                Surface(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clickable {
                                                            noteInputForDialog = note
                                                            showNoteDialog = true
                                                        },
                                                    shape = CircleShape,
                                                    color = hexToColor(itemColorHex),
                                                ) {
                                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Filled.EditNote,
                                                            contentDescription = stringResource(R.string.edit_note),
                                                            tint = textColor
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.weight(1f))

                                                Surface(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clickable {
                                                            trainingToDelete = exercise
                                                            showDeleteDialog = true
                                                            if (settings.useHapticFeedback) {
                                                                haptic.performHapticFeedback(HapticFeedbackType.Reject)
                                                            }
                                                        },
                                                    shape = CircleShape,
                                                    color = MaterialTheme.colorScheme.errorContainer,
                                                ) {
                                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            Icons.Rounded.Delete,
                                                            contentDescription = stringResource(R.string.delete_training),
                                                            tint = MaterialTheme.colorScheme.error
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Button(
                                                    onClick = {
                                                        viewModel.update(
                                                            exercise.copy(
                                                                name = name,
                                                                weight = weight,
                                                                note = note,
                                                                group = group,
                                                                color = itemColorHex
                                                            )
                                                        )
                                                        expandedItemIndex = -1

                                                        if (settings.useHapticFeedback) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                                                    shape = RoundedCornerShape(16.dp),
                                                ) {
                                                    Icon(
                                                        Icons.Filled.Save,
                                                        contentDescription = null,
                                                        tint = textColor,
                                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                                    )
                                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                                    Text(
                                                        stringResource(R.string.save),
                                                        color = textColor,
                                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteDialog && trainingToDelete != null) {
            DeleteExerciseDialog(
                exercise = trainingToDelete!!,
                onConfirm = { viewModel.delete(trainingToDelete!!) },
                onDismiss = { showDeleteDialog = false }
            )
        }
    } // Ende Scaffold
}
