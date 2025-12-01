package com.example.palcompanion.data.repository

import com.example.palcompanion.data.Breeding
import com.example.palcompanion.data.BreedingCombination
import com.example.palcompanion.data.BreedingCombinationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface BreedingRepository {
    fun getBreedingCombinations(): Flow<List<Breeding>>
    fun getBreedingCombinationsForChild(childName: String): Flow<List<Breeding>>
}

class DefaultBreedingRepository(private val breedingCombinationDao: BreedingCombinationDao) : BreedingRepository {
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
}

private fun BreedingCombination.toBreeding(): Breeding {
    return Breeding(
        parent1 = this.parent1,
        parent2 = this.parent2,
        child = this.child
    )
}
