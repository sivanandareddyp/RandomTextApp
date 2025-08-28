@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.sivanandareddyapplication.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sivanandareddyapplication.ui.RandomTextViewModel
import com.example.sivanandareddyapplication.ui.components.ErrorBanner
import com.example.sivanandareddyapplication.ui.components.RandomRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: RandomTextViewModel) {

    val items by vm.items.collectAsState()           // StateFlow collected as Compose State
    val state = vm.state                             // Compose mutableStateOf can use 'by' directly

    Scaffold(topBar = { TopAppBar(title = { Text("Random Generator") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = state.requestedLength,
                onValueChange = vm::onLengthChange,
                label = { Text("Max length") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = vm::generate, enabled = !state.isLoading) { Text("Generate") }
                OutlinedButton(onClick = vm::deleteAll) { Text("Delete all") }
            }

            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())

            ErrorBanner(state.error) {
                vm.clearError()
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.id }) { item ->
                    RandomRow(item) { id -> vm.deleteOne(id) }
                }
            }
        }
    }
}
