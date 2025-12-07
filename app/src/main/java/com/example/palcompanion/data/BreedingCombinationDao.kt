package com.example.palcompanion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedingCombinationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(combinations: List<BreedingCombination>)

    @Query("SELECT * FROM breeding_combinations ORDER BY parent1 ASC, parent2 ASC")
    fun getAll(): Flow<List<BreedingCombination>>

    @Query("SELECT * FROM breeding_combinations WHERE child = :childName")
    fun findByChildName(childName: String): Flow<List<BreedingCombination>>
}
