package com.example.week2_assignment

import com.example.week2_assignment.data.*
import org.junit.Assert.*
import org.junit.Test

class HardwareOperationsTest {
    @Test fun allocationCreatesNewStateWithoutChangingOriginal() {
        val before = emptyAllocations()
        val after = allocateOne(before, "103")
        assertEquals(0, before.sum())
        assertEquals(1, after.sum())
        assertEquals(8, initialHardware.first { it.id == "103" }.stockCount)
        assertEquals(7, inventoryFor(after).first { it.id == "103" }.stockCount)
        assertEquals(999.0, totalCostFor(after), 0.001)
    }

    @Test fun exhaustedStockCannotBeAllocatedAgain() {
        var allocations = emptyAllocations()
        repeat(10) { allocations = allocateOne(allocations, "108") }
        assertEquals(1, allocations.sum())
        assertEquals(0, inventoryFor(allocations).first { it.id == "108" }.stockCount)
        assertEquals(4999.0, totalCostFor(allocations), 0.001)
    }

    @Test fun zeroStockAndUnknownIdLeaveStateUntouched() {
        val allocations = emptyAllocations()
        assertSame(allocations, allocateOne(allocations, "107"))
        assertSame(allocations, allocateOne(allocations, "missing"))
    }

    @Test fun mixedAllocationsAccumulateCostsAndReduceOnlyTheirStocks() {
        val allocations = allocateOne(allocateOne(allocateOne(emptyAllocations(), "106"), "103"), "106")
        assertEquals(1169.0, totalCostFor(allocations), 0.001)
        assertEquals(54, inventoryFor(allocations).sumOf { it.stockCount })
        assertEquals(5, inventoryFor(allocations).first { it.id == "101" }.stockCount)
    }

    @Test fun categoryAndQueryMustBothMatch() {
        assertEquals(listOf("102"), filterHardware(initialHardware, "nvidia", "Yapay Zeka / GPU").map { it.id })
        assertTrue(filterHardware(initialHardware, "pixel", "Yapay Zeka / GPU").isEmpty())
        assertEquals(listOf("102", "108"), filterHardware(initialHardware, "", "Yapay Zeka / GPU").map { it.id })
    }

    @Test fun searchSupportsTurkishLettersWhitespaceAndCategoryNames() {
        assertEquals(listOf("104"), filterHardware(initialHardware, "  MONITOR  ", ALL_CATEGORIES).map { it.id })
        assertEquals(listOf("105"), filterHardware(initialHardware, "KULAKLIK", ALL_CATEGORIES).map { it.id })
        assertEquals(8, filterHardware(initialHardware, "  ", ALL_CATEGORIES).size)
    }
}
