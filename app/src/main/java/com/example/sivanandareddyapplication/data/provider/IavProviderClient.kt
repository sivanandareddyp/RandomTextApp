package com.example.sivanandareddyapplication.data.provider

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import com.example.sivanandareddyapplication.data.model.RandomEnvelope

class IavProviderClient(private val contentResolver: ContentResolver) {

    companion object {
        private const val TAG = "IavProviderClient"
        private const val MAX_RETRIES = 3
        private const val INITIAL_DELAY_MS = 400L
        private const val TIMEOUT_MS = 8000L
    }

    private val baseUri: Uri = Uri.parse("content://com.iav.contestdataprovider/text")
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Requests a random string from the content provider.
     * @param maxLength maximum length of string requested
     * @return Result wrapping RandomEnvelope or exception
     */
    suspend fun requestRandom(maxLength: Int): Result<RandomEnvelope> {

        val extras = Bundle().apply { putInt(ContentResolver.QUERY_ARG_LIMIT, 1) }
        val uri = baseUri.buildUpon().appendQueryParameter("length", maxLength.toString()).build()

        var delayMs = INITIAL_DELAY_MS

        repeat(MAX_RETRIES) { attempt ->
            try {
                Log.d(TAG, "Attempt ${attempt + 1}: querying provider...")
                val res = withTimeoutOrNull(TIMEOUT_MS) {
                    queryOnce(uri, extras)
                }
                if (res != null) {
                    Log.d(TAG, "Provider success: $res")
                    return Result.success(res)
                } else {
                    Log.w(TAG, "Provider timeout on attempt ${attempt + 1}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Provider error on attempt ${attempt + 1}: ${e.message}")
            }
            delay(delayMs)
            delayMs *= 2
        }

        return Result.failure(IllegalStateException("Provider failed after $MAX_RETRIES retries"))
    }

    /**
     * Queries the content provider once and returns the parsed RandomEnvelope.
     */
    private fun queryOnce(uri: Uri, extras: Bundle): RandomEnvelope {
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(uri, null, extras, null)
            if (cursor == null) throw IllegalStateException("Cursor is null")
            Log.d(TAG, "Cursor count: ${cursor.count}")

            if (!cursor.moveToFirst()) throw IllegalStateException("No data returned")

            val dataIdx = cursor.getColumnIndex("data")
            if (dataIdx == -1) throw IllegalStateException("Missing column 'data'")

            val jsonString = cursor.getString(dataIdx)
                ?: throw IllegalStateException("Column 'data' is null")

            Log.d(TAG, "Raw JSON: $jsonString")

            return json.decodeFromString<RandomEnvelope>(jsonString)

        } finally {
            cursor?.close()
        }
    }
}

/**
 * Custom timeout exception (optional)
 */
class TimeoutException(message: String) : Exception(message)
