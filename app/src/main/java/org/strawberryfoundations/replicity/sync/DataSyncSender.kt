package org.strawberryfoundations.replicity.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.strawberryfoundations.replicity.core.model.Training


object DataSyncSender {
    fun sendDbSnapshot(context: Context, trainings: List<Training>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = Json.encodeToString(ListSerializer(Training.serializer()), trainings)
                val bytes = json.toByteArray(Charsets.UTF_8)
                val asset = Asset.createFromBytes(bytes)

                val putDataMapReq = PutDataMapRequest.create("/db-sync")
                putDataMapReq.dataMap.putLong("syncTime", System.currentTimeMillis())
                putDataMapReq.dataMap.putAsset("dbAsset", asset)
                val request = putDataMapReq.asPutDataRequest().setUrgent()

                Wearable.getDataClient(context).putDataItem(request)
                    .addOnSuccessListener { dataItem ->
                        Log.i("DataSyncSender", "Successfully sent DB snapshot: $dataItem")
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
