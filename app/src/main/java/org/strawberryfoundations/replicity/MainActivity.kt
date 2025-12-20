package org.strawberryfoundations.replicity

import android.os.Bundle
import android.transition.Fade
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.strawberryfoundations.replicity.core.preferences.AppSettings
import org.strawberryfoundations.replicity.core.preferences.SettingsDataStore
import org.strawberryfoundations.replicity.core.preferences.UserPreferences
import org.strawberryfoundations.replicity.core.preferences.getUserDataFlow
import org.strawberryfoundations.replicity.ui.theme.GymscribeTheme
import org.strawberryfoundations.replicity.ui.views.DeviceView
import org.strawberryfoundations.replicity.ui.views.ProfileView
import org.strawberryfoundations.replicity.ui.views.SettingsView
import org.strawberryfoundations.replicity.ui.views.TrainingView


// Class: MainActivity
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

// Composable: MainViewWithPersistence
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

// Composable: MainView
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainView(
    settings: AppSettings,
    onSettingsChange: (AppSettings.() -> AppSettings) -> Unit,
) {
    val context = LocalContext.current
    var userData by remember { mutableStateOf<UserPreferences?>(null) }

    LaunchedEffect(Unit) {
        userData = try {
            val prefs = getUserDataFlow(context).first()
            if (prefs.username.isNotBlank()) {
                prefs
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    var selectedItem by remember { mutableIntStateOf(0) }
    var showProfile by remember { mutableStateOf(false) }
    
    val items = listOf(
        stringResource(R.string.your_workouts),
        stringResource(R.string.device),
        stringResource(R.string.settings),
    )

    val selectedIcons = listOf(
        Icons.Filled.FitnessCenter,
        Icons.Filled.Watch,
        Icons.Filled.Settings,
    )
    val unselectedIcons = listOf(
        Icons.Outlined.FitnessCenter,
        Icons.Outlined.Watch,
        Icons.Outlined.Settings,
    )

    val navBarColor = NavigationBarDefaults.containerColor

    // Handle Android back gesture when profile is open
    BackHandler(enabled = showProfile) {
        showProfile = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        Icon(
                            imageVector = selectedIcons[selectedItem],
                            contentDescription = items[selectedItem],
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp)
                                .size(26.dp),
                        )
                    },
                    title = {
                        Text(
                            text = items[selectedItem],
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 22.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = { showProfile = true }) {
                            AsyncImage(
                                model = userData?.profilePictureUrl,
                                contentDescription = stringResource(R.string.profile_picture),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.ic_launcher),
                                error = painterResource(R.drawable.ic_launcher)
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
                    containerColor = navBarColor,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                icon = {
                                    Icon(
                                        imageVector = if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                        contentDescription = item
                                    )
                                },
                                label = {
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        maxLines = if (index == 0) 2 else 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            AnimatedContent(
                targetState = selectedItem,
                transitionSpec = {
                    val duration = 260
                    val easing = FastOutSlowInEasing
                    if (targetState > initialState) {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = duration, easing = easing)
                        ) + fadeIn(animationSpec = tween(durationMillis = duration, easing = easing)) togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(durationMillis = duration, easing = easing)
                                ) + fadeOut(animationSpec = tween(durationMillis = duration, easing = easing))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(durationMillis = duration, easing = easing)
                        ) + fadeIn(animationSpec = tween(durationMillis = duration, easing = easing)) togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(durationMillis = duration, easing = easing)
                                ) + fadeOut(animationSpec = tween(durationMillis = duration, easing = easing))
                    }
                }
            ) { index ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    when (index) {
                        0 -> TrainingView(settings = settings)
                        1 -> DeviceView()
                        2 -> SettingsView(
                            settings = settings,
                            onSettingsChange = onSettingsChange
                        )
                    }
                }
            }
        }

        // Profile page overlay
        AnimatedVisibility(
            visible = showProfile,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 350, easing = EaseInOutCubic)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 350, easing = EaseInOutCubic)
            )
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { showProfile = false }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.profile),
                                style = MaterialTheme.typography.displayLarge,
                                fontSize = 22.sp
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = navBarColor,
                            scrolledContainerColor = navBarColor
                        )
                    )
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    ProfileView()
                }
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
