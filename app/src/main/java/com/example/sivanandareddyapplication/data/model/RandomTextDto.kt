package com.example.sivanandareddyapplication.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RandomEnvelope(
    @SerialName("randomText") val randomText: RandomTextDto
)

@Serializable
data class RandomTextDto(
    val value: String,
    val length: Int,
    val created: String
)

