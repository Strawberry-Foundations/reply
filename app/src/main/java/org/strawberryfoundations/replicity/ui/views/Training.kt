package org.strawberryfoundations.replicity.ui.views

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import org.strawberryfoundations.replicity.R
import org.strawberryfoundations.replicity.core.model.Training
import org.strawberryfoundations.replicity.core.preferences.AppSettings
import org.strawberryfoundations.replicity.data.TrainingViewModel
import org.strawberryfoundations.replicity.ui.theme.TitleMediumVFConfig
import org.strawberryfoundations.replicity.ui.theme.ascenderHeight
import org.strawberryfoundations.replicity.ui.theme.counterWidth
import java.util.Locale


@Composable
fun SimpleColorPickerDialog(
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
        title = { Text(stringResource(R.string.select_color)) },
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
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .size(32.dp)
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

@Composable
fun NoteEditDialog(
    initialNote: String,
    onNoteSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentNote by remember(initialNote) { mutableStateOf(initialNote) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_note_title)) },
        text = {
            OutlinedTextField(
                value = currentNote,
                onValueChange = { currentNote = it },
                label = { Text(stringResource(R.string.note)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
            )
        },
        confirmButton = {
            TextButton(onClick = { onNoteSave(currentNote) }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

fun colorToHex(color: Color): String =
    "#%02X%02X%02X".format(
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
    )

fun hexToColor(hex: String): Color =
    runCatching { Color(hex.toColorInt()) }.getOrDefault(Color.LightGray)

fun darkenColor(color: Color, factor: Float = 0.85f): Color {
    return Color(
        red = (color.red * factor).coerceIn(0f, 1f),
        green = (color.green * factor).coerceIn(0f, 1f),
        blue = (color.blue * factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

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

@SuppressLint("DefaultLocale", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalLayoutApi::class, ExperimentalAnimationApi::class, ExperimentalTextApi::class)
@Composable
fun TrainingView(
    viewModel: TrainingViewModel = viewModel(),
    settings: AppSettings,
) {
    val allStr = stringResource(R.string.all)
    val upperBodyStr = stringResource(R.string.upper_body)
    val legsStr = stringResource(R.string.legs)
    val otherStr = stringResource(R.string.other)

    val categories = listOf(
        allStr,
        upperBodyStr,
        legsStr,
        otherStr
    )

    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var expandedItemIndex by remember { mutableIntStateOf(-1) }

    val exercises by viewModel.trainings.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var trainingToDelete by remember { mutableStateOf<Training?>(null) }

    val filteredExercises by remember(exercises, selectedCategoryIndex, upperBodyStr, legsStr, otherStr) {
        derivedStateOf {
            when (selectedCategoryIndex) {
                1 -> exercises.filter { it.group == upperBodyStr }
                2 -> exercises.filter { it.group == legsStr }
                3 -> exercises.filter { it.group == otherStr }
                else -> exercises.toList()
            }
        }
    }

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

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(visible = expandedItemIndex == -1) {
                val newTrainingName = stringResource(R.string.new_training)
                val addTrainingLabel = stringResource(R.string.add_training)
                val str = stringResource(R.string.all)

                FloatingActionButton(
                    onClick = {
                        val group = if (categories[selectedCategoryIndex] != str) {
                            categories[selectedCategoryIndex]
                        } else {
                            otherStr
                        }
                        viewModel.insert(
                            Training(
                                name = newTrainingName,
                                color = defaultColorHex,
                                group = group
                            )
                        )
                    },
                ) {
                    Icon(Icons.Filled.Add, contentDescription = addTrainingLabel)
                }
            }
        }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val selectedColor = MaterialTheme.colorScheme.primaryContainer
            val unselectedColor = MaterialTheme.colorScheme.surfaceVariant
            val selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            val unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            val shape = RoundedCornerShape(18.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEachIndexed { index, cat ->
                    val isSelected = selectedCategoryIndex == index
                    val onChipClick = remember(index) { { selectedCategoryIndex = index; expandedItemIndex = -1 } }

                    val animatedBackground by animateColorAsState(
                        targetValue = if (isSelected) selectedColor else unselectedColor,
                        label = "chipBg"
                    )
                    val animatedTextColor by animateColorAsState(
                        targetValue = if (isSelected) selectedTextColor else unselectedTextColor,
                        label = "chipText"
                    )

                    FilterChip(
                        selected = isSelected,
                        onClick = onChipClick,
                        label = {
                            val emoji = when (index) {
                                1 -> "💪 "
                                2 -> "🦵 "
                                3 -> "🧩 "
                                else -> "🏋 "
                            }

                            val fontFamily = FontFamily(
                                Font(
                                    R.font.roboto_flex,
                                    variationSettings = FontVariation.Settings(
                                        FontVariation.weight(450),
                                        FontVariation.width(70f),
                                        ascenderHeight(TitleMediumVFConfig.ASCENDER_HEIGHT),
                                        counterWidth(TitleMediumVFConfig.COUNTER_WIDTH)
                                    )
                                )
                            )

                            if (settings.useEmojisForGroups) {
                                Text(
                                    text = emoji + cat,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = animatedTextColor,
                                        fontFamily = fontFamily
                                    )
                                )
                            } else {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = animatedTextColor,
                                        fontFamily = fontFamily
                                    )
                                )
                            }
                        },
                        leadingIcon = {
                            AnimatedContent(
                                targetState = isSelected,
                                transitionSpec = {
                                    fadeIn().togetherWith(fadeOut())
                                }, label = "chipIcon"
                            ) { showIcon ->
                                if (showIcon) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = animatedTextColor
                                    )
                                }
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = animatedBackground,
                            labelColor = animatedTextColor,
                            selectedContainerColor = animatedBackground,
                            selectedLabelColor = animatedTextColor
                        ),
                        shape = shape,
                        elevation = FilterChipDefaults.elevatedFilterChipElevation()
                    )
                }
            }

            val listState = rememberLazyListState()

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
                    ) { index, training ->
                        val cardColor = remember(training.color, themeColorHexMap, colorScheme.surface) {
                            themeColorHexMap[training.color]
                                ?: if (training.color.isNotBlank()) hexToColor(training.color)
                                else colorScheme.surface
                        }
                        val borderColor = remember(cardColor) { darkenColor(cardColor, 0.70f) }
                        val textColor = remember(cardColor) {
                            if (cardColor.luminance() > 0.55f) Color.Black else Color.White
                        }

                        val isExpanded = expandedItemIndex == index

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    expandedItemIndex = if (isExpanded) -1 else index
                                },
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            border = BorderStroke(2.dp, borderColor),
                            shape = MaterialTheme.shapes.large,
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
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
                                            training.name,
                                            color = textColor,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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
                                                "${training.weight} kg",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    fontFamily = FontFamily(
                                                        Font(
                                                            R.font.roboto_flex,
                                                            variationSettings = FontVariation.Settings(
                                                                FontVariation.weight(TitleMediumVFConfig.WEIGHT),
                                                                FontVariation.width(130f),
                                                                FontVariation.slant(TitleMediumVFConfig.SLANT),
                                                                ascenderHeight(TitleMediumVFConfig.ASCENDER_HEIGHT),
                                                                counterWidth(TitleMediumVFConfig.COUNTER_WIDTH)
                                                            )
                                                        )
                                                    )

                                                ),
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
                                    exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                                ) {
                                    var name by remember(training.name) { mutableStateOf(training.name) }
                                    var weight by remember(training.weight) { mutableStateOf(training.weight) }
                                    var note by remember(training.note) { mutableStateOf(training.note) }
                                    var group by remember(training.group) { mutableStateOf(training.group) }
                                    var groupExpanded by remember { mutableStateOf(false) }

                                    var itemColorHex by remember(training.color, defaultColorHex) {
                                        mutableStateOf(training.color.ifBlank { defaultColorHex })
                                    }
                                    var colorDialogOpen by remember { mutableStateOf(false) }
                                    var colorPickerInitialColor by remember(itemColorHex) { mutableStateOf(hexToColor(itemColorHex)) }

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
                                                shape = MaterialTheme.shapes.medium,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(Modifier.padding(12.dp)) {
                                                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = textColor.copy(alpha=0.7f),
                                                        unfocusedBorderColor = textColor.copy(alpha = 0.4f),
                                                        cursorColor = textColor,
                                                        focusedTextColor = textColor,
                                                        unfocusedTextColor = textColor,
                                                        disabledTextColor = textColor.copy(alpha=0.7f),
                                                        focusedLabelColor = textColor.copy(alpha=0.7f),
                                                        unfocusedLabelColor = textColor.copy(alpha=0.4f)
                                                    )

                                                    OutlinedTextField(
                                                        value = name,
                                                        onValueChange = { name = it },
                                                        label = { Text(stringResource(R.string.name)) },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = textFieldColors
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))

                                                    Box {
                                                        val emoji = when (group) {
                                                            upperBodyStr -> "💪"
                                                            legsStr -> "🦵"
                                                            otherStr -> "🧩"
                                                            else -> ""
                                                        }

                                                        val text = if (settings.useEmojisForGroups && emoji.isNotBlank()) {
                                                            "$emoji $group"
                                                        } else {
                                                            group.ifBlank { otherStr }
                                                        }

                                                        OutlinedTextField(
                                                            value = text,
                                                            onValueChange = {},
                                                            label = { Text(stringResource(R.string.group)) },
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable(
                                                                    indication = null,
                                                                    interactionSource = remember { MutableInteractionSource() }
                                                                ) { groupExpanded = !groupExpanded },
                                                            readOnly = true,
                                                            trailingIcon = {
                                                                Icon(Icons.Filled.ArrowDropDown, contentDescription = stringResource(R.string.group), tint = textColor)
                                                            },
                                                            colors = textFieldColors
                                                        )
                                                        Box(
                                                            modifier = Modifier
                                                                .matchParentSize()
                                                                .clickable { groupExpanded = !groupExpanded }
                                                        )
                                                        DropdownMenu(
                                                            expanded = groupExpanded,
                                                            onDismissRequest = { groupExpanded = false },
                                                            modifier = Modifier
                                                                .background(
                                                                    color = cardColor,
                                                                )
                                                                .clip(RoundedCornerShape(12.dp))
                                                        ) {
                                                            val groupOptions = remember { listOf(upperBodyStr, legsStr, otherStr) }
                                                            groupOptions.forEachIndexed { index, option ->
                                                                DropdownMenuItem(
                                                                    text = {
                                                                        Row(
                                                                            verticalAlignment = Alignment.CenterVertically,
                                                                            modifier = Modifier.fillMaxWidth()
                                                                        ) {
                                                                            val emoji = when (option) {
                                                                                upperBodyStr -> "💪"
                                                                                legsStr -> "🦵"
                                                                                otherStr -> "🧩"
                                                                                else -> ""
                                                                            }
                                                                            if (settings.useEmojisForGroups && emoji.isNotBlank()) {
                                                                                Text(
                                                                                    text = emoji,
                                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                                    modifier = Modifier.padding(end = 8.dp)
                                                                                )
                                                                            }
                                                                            Text(
                                                                                text = option,
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
                                                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                                                            else Color.Transparent,
                                                                            shape = RoundedCornerShape(8.dp)
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
                                                        value = weight,
                                                        onValueChange = { weight = it },
                                                        label = { Text(stringResource(R.string.weight_kg)) },
                                                        singleLine = true,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = textFieldColors
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
                                                                        val current = weight.replace(',', '.').toDoubleOrNull() ?: 0.0
                                                                        val newWeight = (current - stepValue).coerceAtLeast(0.0)
                                                                        weight = if (newWeight.rem(1.0) == 0.0) newWeight.toInt().toString() else String.format(Locale.US, "%.3f", newWeight).trimEnd('0').trimEnd('.')
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
                                                                        val current = weight.replace(',', '.').toDoubleOrNull() ?: 0.0
                                                                        val newWeight = current + stepValue
                                                                        weight = if (newWeight.rem(1.0) == 0.0) newWeight.toInt().toString() else String.format(Locale.US, "%.3f", newWeight).trimEnd('0').trimEnd('.')
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
                                                SimpleColorPickerDialog(
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
                                                        .size(36.dp)
                                                        .clickable {
                                                            colorPickerInitialColor =
                                                                hexToColor(itemColorHex)
                                                            colorDialogOpen = true
                                                        },
                                                    shape = CircleShape,
                                                    color = hexToColor(itemColorHex),
                                                    border = BorderStroke(2.dp, borderColor)
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
                                                IconButton(
                                                    onClick = {
                                                        noteInputForDialog = note
                                                        showNoteDialog = true
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.EditNote,
                                                        contentDescription = stringResource(R.string.edit_note_title),
                                                        tint = textColor
                                                    )
                                                }
                                                Spacer(modifier = Modifier.weight(1f))
                                                IconButton(
                                                    onClick = {
                                                        trainingToDelete = training
                                                        showDeleteDialog = true
                                                    }
                                                ) {
                                                    Icon(
                                                        Icons.Filled.Delete,
                                                        contentDescription = stringResource(R.string.delete_training),
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Button(
                                                    onClick = {
                                                        viewModel.update(
                                                            training.copy(
                                                                name = name,
                                                                weight = weight,
                                                                note = note,
                                                                group = group,
                                                                color = itemColorHex
                                                            )
                                                        )
                                                        expandedItemIndex = -1
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = cardColor)
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
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    trainingToDelete = null
                },
                title = { Text("\u26A0\uFE0F ${stringResource(R.string.delete_training)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                text = { Text(stringResource(R.string.delete_training_confirm, trainingToDelete?.name ?: "")) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            trainingToDelete?.let { viewModel.delete(it) }
                            showDeleteDialog = false
                            trainingToDelete = null
                            expandedItemIndex = -1
                        }
                    ) {
                        Text(stringResource(R.string.delete_training), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            trainingToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    } // Ende Scaffold
}
