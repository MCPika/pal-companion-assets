package com.example.palcompanion.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BreedingCombination::class, SavedBreedingTree::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun breedingCombinationDao(): BreedingCombinationDao
    abstract fun savedBreedingTreeDao(): SavedBreedingTreeDao
}
