package com.example.palcompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_breeding_trees")
data class SavedBreedingTree(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val rootPalName: String,
    val treeJson: String // Store the tree as a JSON string
)
