package org.strawberryfoundations.replicity.ui.views

import android.annotation.SuppressLint
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import org.strawberryfoundations.replicity.R
import org.strawberryfoundations.replicity.core.AvatarCache
import org.strawberryfoundations.replicity.core.getUserDataFlow
import org.strawberryfoundations.replicity.core.saveUserData
import org.strawberryfoundations.replicity.core.model.UserPreferences
import org.strawberryfoundations.replicity.ui.theme.ascenderHeight
import org.strawberryfoundations.replicity.ui.theme.counterWidth
import org.strawberryfoundations.replicity.ui.theme.font.GoogleSansFlex


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
            } else {
                userData = null
                isLoggedIn = false
            }
        } catch (_: Exception) {
            userData = null
            isLoggedIn = false
        }
    }

    Scaffold { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
                            currentLoginCode = null
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
                                errorMessage = context.getString(
                                    R.string.login_code_error,
                                    e.message ?: e.toString()
                                )
                            }
                            isLoading = false
                        }
                    },
                    onCancelLogin = {
                        polling = false
                        currentLoginCode = null
                        errorMessage = null
                    }
                )
            }
        }
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
                    errorMessage = null
                },
                onInvalidCode = {
                    errorMessage = context.getString(R.string.login_code_invalid)
                    polling = false
                },
                onTimeout = {
                    errorMessage = context.getString(R.string.login_timeout)
                    polling = false
                },
                onError = {
                    errorMessage = context.getString(R.string.connection_error)
                    polling = false
                }
            )
        }
    }
}

@Composable
private fun LoggedInContent(
    userData: UserPreferences,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val imageLoader = remember { AvatarCache.getImageLoader(context) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(userData.profilePictureUrl)
                        .memoryCacheKey(userData.username) // Eindeutiger Cache-Key
                        .diskCacheKey(userData.username)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.profile_picture),
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_launcher),
                    error = painterResource(R.drawable.ic_launcher)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = userData.fullName,
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "@${userData.username}",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Account Information Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.account_information),
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                ProfileInfoRow(
                    label = stringResource(R.string.email_label).replace($$"%1$s", ""),
                    value = userData.email
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                ProfileInfoRow(
                    label = stringResource(R.string.strawberry_id).replace($$"%1$s", ""),
                    value = userData.username
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Settings Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                ProfileSettingsItem(
                    title = stringResource(R.string.privacy),
                    subtitle = stringResource(R.string.privacy_settings_subtitle),
                    onClick = { }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                )

                ProfileSettingsItem(
                    title = stringResource(R.string.strawberry_cloud),
                    subtitle = stringResource(R.string.strawberry_cloud_subtitle),
                    onClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Sign Out Button
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = stringResource(R.string.sign_out),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ProfileSettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.Forward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalTextApi::class)
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
            contentDescription = stringResource(R.string.strawberry_id_logo),
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = "Beta",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.sign_in_with_strawberry_id),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            enabled = !isLoading && !polling,
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Text(
                    text = when {
                        polling -> stringResource(R.string.waiting_for_login)
                        else -> stringResource(R.string.sign_in_button)
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        if (polling) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onCancelLogin) {
                Text(stringResource(R.string.cancel_login))
            }
        }

        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
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