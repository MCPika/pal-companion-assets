package com.example.palcompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breeding_combinations")
data class BreedingCombination(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val child: String,
    val parent1: String,
    val parent2: String
)
