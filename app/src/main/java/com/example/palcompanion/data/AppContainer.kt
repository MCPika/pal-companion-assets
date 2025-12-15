@file:OptIn(InternalSerializationApi::class)

package com.example.palcompanion.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.palcompanion.data.repository.BreedingRepository
import com.example.palcompanion.data.repository.DefaultBreedingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface AppContainer {
    val breedingRepository: BreedingRepository
}

@Serializable
private data class ParentPair(val parent1: String, val parent2: String)

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "pal_companion.db"
        )
        .addMigrations(MIGRATION_1_2)
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val jsonString = context.assets.open("breeding.json").bufferedReader().use { it.readText() }
                        val json = Json { ignoreUnknownKeys = true }
                        val breedingMap = json.decodeFromString<Map<String, List<ParentPair>>>(jsonString)
                        val breedingList = mutableListOf<BreedingCombination>()
                        breedingMap.forEach { (child, parentPairs) ->
                            parentPairs.forEach { pair ->
                                breedingList.add(
                                    BreedingCombination(
                                        child = child,
                                        parent1 = pair.parent1,
                                        parent2 = pair.parent2
                                    )
                                )
                            }
                        }
                        database.breedingCombinationDao().insertAll(breedingList)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
        .setQueryExecutor(Dispatchers.IO.asExecutor())
        .build()
    }

    override val breedingRepository: BreedingRepository by lazy {
        DefaultBreedingRepository(database.breedingCombinationDao(), database.savedBreedingTreeDao())
    }

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `saved_breeding_trees` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `rootPalName` TEXT NOT NULL, `treeJson` TEXT NOT NULL)")
            }
        }
    }
}
