package com.example.sivanandareddyapplication.data

import com.example.sivanandareddyapplication.data.local.RandomTextDao
import com.example.sivanandareddyapplication.data.local.RandomTextEntity
import com.example.sivanandareddyapplication.data.provider.IavProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant

class RandomTextRepository(
    private val dao: RandomTextDao,
    private val provider: IavProviderClient
) {

    // Expose items as a StateFlow that emits the latest list of RandomTextEntity
    val items: StateFlow<List<RandomTextEntity>> = dao.observeAll()
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Fetch random text from provider and save into DB
    suspend fun fetchAndSave(maxLength: Int): Result<Unit> {
        return runCatching {
            val res = provider.requestRandom(maxLength)
            val dto = res.getOrThrow().randomText
            val createdEpoch = try {
                Instant.parse(dto.created).toEpochMilli()
            } catch (_: Exception) {
                System.currentTimeMillis()
            }

            dao.insert(
                RandomTextEntity(
                    value = dto.value,
                    requestedLength = maxLength,
                    providerLength = dto.length,
                    createdIso = dto.created,
                    createdEpochMillis = createdEpoch
                )
            )
        }
    }

    // Delete all items
    suspend fun deleteAll() = dao.deleteAll()

    // Delete one item by ID
    suspend fun deleteOne(id: Long) = dao.deleteById(id)
}
