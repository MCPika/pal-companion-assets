package com.example.palcompanion.data

import android.content.Context
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

    private val palImageUrlBase = "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Img/"

    suspend fun loadPals(): List<Pal> {
        val pals = mutableListOf<Pal>()
        try {
            val jsonString = withContext(Dispatchers.IO) {
                URL("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/pals.json").readText()
            }
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.optString("id")
                val name = jsonObject.optString("name")

                val imageUrl = "$palImageUrlBase${name.lowercase().replace(" ", "_")}.webp"

                val elements = mutableListOf<PalElement>()
                try {
                    when (val elementsJson = jsonObject.opt("palElement")) { // CORRECTED KEY
                        is JSONArray -> {
                            for (j in 0 until elementsJson.length()) {
                                elements.add(PalElement.valueOf(elementsJson.getString(j).uppercase()))
                            }
                        }
                        is String -> {
                            elements.add(PalElement.valueOf(elementsJson.uppercase()))
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }

                val workSuitabilitiesArray = jsonObject.optJSONArray("workSuitabilities") ?: JSONArray()
                val workSuitabilities = mutableListOf<PalWorkSuitability>()
                for (j in 0 until workSuitabilitiesArray.length()) {
                    val workObject = workSuitabilitiesArray.getJSONObject(j)
                    try {
                        workSuitabilities.add(
                            PalWorkSuitability(
                                WorkSuitability.valueOf(workObject.getString("work").uppercase()),
                                workObject.getInt("level")
                            )
                        )
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
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
                for (j in 0 until activeSkillsArray.length()) {
                    val skillObject = activeSkillsArray.getJSONObject(j)
                    try {
                        activeSkills.add(
                            ActiveSkill(
                                level = skillObject.optInt("level"),
                                name = skillObject.optString("name"),
                                description = skillObject.optString("description"),
                                cooldown = skillObject.optInt("cooldown"),
                                power = skillObject.optInt("power"),
                                element = PalElement.valueOf(skillObject.getString("skillElement").uppercase()) // CORRECTED KEY
                            )
                        )
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
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
            ioException.printStackTrace()
        } catch (jsonException: JSONException) {
            jsonException.printStackTrace()
        }
        return pals
    }

    fun loadWorkSuitabilityFilters(): List<Filter> {
        return WorkSuitability.entries.map { 
            Filter(
                name = it.name.lowercase().replaceFirstChar { char -> char.uppercase() },
                iconUrl = it.iconUrl,
                workSuitability = it
            )
        } + Filter("Cancel", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/cancel.webp")
    }

    fun loadPalTypeFilters(): List<Filter> {
        return PalElement.entries.map { 
            Filter(
                name = it.name.lowercase().replaceFirstChar { char -> char.uppercase() },
                iconUrl = it.iconIcUrl,
                palElement = it
            )
        } + Filter("Cancel", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/cancel.webp")
    }

    fun loadJobLevelFilters(): List<Filter> {
        return listOf(
            Filter("Cancel", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/cancel.webp"),
        )
    }
}
