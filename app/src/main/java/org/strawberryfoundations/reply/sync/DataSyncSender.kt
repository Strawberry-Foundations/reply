package org.strawberryfoundations.reply.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.strawberryfoundations.reply.core.model.DbSnapshot
import org.strawberryfoundations.reply.core.SettingsDataStore
import org.strawberryfoundations.reply.room.entities.Exercise
import org.strawberryfoundations.reply.room.entities.WorkoutSession

object DataSyncSender {
    fun sendDbSnapshot(context: Context, trainings: List<Exercise>, sessions: List<WorkoutSession>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = DbSnapshot(exercises = trainings, workoutSessions = sessions)
                val json = Json.encodeToString(DbSnapshot.serializer(), snapshot)
                val bytes = json.toByteArray(Charsets.UTF_8)
                val asset = Asset.createFromBytes(bytes)

                val putDataMapReq = PutDataMapRequest.create("/db-sync")
                putDataMapReq.dataMap.putLong("syncTime", System.currentTimeMillis())
                putDataMapReq.dataMap.putAsset("dbAsset", asset)
                val request = putDataMapReq.asPutDataRequest().setUrgent()

                Wearable.getDataClient(context).putDataItem(request)
                    .addOnSuccessListener { dataItem ->
                        Log.i("DataSyncSender", "Successfully sent DB snapshot: $dataItem")
                        // Update last sync time
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val settings = SettingsDataStore(context)
                                settings.updateSettings { it.copy(lastSync = System.currentTimeMillis()) }
                            } catch (e: Exception) {
                                Log.w("DataSyncSender", "Failed to update lastSync", e)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("DataSyncSender", "Failed to send DB snapshot", e)
                    }
            } catch (e: Exception) {
                Log.e("DataSyncSender", "Failed to serialize/send snapshot", e)
            }
        }
    }
}
