package com.example.sivanandareddyapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "random_text")
data class RandomTextEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val value: String,
    val requestedLength: Int,
    val providerLength: Int,
    val createdIso: String,
    val createdEpochMillis: Long
)

