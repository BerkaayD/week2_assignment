package com.example.week2_assignment.data

import java.text.Normalizer
import java.util.Locale

const val ALL_CATEGORIES = "Tümü"
private val combiningMarks = Regex("\\p{M}+")
private val turkishLocale = Locale.forLanguageTag("tr-TR")

fun emptyAllocations(): List<Int> = List(initialHardware.size) { 0 }

fun allocateOne(allocations: List<Int>, hardwareId: String): List<Int> {
    val index = initialHardware.indexOfFirst { it.id == hardwareId }
    if (index < 0 || allocations[index] >= initialHardware[index].stockCount) {
        return allocations
    }
    return allocations.mapIndexed { itemIndex, count ->
        if (itemIndex == index) count + 1 else count
    }
}

fun inventoryFor(allocations: List<Int>): List<HardwareItem> =
    initialHardware.mapIndexed { index, item ->
        item.copy(stockCount = item.stockCount - allocations[index])
    }

fun totalCostFor(allocations: List<Int>): Double =
    initialHardware.indices.sumOf { initialHardware[it].cost * allocations[it] }

private fun searchable(value: String): String =
    Normalizer.normalize(value.lowercase(turkishLocale), Normalizer.Form.NFD)
        .replace(combiningMarks, "")
        .replace('ı', 'i')

fun filterHardware(
    inventory: List<HardwareItem>,
    searchQuery: String,
    category: String
): List<HardwareItem> {
    val query = searchable(searchQuery.trim())
    return inventory.filter { item ->
        val categoryMatches = category == ALL_CATEGORIES || item.category == category
        val textMatches = query.isEmpty() || searchable(item.name).contains(query) ||
            searchable(item.category).contains(query)
        categoryMatches && textMatches
    }
}

fun formatCost(amount: Double): String = String.format(Locale.US, "$%,.2f", amount)
