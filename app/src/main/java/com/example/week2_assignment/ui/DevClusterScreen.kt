package com.example.week2_assignment.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.week2_assignment.data.*
import com.example.week2_assignment.ui.theme.DevClusterTheme

private val AllocationSaver = listSaver<List<Int>, Int>(save = { it }, restore = { it.toList() })

@Composable
fun DevClusterScreen() {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(ALL_CATEGORIES) }
    var allocations by rememberSaveable(stateSaver = AllocationSaver) { mutableStateOf(emptyAllocations()) }
    var expandedId by rememberSaveable { mutableStateOf<String?>(null) }
    var status by rememberSaveable { mutableStateOf("Kaynakların hazır. Kümene donanım ayırarak başla.") }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    val inventory = remember(allocations) { inventoryFor(allocations) }
    val totalSpent = remember(allocations) { totalCostFor(allocations) }
    val allocatedCount = remember(allocations) { allocations.sum() }
    val remainingCount = remember(inventory) { inventory.sumOf { it.stockCount } }
    val categories = remember { listOf(ALL_CATEGORIES) + initialHardware.map { it.category }.distinct() }

    val matchingHardware = remember(searchQuery, selectedCategory) {
        filterHardware(initialHardware, searchQuery, selectedCategory)
    }

    val filteredHardware = remember(matchingHardware, inventory) {
        val inventoryById = inventory.associateBy { it.id }
        matchingHardware.map { inventoryById.getValue(it.id) }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding).imePadding(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 840.dp).fillMaxSize().testTag("hardware_list"),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item(key = "header") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("DEV / ALTYAPI LABORATUVARI", color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium)
                        Text("DevCluster", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Text("Donanım ve sunucu kaynak yönetimi", color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
                item(key = "budget") {
                    Surface(shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("TOPLAM HARCAMA", style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(formatCost(totalSpent), modifier = Modifier.testTag("total_spent"),
                                style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                            Text("Yerel simülasyon · Tutarlar USD", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
                item(key = "metrics") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Metric("Kalan stok", remainingCount, Modifier.weight(1f))
                        Metric("Ayrılan", allocatedCount, Modifier.weight(1f).testTag("allocated_total"))
                        Metric("Çeşit", inventory.size, Modifier.weight(1f))
                    }
                }
                item(key = "search") {
                    HardwareSearchBar(searchQuery = searchQuery, onQueryChange = { searchQuery = it })
                }
                item(key = "categories") {
                    LazyRow(modifier = Modifier.testTag("category_list"), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories, key = { it }) { category ->
                            FilterChip(selected = selectedCategory == category,
                                onClick = { selectedCategory = category }, label = { Text(category) },
                                modifier = Modifier.testTag("category_$category"))
                        }
                    }
                }
                item(key = "results") {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Envanter · ${filteredHardware.size} sonuç", style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.testTag("result_count"))
                        TextButton(onClick = { showResetDialog = true }, enabled = allocatedCount > 0,
                            modifier = Modifier.testTag("reset_session")) { Text("Sıfırla") }
                    }
                    Text(status, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                }
                if (filteredHardware.isEmpty()) {
                    item(key = "empty") {
                        Surface(shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                            Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Eşleşen donanım yok", style = MaterialTheme.typography.titleMedium)
                                Text("Başka bir kelime deneyebilir veya filtreleri temizleyebilirsin.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                TextButton(onClick = { searchQuery = ""; selectedCategory = ALL_CATEGORIES },
                                    modifier = Modifier.testTag("clear_filters")) { Text("Filtreleri temizle") }
                            }
                        }
                    }
                }

                items(filteredHardware, key = { it.id }, contentType = { "hardware" }) { item ->
                    val index = initialHardware.indexOfFirst { it.id == item.id }
                    HardwareCard(item = item, allocatedCount = allocations[index], expanded = expandedId == item.id,
                        onToggleDetails = { expandedId = if (expandedId == item.id) null else item.id },
                        onAllocate = {
                            val updated = allocateOne(allocations, item.id)
                            if (updated != allocations) {
                                allocations = updated
                                status = "${item.name}: 1 adet kümene ayrıldı."
                            }
                        })
                }
                item(key = "footer") {
                    Text("Bir karta dokunarak ayırma ayrıntılarını görebilirsin.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
    if (showResetDialog) {
        AlertDialog(onDismissRequest = { showResetDialog = false },
            title = { Text("Ayırmalar sıfırlansın mı?") },
            text = { Text("Ayrılan kaynaklar stoka döner ve toplam harcama sıfırlanır.") },
            confirmButton = {
                TextButton(onClick = {
                    allocations = emptyAllocations()
                    status = "Ayırmalar sıfırlandı. Tüm kaynaklar başlangıç stoklarına döndü."
                    showResetDialog = false
                }, modifier = Modifier.testTag("confirm_reset")) { Text("Sıfırla") }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Vazgeç") } })
    }
}

@Composable
private fun Metric(label: String, value: Int, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DevClusterPreview() { DevClusterTheme { DevClusterScreen() } }
