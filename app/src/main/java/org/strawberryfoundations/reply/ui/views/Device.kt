package org.strawberryfoundations.reply.ui.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.DeviceHub
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.NearMeDisabled
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.core.AppSettings
import org.strawberryfoundations.reply.database.AppDatabase
import org.strawberryfoundations.reply.database.ExerciseViewModel
import org.strawberryfoundations.reply.sync.DataSyncSender
import org.strawberryfoundations.reply.ui.theme.customFont
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("LocalContextGetResourceValueCall", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DeviceView(
    settings: AppSettings,
    viewModel: ExerciseViewModel = viewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val workoutCount = viewModel.trainings.collectAsState().value.size

    val isSending = remember { mutableStateOf(false) }
    val isLoadingNodes = remember { mutableStateOf(false) }
    val nodesState = remember { mutableStateOf<List<Node>>(emptyList()) }
    val lastSync = remember { mutableStateOf<String?>(null) }
    val pullState = rememberPullToRefreshState()

    LaunchedEffect( Unit ) {
        scope.launch {
            isLoadingNodes.value = true
            try {
                val nodeClient = Wearable.getNodeClient(context)
                nodeClient.connectedNodes
                    .addOnSuccessListener { nodes ->
                        nodesState.value = nodes
                        isLoadingNodes.value = false
                    }
                    .addOnFailureListener { e ->
                        isLoadingNodes.value = false
                        Log.w("DeviceView", "Failed listing nodes", e)
                    }
            } catch (e: Exception) {
                isLoadingNodes.value = false
                Log.w("DeviceView", "Error while checking nodes", e)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { _ ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = isLoadingNodes.value,
            onRefresh = {
                scope.launch {
                    isLoadingNodes.value = true
                    try {
                        val nodeClient = Wearable.getNodeClient(context)
                        nodeClient.connectedNodes
                            .addOnSuccessListener { nodes ->
                                nodesState.value = nodes
                                isLoadingNodes.value = false
                                scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.found_nodes, nodes.size)) }
                            }
                            .addOnFailureListener { e ->
                                isLoadingNodes.value = false
                                Log.w("DeviceView", "Failed listing nodes", e)
                                scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.failed_listing_nodes, e.message ?: "")) }
                            }
                    } catch (e: Exception) {
                        isLoadingNodes.value = false
                        Log.w("DeviceView", "Error while checking nodes", e)
                        scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.error_colon, e.message ?: "")) }
                    }
                }
            },
            state = pullState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullState,
                    isRefreshing = isLoadingNodes.value,
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp)
            ) {

                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Statistics
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BarChart,
                            contentDescription = stringResource(id = R.string.statistics),
                            tint = MaterialTheme.colorScheme.primary,
                        )

                        Text(
                            text = stringResource(id = R.string.statistics),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.workouts),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                                Text(
                                    text = workoutCount.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }


                item { Spacer(modifier = Modifier.height(12.dp)) }

                // Device & Sync
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Sync,
                            contentDescription = stringResource(id = R.string.device_sync_title),
                            tint = MaterialTheme.colorScheme.primary,
                        )

                        Text(
                            text = stringResource(id = R.string.device_sync_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(4.dp)) }

                item {
                    Text(
                        text = stringResource(id = R.string.device_sync_description),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 16.sp,
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                // Device & Sync statistics
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Rounded.Info, contentDescription = stringResource(id = R.string.info))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = R.string.device_status),
                                    style = MaterialTheme.typography.displayMedium,
                                    fontSize = 17.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.last_db_sync),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 15.sp,
                                )
                                Text(
                                    text = lastSync.value ?: stringResource(id = R.string.never),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 15.sp,
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.nodes_discovered),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 15.sp,
                                )
                                Text(
                                    text = "${nodesState.value.size}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 15.sp,
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                // Actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ButtonGroup(
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isSending.value = true
                                        try {
                                            val dao = AppDatabase.getInstance(context).trainingDao()
                                            val trainings = withContext(Dispatchers.IO) { dao.getAll().first() }
                                            if (trainings.isNotEmpty()) {
                                                DataSyncSender.sendDbSnapshot(context, trainings)
                                                val fmt = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
                                                lastSync.value = fmt.format(Date())
                                                scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.db_snapshot_queued)) }
                                            } else {
                                                scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.no_trainings_to_send)) }
                                            }
                                        } catch (e: Exception) {
                                            Log.w("DeviceView", "Error sending DB snapshot", e)
                                            scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.send_failed, e.message ?: "")) }
                                        } finally {
                                            isSending.value = false
                                        }
                                    }
                                }) {
                                Icon(
                                    imageVector = Icons.Rounded.Sync,
                                    contentDescription = stringResource(id = R.string.send),
                                    modifier = Modifier.padding(end = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = R.string.synchronize),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        isLoadingNodes.value = true
                                        try {
                                            val nodeClient = Wearable.getNodeClient(context)
                                            nodeClient.connectedNodes
                                                .addOnSuccessListener { nodes ->
                                                    nodesState.value = nodes
                                                    isLoadingNodes.value = false
                                                    scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.found_nodes, nodes.size)) }
                                                }
                                                .addOnFailureListener { e ->
                                                    isLoadingNodes.value = false
                                                    Log.w("DeviceView", "Failed listing nodes", e)
                                                    scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.failed_listing_nodes, e.message ?: "")) }
                                                }
                                        } catch (e: Exception) {
                                            isLoadingNodes.value = false
                                            Log.w("DeviceView", "Error while checking nodes", e)
                                            scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.error_colon, e.message ?: "")) }
                                        }
                                    }
                                }) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = stringResource(id = R.string.device_sync_title),
                                    modifier = Modifier.padding(end = 2.dp)
                                )
                            }
                        }

                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                // Connected nodes
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeviceHub,
                            contentDescription = stringResource(id = R.string.connected_nodes),
                            tint = MaterialTheme.colorScheme.primary,
                        )

                        Text(
                            text = stringResource(id = R.string.connected_nodes),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(8.dp)) }

                if (nodesState.value.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.no_connected_nodes_found),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(nodesState.value) { node ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    scope.launch { snackbarHostState.showSnackbar(node.id) }
                                },
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Watch,
                                        contentDescription = stringResource(id = R.string.info),
                                        modifier = Modifier.padding(end = 8.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = node.displayName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontSize = 15.sp,
                                            maxLines = 1,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = node.id,
                                            style = customFont.numeralMedium,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (node.isNearby) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (node.isNearby) Icons.Rounded.NearMe else Icons.Rounded.NearMeDisabled,
                                                contentDescription = stringResource(id = R.string.info),
                                                modifier = Modifier.padding(end = 8.dp)
                                            )

                                            Text(
                                                text = if (node.isNearby) stringResource(R.string.connected) else stringResource(R.string.not_connected),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 13.sp,
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