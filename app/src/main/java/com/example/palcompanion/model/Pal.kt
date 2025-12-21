package com.example.palcompanion.model

import androidx.annotation.StringRes
import com.example.palcompanion.Constants
import com.example.palcompanion.R


data class Pal(
    val id: String,
    val name: String,
    val elements: List<PalElement>,
    val imageUrl: String,
    val workSuitability: List<PalWorkSuitability>,
    val description: String,
    val partnerSkill: PartnerSkill,
    val drops: List<Drop>,
    val activeSkills: List<ActiveSkill>
)


data class PalWorkSuitability(
    val type: WorkSuitability,
    val level: Int
)


data class PartnerSkill(
    val name: String,
    val description: String
)


data class Drop(
    val name: String,
    val quantity: String? = null,
    val rate: String? = null,
    val special: String? = null
) {
    val imageUrl: String
        get() = "${Constants.PALS_DROPS_IMAGE_URL}/${name.replace(' ', '_').lowercase()}.png"
}


data class ActiveSkill(
    val level: Int,
    val name: String,
    val description: String,
    val cooldown: Int,
    val power: Int,
    val element: PalElement
)


enum class PalElement(val iconUrl: String, val iconIcUrl: String) {
    NORMAL(Constants.NORMAL_ELEMENT_URL, Constants.NORMAL_ELEMENT_ICON_URL),
    GRASS(Constants.GRASS_ELEMENT_URL, Constants.GRASS_ELEMENT_ICON_URL),
    FIRE(Constants.FIRE_ELEMENT_URL, Constants.FIRE_ELEMENT_ICON_URL),
    WATER(Constants.WATER_ELEMENT_URL, Constants.WATER_ELEMENT_ICON_URL),
    ICE(Constants.ICE_ELEMENT_URL, Constants.ICE_ELEMENT_ICON_URL),
    ELECTRIC(Constants.ELECTRIC_ELEMENT_URL, Constants.ELECTRIC_ELEMENT_ICON_URL),
    GROUND(Constants.GROUND_ELEMENT_URL, Constants.GROUND_ELEMENT_ICON_URL),
    DARK(Constants.DARK_ELEMENT_URL, Constants.DARK_ELEMENT_ICON_URL),
    DRAGON(Constants.DRAGON_ELEMENT_URL, Constants.DRAGON_ELEMENT_ICON_URL)
}


enum class WorkSuitability(val iconUrl: String, @StringRes val displayName: Int) {
    KINDLING(Constants.KINDLING_ICON_URL, R.string.work_kindling),
    WATERING(Constants.WATERING_ICON_URL, R.string.work_watering),
    PLANTING(Constants.PLANTING_ICON_URL, R.string.work_planting),
    GENERATING_ELECTRICITY(Constants.GENERATING_ELECTRICITY_ICON_URL, R.string.work_generating_electricity),
    HANDIWORK(Constants.HANDIWORK_ICON_URL, R.string.work_handiwork),
    GATHERING(Constants.GATHERING_ICON_URL, R.string.work_gathering),
    LUMBERING(Constants.LUMBERING_ICON_URL, R.string.work_lumbering),
    MINING(Constants.MINING_ICON_URL, R.string.work_mining),
    MEDICINE_PRODUCTION(Constants.MEDICINE_PRODUCTION_ICON_URL, R.string.work_medicine_production),
    COOLING(Constants.COOLING_ICON_URL, R.string.work_cooling),
    TRANSPORTING(Constants.TRANSPORTING_ICON_URL, R.string.work_transporting),
    FARMING(Constants.FARMING_PALS_ICON_URL, R.string.work_farming)
}
