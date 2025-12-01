package com.example.palcompanion.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BreedingCombination::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun breedingCombinationDao(): BreedingCombinationDao
}
