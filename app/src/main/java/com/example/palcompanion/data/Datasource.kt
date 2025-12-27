package com.example.palcompanion.data

import android.content.Context
import android.util.Log
import com.example.palcompanion.Constants
import com.example.palcompanion.model.ActiveSkill
import com.example.palcompanion.model.Drop
import com.example.palcompanion.model.Filter
import com.example.palcompanion.model.Pal
import com.example.palcompanion.model.PalElement
import com.example.palcompanion.model.PalWorkSuitability
import com.example.palcompanion.model.PartnerSkill
import com.example.palcompanion.model.WorkSuitability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class Datasource(private val context: Context) {

    private val palImageUrlBase = Constants.PALS_IMAGE_URL

    suspend fun loadPals(language: String): List<Pal> = withContext(Dispatchers.IO) {
        val pals = mutableListOf<Pal>()
        try {
            val jsonUrl = if (language == "fr") Constants.PALS_FR_JSON_URL else Constants.PALS_EN_JSON_URL
            Log.d("Datasource", "Loading pals from: $jsonUrl")
            val jsonString = URL(jsonUrl).readText()
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.optString("id")
                val name = jsonObject.optString("name")

                val imageUrl = "$palImageUrlBase/${name.lowercase().replace(" ", "_")}.webp"

                val elements = mutableListOf<PalElement>()
                val palElementKey = "palElement"
                try {
                    when (val elementsJson = jsonObject.opt(palElementKey)) {
                        is JSONArray -> {
                            for (j in 0 until elementsJson.length()) {
                                PalElement.fromString(elementsJson.optString(j))?.let { elements.add(it) }
                            }
                        }
                        is String -> {
                            PalElement.fromString(elementsJson)?.let { elements.add(it) }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Datasource", "Error parsing elements for pal: $name", e)
                }

                val workSuitabilitiesArray = jsonObject.optJSONArray("workSuitabilities") ?: JSONArray()
                val workSuitabilities = mutableListOf<PalWorkSuitability>()
                for (j in 0 until workSuitabilitiesArray.length()) {
                    val workObject = workSuitabilitiesArray.getJSONObject(j)
                    try {
                        val workName = workObject.optString("work")
                        WorkSuitability.fromString(workName)?.let {
                            workSuitabilities.add(
                                PalWorkSuitability(
                                    it,
                                    workObject.getInt("level")
                                )
                            )
                        } ?: Log.w("Datasource", "Unknown work suitability '$workName' for pal: $name")
                    } catch (e: Exception) {
                        Log.e("Datasource", "Error parsing work suitability for pal: $name", e)
                    }
                }

                val partnerSkillObject = jsonObject.optJSONObject("partnerSkill")
                val partnerSkill = if (partnerSkillObject != null) {
                    PartnerSkill(
                        name = partnerSkillObject.optString("name"),
                        description = partnerSkillObject.optString("description")
                    )
                } else {
                    PartnerSkill("", "")
                }

                val dropsArray = jsonObject.optJSONArray("drops") ?: JSONArray()
                val drops = mutableListOf<Drop>()
                for (j in 0 until dropsArray.length()) {
                    val dropObject = dropsArray.getJSONObject(j)
                    val dropName = dropObject.optString("name")
                    if (dropObject.has("special")) {
                        drops.add(Drop(name = dropName, special = dropObject.optString("special")))
                    } else {
                        drops.add(
                            Drop(
                                name = dropName,
                                quantity = dropObject.optString("quantity"),
                                rate = dropObject.optString("rate")
                            )
                        )
                    }
                }

                val activeSkillsArray = jsonObject.optJSONArray("activeSkills") ?: JSONArray()
                val activeSkills = mutableListOf<ActiveSkill>()
                val skillElementKey = "skillElement"
                for (j in 0 until activeSkillsArray.length()) {
                    val skillObject = activeSkillsArray.getJSONObject(j)
                    try {
                        val skillElementName = skillObject.optString(skillElementKey)
                        PalElement.fromString(skillElementName)?.let {
                            activeSkills.add(
                                ActiveSkill(
                                    level = skillObject.optInt("level"),
                                    name = skillObject.optString("name"),
                                    description = skillObject.optString("description"),
                                    cooldown = skillObject.optInt("cooldown"),
                                    power = skillObject.optInt("power"),
                                    element = it
                                )
                            )
                        } ?: Log.w("Datasource", "Unknown skill element '$skillElementName' for pal: $name")
                    } catch (e: Exception) {
                        Log.e("Datasource", "Error parsing active skill for pal: $name", e)
                    }
                }

                pals.add(
                    Pal(
                        id = id,
                        name = name,
                        elements = elements,
                        imageUrl = imageUrl,
                        workSuitability = workSuitabilities,
                        description = jsonObject.optString("description"),
                        partnerSkill = partnerSkill,
                        drops = drops,
                        activeSkills = activeSkills
                    )
                )
            }
        } catch (ioException: IOException) {
            Log.e("Datasource", "Failed to load pals", ioException)
        } catch (jsonException: JSONException) {
            Log.e("Datasource", "Failed to parse pals JSON", jsonException)
        }
        return@withContext pals
    }

    fun loadWorkSuitabilityFilters(): List<Filter> {
        return WorkSuitability.entries.map { 
            Filter(
                name = it.name.lowercase().replaceFirstChar { char -> char.uppercase() },
                iconUrl = it.iconUrl,
                workSuitability = it
            )
        } + Filter("Cancel", Constants.CANCEL_ICON_URL)
    }

    fun loadPalTypeFilters(): List<Filter> {
        return PalElement.entries.map { 
            Filter(
                name = it.name.lowercase().replaceFirstChar { char -> char.uppercase() },
                iconUrl = it.iconIcUrl,
                palElement = it
            )
        } + Filter("Cancel", Constants.CANCEL_ICON_URL)
    }

    fun loadJobLevelFilters(): List<Filter> {
        return listOf(
            Filter("Cancel", Constants.CANCEL_ICON_URL),
        )
    }
}
