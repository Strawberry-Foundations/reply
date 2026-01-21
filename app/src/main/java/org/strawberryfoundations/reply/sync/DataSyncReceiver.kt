package org.strawberryfoundations.reply.sync

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.strawberryfoundations.reply.core.model.DbSnapshot
import org.strawberryfoundations.reply.room.AppDatabase


class DataSyncReceiver : WearableListenerService() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/db-sync") {
                    val item = DataMapItem.fromDataItem(event.dataItem)
                    val asset = item.dataMap.getAsset("dbAsset") ?: continue

                    Wearable.getDataClient(this).getFdForAsset(asset)
                        .addOnSuccessListener { result ->
                            scope.launch {
                                try {
                                    result.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                                        val json = reader.readText()
                                        val snapshot: DbSnapshot = Json.decodeFromString(json)
                                        val db = AppDatabase.getInstance(applicationContext)
                                        val exerciseDao = db.trainingDao()
                                        val sessionDao = db.workoutSessionDao()
                                        
                                        for (exercise in snapshot.exercises) {
                                            exerciseDao.insert(exercise)
                                        }
                                        for (session in snapshot.workoutSessions) {
                                            sessionDao.insert(session)
                                        }
                                        
                                        Log.i("DataSyncReceiver", "Applied ${snapshot.exercises.size} exercises and ${snapshot.workoutSessions.size} sessions from sync")
                                    }
                                } catch (e: Exception) {
                                    Log.e("DataSyncReceiver", "Failed to apply snapshot", e)
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("DataSyncReceiver", "Failed getting asset fd", e)
                        }
                }
            }
        }
    }
}
