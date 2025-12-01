package com.example.palcompanion.data

import kotlinx.serialization.Serializable

@Serializable
data class Breeding(
    val parent1: String,
    val parent2: String,
    val child: String
)