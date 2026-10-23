package com.example.week2_assignment.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.week2_assignment.data.HardwareItem
import com.example.week2_assignment.data.formatCost

// Stateless: Bu bileşenin içinde remember veya kendi arama durumu yoktur.
@Composable
fun HardwareSearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth().testTag("hardware_search"),
        label = { Text("Donanım veya kategori ara") },
        placeholder = { Text("Örn. Pixel, GPU, monitör") },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                TextButton(onClick = { onQueryChange("") }) { Text("Temizle") }
            }
        }
    )
}

// Stateless: Veri aşağı iner; kullanıcı olayları callback ile üst ekrana çıkar.
@Composable
fun HardwareCard(
    item: HardwareItem,
    allocatedCount: Int,
    expanded: Boolean,
    onToggleDetails: () -> Unit,
    onAllocate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val available = item.stockCount > 0
    val badge = when (item.id) {
        "101" -> "DEV"
        "102", "108" -> "GPU"
        "103" -> "MOB"
        "104" -> "DSP"
        "105" -> "SND"
        "106" -> "IoT"
        else -> "KEY"
    }

    Surface(
        modifier = modifier.fillMaxWidth().testTag("hardware_card_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, if (expanded) colors.primary else colors.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    role = Role.Button,
                    onClickLabel = if (expanded) "Ayrıntıları gizle" else "Ayrıntıları göster",
                    onClick = onToggleDetails
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp)
                        .background(colors.primary.copy(alpha = 0.10f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(badge, color = colors.primary, fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(item.category, color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium)
                    Text(item.name, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(formatCost(item.cost), style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                    Text(
                        text = if (available) "${item.stockCount} adet stokta" else "Stok tükendi",
                        color = if (available) colors.primary else colors.error,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.testTag("stock_${item.id}")
                    )
                }
                Button(
                    onClick = onAllocate,
                    enabled = available,
                    modifier = Modifier.testTag("allocate_${item.id}"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (available) "+  Kaynak ayır" else "Stok yok")
                }
            }
            if (expanded) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.outlineVariant))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kaynak #${item.id}", color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall)
                    Text("Kümende $allocatedCount adet", color = colors.primary,
                        style = MaterialTheme.typography.bodySmall)
                }
                Text("Her işlem 1 adet ayırır ve birim fiyatı toplam harcamaya ekler.",
                    color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
