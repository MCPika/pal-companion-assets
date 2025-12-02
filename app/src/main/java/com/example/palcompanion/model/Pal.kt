package com.example.palcompanion.model

import androidx.annotation.StringRes
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
        get() = "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Drops/${name.replace(' ', '_').lowercase()}.png"
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
    NORMAL("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements/normal.webp", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements_Ic/normal_ic.webp"),
    GRASS("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements/grass.webp", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements_Ic/grass_ic.webp"),
    FIRE("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements/fire.webp", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements_Ic/fire_ic.webp"),
    WATER("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements/water.webp", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements_Ic/water_ic.webp"),
    ICE("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements/ice.webp", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements_Ic/ice_ic.webp"),
    ELECTRIC("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements/electric.webp", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements_Ic/electric_ic.webp"),
    GROUND("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements/ground.webp", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements_Ic/ground_ic.webp"),
    DARK("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements/dark.webp", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements_Ic/dark_ic.webp"),
    DRAGON("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements/dragon.webp", "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Elements_Ic/dragon_ic.webp")
}


enum class WorkSuitability(val iconUrl: String, @StringRes val displayName: Int) {
    KINDLING("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/kindling.webp", R.string.work_kindling),
    WATERING("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/watering.webp", R.string.work_watering),
    PLANTING("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/planting.webp", R.string.work_planting),
    GENERATING_ELECTRICITY("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/generating_electricity.webp", R.string.work_generating_electricity),
    HANDIWORK("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/handiwork.webp", R.string.work_handiwork),
    GATHERING("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/gathering.webp", R.string.work_gathering),
    LUMBERING("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/lumbering.webp", R.string.work_lumbering),
    MINING("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/mining.webp", R.string.work_mining),
    MEDICINE_PRODUCTION("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/medicine_production.webp", R.string.work_medicine_production),
    COOLING("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/cooling.webp", R.string.work_cooling),
    TRANSPORTING("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/transporting.webp", R.string.work_transporting),
    FARMING("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/farming.webp", R.string.work_farming)
}
