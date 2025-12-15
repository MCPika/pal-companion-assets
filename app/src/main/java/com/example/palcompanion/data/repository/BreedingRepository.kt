package com.example.palcompanion.data.repository

import com.example.palcompanion.data.Breeding
import com.example.palcompanion.data.BreedingCombination
import com.example.palcompanion.data.BreedingCombinationDao
import com.example.palcompanion.data.SavedBreedingTree
import com.example.palcompanion.data.SavedBreedingTreeDao
import com.example.palcompanion.ui.breeds.PalNode
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface BreedingRepository {
    fun getBreedingCombinations(): Flow<List<Breeding>>
    fun getBreedingCombinationsForChild(childName: String): Flow<List<Breeding>>
    fun getSavedTrees(): Flow<List<SavedBreedingTree>>
    suspend fun saveTree(rootNode: PalNode)
    suspend fun deleteTrees(ids: List<Int>)
}

class DefaultBreedingRepository(
    private val breedingCombinationDao: BreedingCombinationDao,
    private val savedBreedingTreeDao: SavedBreedingTreeDao
) : BreedingRepository {
    override fun getBreedingCombinations(): Flow<List<Breeding>> {
        return breedingCombinationDao.getAll().map { combinations ->
            combinations.map { it.toBreeding() }
        }
    }

    override fun getBreedingCombinationsForChild(childName: String): Flow<List<Breeding>> {
        return breedingCombinationDao.findByChildName(childName).map { combinations ->
            combinations.map { it.toBreeding() }
        }
    }

    override fun getSavedTrees(): Flow<List<SavedBreedingTree>> {
        return savedBreedingTreeDao.getAll()
    }

    override suspend fun saveTree(rootNode: PalNode) {
        val treeJson = Gson().toJson(rootNode)
        val savedTree = SavedBreedingTree(rootPalName = rootNode.palName, treeJson = treeJson)
        savedBreedingTreeDao.insert(savedTree)
    }

    override suspend fun deleteTrees(ids: List<Int>) {
        savedBreedingTreeDao.deleteByIds(ids)
    }
}

private fun BreedingCombination.toBreeding(): Breeding {
    return Breeding(
        parent1 = this.parent1,
        parent2 = this.parent2,
        child = this.child
    )
}
