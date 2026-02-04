package org.strawberryfoundations.reply.ui.views

import android.annotation.SuppressLint
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.core.AvatarCache
import org.strawberryfoundations.reply.core.getUserDataFlow
import org.strawberryfoundations.reply.core.model.UserPreferences
import org.strawberryfoundations.reply.core.saveUserData

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "LocalContextGetResourceValueCall")
fun ProfileView() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var userData by remember { mutableStateOf<UserPreferences?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var polling by rememberSaveable { mutableStateOf(false) }
    var currentLoginCode by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val prefs = getUserDataFlow(context).first()
            if (prefs.username.isNotBlank()) {
                userData = prefs
                isLoggedIn = true
            }
        } catch (_: Exception) {
            isLoggedIn = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isLoggedIn && userData != null) {
            LoggedInContent(
                userData = userData!!,
                onSignOut = {
                    coroutineScope.launch {
                        saveUserData(context, UserPreferences())
                        isLoggedIn = false
                        userData = null
                    }
                }
            )
        } else {
            LoginContent(
                isLoading = isLoading,
                polling = polling,
                errorMessage = errorMessage,
                onLoginClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            val code = fetchLoginCode()
                            currentLoginCode = code
                            val loginUrl = "https://id.strawberryfoundations.org/v2/de/login/oauth_dialog/reply?code=$code"
                            val intent = CustomTabsIntent.Builder().build().intent
                            intent.data = loginUrl.toUri()
                            context.startActivity(intent)
                            polling = true
                        } catch (e: Exception) {
                            errorMessage = context.getString(R.string.login_error, e.localizedMessage)
                        }
                        isLoading = false
                    }
                },
                onCancelLogin = {
                    polling = false
                    currentLoginCode = null
                }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }



    LaunchedEffect(polling, currentLoginCode) {
        if (polling && currentLoginCode != null) {
            handleLoginPolling(
                loginCode = currentLoginCode!!,
                maxAttempts = 30,
                onSuccess = { user ->
                    saveUserData(context, user)
                    userData = user
                    isLoggedIn = true
                    polling = false
                },
                onInvalidCode = { polling = false },
                onTimeout = { polling = false },
                onError = { polling = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoggedInContent(
    userData: UserPreferences,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val imageLoader = remember { AvatarCache.getImageLoader(context) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(userData.profilePictureUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialShapes.Cookie9Sided.toShape()),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_launcher),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.hello_user, userData.fullName.split(" ").firstOrNull() ?: "User"),
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {

                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text(
                        text = stringResource(R.string.manage_strawberry_id),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Account information section
        Text(
            text = stringResource(R.string.account_information),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                ProfileInfoRow(label = stringResource(R.string.email), value = userData.email)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surface
                )

                ProfileInfoRow(label = stringResource(R.string.strawberry_id), value = userData.username)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section header
        Text(
            text = stringResource(R.string.more_from_strawberry),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        // Settings section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                ProfileSettingsItem(
                    title = stringResource(R.string.account_and_profile),
                    subtitle = stringResource(R.string.manage_account_subtitle),
                    icon = Icons.Rounded.AccountCircle,
                    onClick = { }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surface
                )

                ProfileSettingsItem(
                    title = stringResource(R.string.strawberry_cloud),
                    subtitle = stringResource(R.string.strawberry_cloud_subtitle),
                    icon = Icons.Rounded.CloudQueue,
                    onClick = { }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surface
                )

                ProfileSettingsItem(
                    title = stringResource(R.string.notifications),
                    subtitle = stringResource(R.string.notifications_subtitle),
                    icon = Icons.Rounded.Notifications,
                    onClick = { }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surface
                )

                ProfileSettingsItem(
                    title = stringResource(R.string.privacy),
                    subtitle = stringResource(R.string.privacy_settings_subtitle),
                    icon = Icons.Rounded.PrivacyTip,
                    onClick = { }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surface
                )

                /* ProfileSettingsItem(
                    title = stringResource(R.string.app_settings),
                    subtitle = stringResource(R.string.app_settings_subtitle),
                    icon = Icons.Rounded.Settings,
                    onClick = { }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surface
                ) */

                ProfileSettingsItem(
                    title = stringResource(R.string.manage_storage),
                    subtitle = stringResource(R.string.manage_storage_subtitle),
                    icon = Icons.Rounded.Storage,
                    onClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onSignOut,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text(stringResource(R.string.sign_out), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    ListItem(
        headlineContent = { Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = { Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun ProfileSettingsItem(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        headlineContent = { Text(title, style = MaterialTheme.typography.bodyLarge) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = { Icon(Icons.Rounded.ChevronRight, null, modifier = Modifier.size(20.dp)) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun LoginContent(
    isLoading: Boolean,
    polling: Boolean,
    errorMessage: String?,
    onLoginClick: () -> Unit,
    onCancelLogin: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.sid_lowercase),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        // Text(text = stringResource(R.string.version_alpha), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.sign_in_with_sid),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = !isLoading && !polling,
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Color.White)
            } else {
                Text(if (polling) stringResource(R.string.waiting_for_login) else stringResource(R.string.sign_in_now))
            }
        }

        if (polling) {
            TextButton(onClick = onCancelLogin, modifier = Modifier.padding(top = 8.dp)) {
                Text(stringResource(R.string.cancel))
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(it, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center)
            }
        }
    }
}

private suspend fun handleLoginPolling(
    loginCode: String,
    maxAttempts: Int,
    onSuccess: suspend (UserPreferences) -> Unit,
    onInvalidCode: () -> Unit,
    onTimeout: () -> Unit,
    onError: () -> Unit
) {
    var attemptCount = 0

    while (attemptCount < maxAttempts) {
        try {
            val response = fetchUserDataWithCode(loginCode)

            when (response?.status) {
                "Ok" -> {
                    response.user?.let { onSuccess(it) }
                    return
                }
                "Invalid Code" -> {
                    onInvalidCode()
                    return
                }
                "Not authenticated" -> {
                    attemptCount++
                    delay(2000)
                }
                else -> {
                    attemptCount++
                    delay(2000)
                }
            }
        } catch (_: Exception) {
            onError()
            return
        }
    }

    onTimeout()
}

private suspend fun fetchLoginCode(): String {
    val httpClient = HttpClient(CIO)
    try {
        val response: HttpResponse = httpClient.get("https://id.strawberryfoundations.org/v2/api/request")
        return response.bodyAsText()
    } finally {
        httpClient.close()
    }
}

private suspend fun fetchUserDataWithCode(code: String): AuthResponse? {
    val httpClient = HttpClient(CIO)
    try {
        val callbackUrl = "https://id.strawberryfoundations.org/v2/api/oauth/callback?code=$code"
        val response: HttpResponse = httpClient.get(callbackUrl)
        val responseBody = response.bodyAsText()

        val json = JSONObject(responseBody)
        val dataObject = json.getJSONObject("data")
        val status = dataObject.getString("status")

        if (status == "Invalid Code" || status == "Not authenticated") {
            return AuthResponse(status, null)
        }

        val userJson = dataObject.getJSONObject("user")
        val user = UserPreferences(
            username = userJson.optString("username"),
            email = userJson.optString("email"),
            fullName = userJson.optString("full_name"),
            profilePictureUrl = userJson.optString("profile_picture_url"),
            token = userJson.optString("token")
        )
        return AuthResponse(status, user)
    } catch (_: Exception) {
        return null
    } finally {
        httpClient.close()
    }
}

private data class AuthResponse(val status: String, val user: UserPreferences?)