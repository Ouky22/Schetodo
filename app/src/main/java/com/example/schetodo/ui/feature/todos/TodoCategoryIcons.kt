package com.example.schetodo.ui.feature.todos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

val sportIcons = mapOf(
    mapEntryOf(Icons.Filled.SportsBaseball),
    mapEntryOf(Icons.Filled.Sports),
    mapEntryOf(Icons.Filled.SportsCricket),
    mapEntryOf(Icons.Filled.SportsEsports),
    mapEntryOf(Icons.Filled.SportsBasketball),
    mapEntryOf(Icons.Filled.SportsGymnastics),
    mapEntryOf(Icons.Filled.SportsFootball),
    mapEntryOf(Icons.Filled.SportsHockey),
)

val householdIcons = mapOf(
    mapEntryOf(Icons.Filled.House),
    mapEntryOf(Icons.Filled.Chair),
    mapEntryOf(Icons.Filled.Kitchen),
    mapEntryOf(Icons.Filled.Window),
    mapEntryOf(Icons.Filled.Dining),
    mapEntryOf(Icons.Filled.CoffeeMaker),
)

fun mapEntryOf(vector: ImageVector) = vector.name to vector

fun getIconByName(iconName: String): ImageVector? {
    val allIconMaps = listOf(sportIcons, householdIcons)

    for (iconMap in allIconMaps) {
        val icon = iconMap[iconName]
        if (icon != null)
            return icon
    }
    return null
}