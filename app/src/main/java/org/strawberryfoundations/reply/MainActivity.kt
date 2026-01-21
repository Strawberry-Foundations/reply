package org.strawberryfoundations.reply

import ExerciseDetail
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.filled.DevicesWearables
import org.strawberryfoundations.material.symbols.filled.Exercise
import org.strawberryfoundations.material.symbols.outlined.DevicesWearables
import org.strawberryfoundations.material.symbols.outlined.Exercise
import org.strawberryfoundations.reply.core.AppSettings
import org.strawberryfoundations.reply.core.AvatarCache
import org.strawberryfoundations.reply.core.SettingsDataStore
import org.strawberryfoundations.reply.core.getUserDataFlow
import org.strawberryfoundations.reply.core.model.UserPreferences
import org.strawberryfoundations.reply.room.ExerciseViewModel
import org.strawberryfoundations.reply.ui.theme.AppTheme
import org.strawberryfoundations.reply.ui.theme.darkenColor
import org.strawberryfoundations.reply.ui.theme.hexToColor
import org.strawberryfoundations.reply.ui.views.ActiveExercise
import org.strawberryfoundations.reply.ui.views.DebugView
import org.strawberryfoundations.reply.ui.views.DeviceView
import org.strawberryfoundations.reply.ui.views.ProfileView
import org.strawberryfoundations.reply.ui.views.SettingsView
import org.strawberryfoundations.reply.ui.views.TrainingView


// Class: MainActivity
class MainActivity : ComponentActivity() {
    private lateinit var appSettings: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.setBackgroundColor(android.graphics.Color.BLACK)
        appSettings = SettingsDataStore(applicationContext)

        setContent {
            MainViewWithPersistence(appSettings)
        }
    }
}

// Composable: MainViewWithPersistence
@Composable
fun MainViewWithPersistence(appSettings: SettingsDataStore) {
    val settings by appSettings.settingsFlow.collectAsState(initial = AppSettings())
    val scope = rememberCoroutineScope()

    AppTheme(dynamicColor = settings.useDynamicColors) {
        MainView(
            settings = settings,
            onSettingsChange = { update ->
                scope.launch {
                    appSettings.updateSettings(update)
                }
            }
        )
    }
}

// Composable: MainView
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun MainView(
    settings: AppSettings,
    onSettingsChange: (AppSettings.() -> AppSettings) -> Unit,
    viewModel: ExerciseViewModel = viewModel(),
) {
    val context = LocalContext.current
    val imageLoader = remember { AvatarCache.getImageLoader(context) }
    var userData by remember { mutableStateOf<UserPreferences?>(null) }

    // Retrieve user data from datastore
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
    val rootNavController = rememberNavController()
    
    // Handle navigation from notification
    LaunchedEffect(Unit) {
        if (context is ComponentActivity) {
            val sessionId = context.intent?.getLongExtra("navigate_to_session", -1L)
            if (sessionId != null && sessionId != -1L) {
                rootNavController.navigate("activeExercise/$sessionId")
                context.intent?.removeExtra("navigate_to_session")
            }
        }
    }

    val items = listOf(
        stringResource(R.string.workout),
        stringResource(R.string.device),
        stringResource(R.string.settings),
    )

    val selectedIcons = listOf(
        MaterialSymbols.Filled.Exercise,
        MaterialSymbols.Filled.DevicesWearables,
        Icons.Filled.Settings,
    )
    val unselectedIcons = listOf(
        MaterialSymbols.Outlined.Exercise,
        MaterialSymbols.Outlined.DevicesWearables,
        Icons.Outlined.Settings,
    )

    val navBarColor = NavigationBarDefaults.containerColor

    BackHandler(enabled = showProfile) {
        showProfile = false
    }

    // Navigation host
    NavHost(
        navController = rootNavController,
        startDestination = "main",
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        enterTransition = {
            fadeIn(tween(300)) + slideInHorizontally(
                initialOffsetX = { it / 8 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(tween(300)) + slideOutHorizontally(
                targetOffsetX = { -it / 10 },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(tween(300)) + slideInHorizontally(
                initialOffsetX = { -it / 8 },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(tween(300)) + slideOutHorizontally(
                targetOffsetX = { it / 10 },
                animationSpec = tween(300)
            )
        }
    ) {
        // Navigation Route: main
        composable("main") {
            // Main Scaffold
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        // Top App Bar
                        CenterAlignedTopAppBar(
                            navigationIcon = {
                                AnimatedContent(
                                    targetState = selectedItem,
                                ) { index ->
                                    Icon(
                                        imageVector = selectedIcons[index],
                                        contentDescription = items[index],
                                        modifier = Modifier
                                            .padding(start = 16.dp, end = 16.dp)
                                            .size(26.dp),
                                    )
                                }
                            },
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Reply",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 24.sp
                                    )
                                    Text(
                                        text = "+",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 24.sp,
                                        color = MaterialTheme.colorScheme.primary

                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { showProfile = true }) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(data = userData?.profilePictureUrl)
                                            .memoryCacheKey(key = userData?.username ?: "default")
                                            .diskCacheKey(key = userData?.username ?: "default")
                                            .crossfade(enable = true)
                                            .build(),
                                        contentDescription = stringResource(R.string.profile_picture),
                                        imageLoader = imageLoader,
                                        modifier = Modifier
                                            .size(size = 38.dp)
                                            .clip(shape = MaterialShapes.Cookie9Sided.toShape()),
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
                    // Main pages
                    AnimatedContent(
                        targetState = selectedItem,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) { index ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            when (index) {
                                0 -> TrainingView(
                                    settings = settings,
                                    onExerciseClick = { exerciseId ->
                                        rootNavController.navigate("exerciseDetail/$exerciseId")
                                    },
                                    onActiveSessionClick = { exerciseId ->
                                        rootNavController.navigate("activeExercise/$exerciseId")
                                    }
                                )
                                1 -> DeviceView(
                                    settings = settings
                                )
                                2 -> SettingsView(
                                    settings = settings,
                                    onSettingsChange = onSettingsChange,
                                    onDebugClick = {
                                        rootNavController.navigate("debug")
                                    }
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
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(WindowInsets.statusBars.asPaddingValues())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 64.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            ProfileView()
                        }

                        IconButton(
                            onClick = { showProfile = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // Navigation Route: exerciseDetail/{exerciseId}
        composable(
            route = "exerciseDetail/{exerciseId}",
            arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
            val trainings by viewModel.trainings.collectAsState()
            val exercise = trainings.firstOrNull { it.id == exerciseId }

            val cardColor = darkenColor(hexToColor(exercise?.color ?: ""), 0.55f)
            val textColor = remember(cardColor) {
                if (cardColor.luminance() > 0.55f) Color.Black else Color.White
            }

            if (exercise == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ContainedLoadingIndicator()
                }
                return@composable
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            navigationIcon = {
                                IconButton(onClick = { rootNavController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            title = {
                                Text(
                                    text = stringResource(R.string.workout),
                                    // text = getExerciseGroupEmoji(exercise.group).repeat(7),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 22.sp
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = cardColor,
                                titleContentColor = textColor
                            )
                        )
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        ExerciseDetail(
                            exercise = exercise,
                            onStartTraining = { exerciseToStart ->
                                rootNavController.navigate("activeExercise/${exerciseToStart.id}")
                            },
                            settings = settings,
                        )
                    }
                }
            }
        }

        // Navigation Route: activeExercise/{exerciseId}
        composable(
            route = "activeExercise/{exerciseId}",
            arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
            
            ActiveExercise(
                sessionId = exerciseId,
                settings = settings,
                onSessionComplete = {
                    rootNavController.popBackStack()
                }
            )
        }

        // Navigation Route: debug
        composable(
            route = "debug",
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
            ) {
                DebugView(
                    settings = settings,
                    exerciseVm = viewModel()
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

    AppTheme(dynamicColor = previewSettings.useDynamicColors) {
        MainView(
            settings = previewSettings,
            onSettingsChange = {}
        )
    }
}
