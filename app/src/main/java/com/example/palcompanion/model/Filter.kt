package com.example.palcompanion.model

data class Filter(
    val name: String,
    val iconUrl: String,
    val workSuitability: WorkSuitability? = null,
    val palElement: PalElement? = null
)
