package com.example.sivanandareddyapplication.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RandomTextDao {
    @Query("SELECT * FROM random_text ORDER BY id DESC")
    fun observeAll(): Flow<List<RandomTextEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RandomTextEntity)

    @Query("DELETE FROM random_text")
    suspend fun deleteAll()

    @Query("DELETE FROM random_text WHERE id = :id")
    suspend fun deleteById(id: Long)
}
