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
        Log.i("DataSyncReceiver", "onDataChanged called with ${dataEvents.count} events")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                Log.i("DataSyncReceiver", "Data changed event for path: $path")
                // Empfange Daten vom Handy oder von der Wearable
                if (path == "/db-sync" || path == "/db-sync-from-wearable") {
                    Log.i("DataSyncReceiver", "Processing sync data from path: $path")
                    val item = DataMapItem.fromDataItem(event.dataItem)
                    val asset = item.dataMap.getAsset("dbAsset")
                    if (asset == null) {
                        Log.w("DataSyncReceiver", "No asset found in data item")
                        continue
                    }

                    Wearable.getDataClient(this).getFdForAsset(asset)
                        .addOnSuccessListener { result ->
                            Log.i("DataSyncReceiver", "Got asset file descriptor, reading data...")
                            scope.launch {
                                try {
                                    result.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                                        val json = reader.readText()
                                        Log.i("DataSyncReceiver", "Read JSON data: ${json.length} bytes")
                                        val snapshot: DbSnapshot = Json.decodeFromString(json)
                                        Log.i("DataSyncReceiver", "Decoded snapshot: ${snapshot.exercises.size} exercises, ${snapshot.workoutSessions.size} sessions")
                                        
                                        val db = AppDatabase.getInstance(applicationContext)
                                        val exerciseDao = db.trainingDao()
                                        val sessionDao = db.workoutSessionDao()
                                        
                                        // Verwende insert mit onConflict = REPLACE statt update
                                        var exerciseCount = 0
                                        for (exercise in snapshot.exercises) {
                                            try {
                                                exerciseDao.insert(exercise)
                                                exerciseCount++
                                            } catch (e: Exception) {
                                                // Falls insert fehlschlägt, versuche update
                                                try {
                                                    exerciseDao.update(exercise)
                                                    exerciseCount++
                                                } catch (e2: Exception) {
                                                    Log.e("DataSyncReceiver", "Failed to insert/update exercise ${exercise.id}", e2)
                                                }
                                            }
                                        }
                                        
                                        var sessionCount = 0
                                        for (session in snapshot.workoutSessions) {
                                            try {
                                                sessionDao.insert(session)
                                                sessionCount++
                                            } catch (e: Exception) {
                                                // Falls insert fehlschlägt, versuche update
                                                try {
                                                    sessionDao.update(session)
                                                    sessionCount++
                                                } catch (e2: Exception) {
                                                    Log.e("DataSyncReceiver", "Failed to insert/update session ${session.id}", e2)
                                                }
                                            }
                                        }
                                        
                                        val source = if (path == "/db-sync-from-wearable") "Wearable" else "Phone"
                                        Log.i("DataSyncReceiver", "Successfully applied $exerciseCount exercises and $sessionCount sessions from $source")
                                    }
                                } catch (e: Exception) {
                                    Log.e("DataSyncReceiver", "Failed to apply snapshot", e)
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("DataSyncReceiver", "Failed getting asset fd", e)
                        }
                } else {
                    Log.d("DataSyncReceiver", "Ignoring path: $path")
                }
            }
        }
    }
}
