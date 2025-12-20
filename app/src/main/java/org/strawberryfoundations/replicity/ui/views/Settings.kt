package org.strawberryfoundations.replicity.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.strawberryfoundations.replicity.R
import org.strawberryfoundations.replicity.core.BackupManager
import org.strawberryfoundations.replicity.core.preferences.AppSettings
import org.strawberryfoundations.replicity.ui.theme.ascenderHeight
import org.strawberryfoundations.replicity.ui.theme.counterWidth
import org.strawberryfoundations.replicity.ui.theme.font.GoogleSansFlex
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun BackupSettingsSection() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val backupManager = remember { BackupManager(context) }

    var isExporting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    var isMerging by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var pendingImportUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // Export Launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                isExporting = true
                statusMessage = try {
                    val outputStream = context.contentResolver.openOutputStream(it)
                    if (outputStream != null && backupManager.exportBackup(outputStream)) {
                        context.getString(R.string.backup_export_success)
                    } else {
                        context.getString(R.string.backup_export_failed)
                    }
                } catch (e: Exception) {
                    context.getString(R.string.backup_error, e.message ?: "")
                }
                isExporting = false
            }
        }
    }

    // Import Launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            pendingImportUri = it
            showImportDialog = true
        }
    }

    // Import Dialog für Replace vs Merge
    if (showImportDialog && pendingImportUri != null) {
        AlertDialog(
            onDismissRequest = { 
                showImportDialog = false
                pendingImportUri = null
            },
            title = { Text(stringResource(R.string.backup_import_options)) },
            text = { 
                Column {
                    Text(stringResource(R.string.backup_import_options_desc))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.backup_import_replace_warning),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Merge Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isMerging = true
                                statusMessage = try {
                                    val inputStream = context.contentResolver.openInputStream(pendingImportUri!!)
                                    if (inputStream != null && backupManager.importBackupMerge(inputStream)) {
                                        context.getString(R.string.backup_merge_success)
                                    } else {
                                        context.getString(R.string.backup_import_failed)
                                    }
                                } catch (e: Exception) {
                                    context.getString(R.string.backup_error, e.message ?: "")
                                }
                                isMerging = false
                                showImportDialog = false
                                pendingImportUri = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(stringResource(R.string.backup_merge))
                    }
                    
                    // Replace Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isImporting = true
                                statusMessage = try {
                                    val inputStream = context.contentResolver.openInputStream(pendingImportUri!!)
                                    if (inputStream != null && backupManager.importBackup(inputStream)) {
                                        context.getString(R.string.backup_import_success)
                                    } else {
                                        context.getString(R.string.backup_import_failed)
                                    }
                                } catch (e: Exception) {
                                    context.getString(R.string.backup_error, e.message ?: "")
                                }
                                isImporting = false
                                showImportDialog = false
                                pendingImportUri = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.backup_replace))
                    }
                    
                    // Cancel Button
                    OutlinedButton(
                        onClick = { 
                            showImportDialog = false
                            pendingImportUri = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        )
    }

    // Export Setting Item
    SettingsItem(
        icon = Icons.Filled.Upload,
        title = stringResource(R.string.backup_export),
        subtitle = stringResource(R.string.backup_export_desc_with_trainings),
        onClick = if (!isExporting && !isImporting && !isMerging) {
            {
                val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
                exportLauncher.launch("gymscribe_backup_$timestamp.json")
            }
        } else null,
        trailingContent = {
            if (isExporting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    )

    // Import Setting Item
    SettingsItem(
        icon = Icons.Filled.Download,
        title = stringResource(R.string.backup_import),
        subtitle = stringResource(R.string.backup_import_desc_with_trainings),
        onClick = if (!isExporting && !isImporting && !isMerging) {
            { importLauncher.launch(arrayOf("application/json")) }
        } else null,
        trailingContent = {
            if (isImporting || isMerging) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    )

    // Status Message
    statusMessage?.let { message ->
        SettingsItem(
            icon = Icons.Filled.Info,
            title = message,
            onClick = { statusMessage = null }
        )
    }
}

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName
    } catch (_: PackageManager.NameNotFoundException) {
        "N/A"
    }.toString()
}

@Composable
fun SettingsSectionTitle(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(start = 16.dp, bottom = 6.dp, top = 16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val itemModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = itemModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (trailingContent != null) {
            Spacer(Modifier.width(16.dp))
            trailingContent()
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@SuppressLint("DefaultLocale", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsView(
    settings: AppSettings,
    onSettingsChange: (AppSettings.() -> AppSettings) -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val appVersion = remember { getAppVersion(context) }

    val termsOfServiceUrl = "https://your-app.com/terms"
    val githubUrl = "https://github.com/Strawberry-Foundations/gymscribe"

    var showDialog by remember { mutableStateOf(false) }

    Scaffold { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Appearance Section
            item {
                SettingsSectionTitle(
                    title = stringResource(R.string.settings_section_appearance),
                    icon = Icons.Default.ColorLens
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Filled.Palette,
                    title = stringResource(R.string.dynamic_colors),
                    subtitle = stringResource(R.string.dynamic_colors_description),
                    trailingContent = {
                        Switch(
                            checked = settings.useDynamicColors,
                            onCheckedChange = { checked ->
                                onSettingsChange { copy(useDynamicColors = checked) }
                            }
                        )
                    }
                )
            }

            // Weight Steps Section
            item {
                SettingsSectionTitle(
                    title = stringResource(R.string.weight_steps_section),
                    icon = Icons.Default.Scale
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = { showDialog = true },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                    ) {
                        Icon(
                            Icons.Filled.Edit, 
                            contentDescription = stringResource(R.string.edit),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    VerticalDivider(
                        modifier = Modifier
                            .height(28.dp)
                            .padding(start = 8.dp, end = 8.dp),
                        thickness = 2.dp
                    )
                    settings.weightSteps.sorted().forEach { step ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = step.toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(
                                            Font(
                                                R.font.roboto_flex,
                                                variationSettings = FontVariation.Settings(
                                                    FontVariation.weight(500),
                                                    FontVariation.width(70f),
                                                    ascenderHeight(GoogleSansFlex.TitleMediumVFConfig.ASCENDER_HEIGHT),
                                                    counterWidth(GoogleSansFlex.TitleMediumVFConfig.COUNTER_WIDTH)
                                                )
                                            )
                                        )
                                    )
                                ) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            item { HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) }
            // Backup & Sync Section
            item {
                SettingsSectionTitle(
                    title = stringResource(R.string.backup_sync_section),
                    icon = Icons.Filled.Sync
                )
            }
            item {
                BackupSettingsSection()
            }

            item { HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) }

            // About Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp, top = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = stringResource(R.string.settings_section_about),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    Text(
                        text = stringResource(R.string.settings_section_about),
                        style = MaterialTheme.typography.displayMedium,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.splash),
                        contentDescription = null,
                        modifier = Modifier.size(58.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gymscribe",
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 28.sp,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Version $appVersion",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "© 2025 Juliandev02",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.settings_all_rights_reserved),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.stbfnds),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(end = 4.dp)
                        )
                        Text(
                            text = "#stbfnds",
                            style = MaterialTheme.typography.displayMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            item { HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) }

            item {
                SettingsItem(
                    icon = Icons.Filled.Gavel,
                    title = stringResource(R.string.settings_terms_of_service),
                    onClick = {
                        try {
                            uriHandler.openUri(termsOfServiceUrl)
                        } catch (_: Exception) {
                            println("Could not open URL: $termsOfServiceUrl")
                        }
                    }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Filled.Code,
                    title = stringResource(R.string.settings_github),
                    onClick = {
                        try {
                            uriHandler.openUri(githubUrl)
                        } catch (_: Exception) {
                            println("Could not open URL: $githubUrl")
                        }
                    }
                )
            }
        }

        // Weight Steps Dialog
        if (showDialog) {
            var newStepInputDialog by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.edit_weight_steps)) },
                text = {
                    Column {
                        settings.weightSteps.sorted().forEach { step ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(step.toString(), modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        onSettingsChange { copy(weightSteps = weightSteps - step) }
                                    }
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        androidx.compose.material3.OutlinedTextField(
                            value = newStepInputDialog,
                            onValueChange = { newStepInputDialog = it },
                            label = { Text(stringResource(R.string.new_step)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val value = newStepInputDialog.replace(',', '.').toDoubleOrNull()
                                if (value != null && value > 0 && value !in settings.weightSteps) {
                                    onSettingsChange { copy(weightSteps = weightSteps + value) }
                                    newStepInputDialog = ""
                                }
                            },
                            enabled = newStepInputDialog.isNotBlank()
                        ) {
                            Text(stringResource(R.string.add))
                        }
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.done))
                    }
                }
            )
        }
    }
}