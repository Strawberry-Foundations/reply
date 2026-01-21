package org.strawberryfoundations.reply.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DataSyncRequestor {
    private const val REQUEST_SYNC_PATH = "/request-sync"
    
    fun requestSyncFromWearable(context: Context, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nodeClient = Wearable.getNodeClient(context)
                nodeClient.connectedNodes
                    .addOnSuccessListener { nodes ->
                        if (nodes.isEmpty()) {
                            Log.w("DataSyncRequestor", "No connected wearable devices found")
                            onFailure(Exception("No connected wearable devices"))
                            return@addOnSuccessListener
                        }
                        
                        val messageClient = Wearable.getMessageClient(context)
                        
                        nodes.forEach { node ->
                            messageClient.sendMessage(
                                node.id,
                                REQUEST_SYNC_PATH,
                                "sync-request".toByteArray()
                            )
                                .addOnSuccessListener {
                                    Log.i("DataSyncRequestor", "Sync request sent to ${node.displayName}")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("DataSyncRequestor", "Failed to send sync request to ${node.displayName}", e)
                                }
                        }
                        
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("DataSyncRequestor", "Failed to get connected nodes", e)
                        onFailure(e)
                    }
            } catch (e: Exception) {
                Log.e("DataSyncRequestor", "Failed to request sync from wearable", e)
                onFailure(e)
            }
        }
    }
}
