package org.strawberryfoundations.replicity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.strawberryfoundations.replicity.core.preferences.AppSettings
import org.strawberryfoundations.replicity.core.preferences.SettingsDataStore
import org.strawberryfoundations.replicity.ui.theme.GymscribeTheme
import org.strawberryfoundations.replicity.ui.views.DeviceView
import org.strawberryfoundations.replicity.ui.views.ProfileView
import org.strawberryfoundations.replicity.ui.views.SettingsView
import org.strawberryfoundations.replicity.ui.views.TrainingView


class MainActivity : ComponentActivity() {
    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        settingsDataStore = SettingsDataStore(applicationContext)

        setContent {
            MainViewWithPersistence(settingsDataStore)
        }
    }
}

@Composable
fun MainViewWithPersistence(settingsDataStore: SettingsDataStore) {
    val settings by settingsDataStore.settingsFlow.collectAsState(initial = AppSettings())
    val scope = rememberCoroutineScope()

    GymscribeTheme(dynamicColor = settings.useDynamicColors) {
        MainView(
            settings = settings,
            onSettingsChange = { update ->
                scope.launch {
                    settingsDataStore.updateSettings(update)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    settings: AppSettings,
    onSettingsChange: (AppSettings.() -> AppSettings) -> Unit,
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        stringResource(R.string.training),
        stringResource(R.string.profile),
        stringResource(R.string.device),
        stringResource(R.string.settings),
    )
    val selectedIcons = listOf(
        Icons.Filled.FitnessCenter,
        Icons.Filled.AccountCircle,
        Icons.Filled.Devices,
        Icons.Filled.Settings,
    )
    val unselectedIcons = listOf(
        Icons.Outlined.FitnessCenter,
        Icons.Outlined.AccountCircle,
        Icons.Outlined.Devices,
        Icons.Outlined.Settings,
    )

    val navBarColor = NavigationBarDefaults.containerColor

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = selectedIcons[selectedItem],
                            contentDescription = items[selectedItem],
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(31.dp),
                        )

                        Text(
                            text = items[selectedItem],
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 30.sp,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = navBarColor,
                    scrolledContainerColor = navBarColor
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = navBarColor
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                contentDescription = item
                            )
                        },
                        label = {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 12.sp
                            )
                        },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> TrainingView(
                    settings = settings
                )
                1 -> ProfileView()
                2 -> DeviceView()
                3 -> SettingsView(
                    settings = settings,
                    onSettingsChange = onSettingsChange
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    val previewSettings = AppSettings(
        useDynamicColors = true,
    )

    GymscribeTheme(dynamicColor = previewSettings.useDynamicColors) {
        MainView(
            settings = previewSettings,
            onSettingsChange = {}
        )
    }
}