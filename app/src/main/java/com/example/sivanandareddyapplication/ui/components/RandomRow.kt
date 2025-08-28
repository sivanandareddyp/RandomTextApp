
package com.example.sivanandareddyapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sivanandareddyapplication.data.local.RandomTextEntity

@Composable
fun RandomRow(item: RandomTextEntity, onDelete: (Long) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(text = item.value, style = MaterialTheme.typography.titleMedium)
            Text("Requested length: ${item.requestedLength}")
            Text("Provider length: ${item.providerLength}")
            Text("Created: ${item.createdIso}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { onDelete(item.id) }) { Text("Delete") }
            }
        }
    }
}
