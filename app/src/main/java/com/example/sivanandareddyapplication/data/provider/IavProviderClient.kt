package com.example.sivanandareddyapplication.data.provider


import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import com.example.sivanandareddyapplication.data.model.RandomEnvelope

class IavProviderClient(private val contentResolver: ContentResolver) {

    private val baseUri: Uri = Uri.parse("content://com.iav.contestdataprovider/text")
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun requestRandom(maxLength: Int): Result<RandomEnvelope> {
        val attempts = listOf(
            baseUri.buildUpon().appendQueryParameter("length", maxLength.toString()).build() to Bundle(),
            baseUri.buildUpon().appendQueryParameter("maxLength", maxLength.toString()).build() to Bundle(),
            baseUri to Bundle().apply { putInt("length", maxLength) },
            baseUri to Bundle()
        )
        attempts.forEach { (_, bundle) -> bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 1) }

        var delayMs = 400L
        repeat(3) { attempt ->
            val (uri, extras) = attempts.getOrElse(attempt) { attempts.last() }
            val res = withTimeoutOrNull(4000) {
                runCatching { queryOnce(uri, extras) }
            } ?: Result.failure(TimeoutException("Provider timeout"))
            if (res.isSuccess) return res
            delay(delayMs)
            delayMs *= 2
        }
        return Result.failure(IllegalStateException("Provider failed after retries"))
    }

    private fun queryOnce(uri: Uri, extras: Bundle): RandomEnvelope {
        val cursor: Cursor? = contentResolver.query(uri, null, extras, null)
        cursor.use { c ->
            if (c == null || !c.moveToFirst()) throw IllegalStateException("No data")
            val dataIdx = c.getColumnIndex("data")
            if (dataIdx == -1) throw IllegalStateException("Missing column 'data'")
            val jsonString = c.getString(dataIdx) ?: throw IllegalStateException("Null data")
            return json.decodeFromString(jsonString)
        }
    }
}

class TimeoutException(message: String) : Exception(message)
