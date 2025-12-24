package org.strawberryfoundations.reply.ui.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.database.AppDatabase
import org.strawberryfoundations.reply.sync.DataSyncSender
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun DeviceView() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val isSending = remember { mutableStateOf(false) }
    val isLoadingNodes = remember { mutableStateOf(false) }
    val nodesState = remember { mutableStateOf<List<Node>>(emptyList()) }
    val lastSync = remember { mutableStateOf<String?>(null) }
    val pullState = rememberPullToRefreshState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
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
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = stringResource(id = R.string.info))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = R.string.device_status),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = stringResource(id = R.string.last_db_sync), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                Text(text = lastSync.value ?: stringResource(id = R.string.never), style = MaterialTheme.typography.bodySmall)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = stringResource(id = R.string.nodes_discovered), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                Text(text = "${nodesState.value.size}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                item {
                    // Actions
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onClick = {
                            scope.launch {
                                isSending.value = true
                                try {
                                    val dao = AppDatabase.getInstance(context).trainingDao()
                                    val trainings = withContext(Dispatchers.IO) { dao.getAll().first() }
                                    if (trainings.isNotEmpty()) {
                                        DataSyncSender.sendDbSnapshot(context, trainings)
                                        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
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
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = stringResource(id = R.string.send),
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(id = R.string.send_db_snapshot), style = MaterialTheme.typography.labelLarge)
                        }

                        if (isSending.value || isLoadingNodes.value) {
                            Spacer(modifier = Modifier.width(8.dp))
                            LoadingIndicator(modifier = Modifier.align(Alignment.CenterVertically))
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                item { HorizontalDivider() }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                item {
                    // Node list
                    Text(
                        text = stringResource(id = R.string.connected_nodes),
                        style = MaterialTheme.typography.titleMedium
                    )
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
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Watch,
                                        contentDescription = stringResource(id = R.string.info),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(
                                        text = node.displayName,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ID:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = node.id, style = MaterialTheme.typography.bodySmall)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Nearby:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = node.isNearby.toString(), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}