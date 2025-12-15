package com.example.palcompanion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedBreedingTreeDao {
    @Insert
    suspend fun insert(tree: SavedBreedingTree)

    @Query("SELECT * FROM saved_breeding_trees ORDER BY rootPalName ASC")
    fun getAll(): Flow<List<SavedBreedingTree>>

    @Query("DELETE FROM saved_breeding_trees WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)
}
