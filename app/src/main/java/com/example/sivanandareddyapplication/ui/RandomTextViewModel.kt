package com.example.sivanandareddyapplication.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sivanandareddyapplication.data.RandomTextRepository
import com.example.sivanandareddyapplication.data.local.RandomTextEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiState(
    val requestedLength: String = "16",
    val isLoading: Boolean = false,
    val error: String? = null
)

class RandomTextViewModel(private val repo: RandomTextRepository) : ViewModel() {

    // Compose mutable state for UI state
    var state by mutableStateOf(UiState())
        private set

    // StateFlow for items
    val items: StateFlow<List<RandomTextEntity>> =
        repo.items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onLengthChange(newValue: String) {
        state = state.copy(requestedLength = newValue.filter { it.isDigit() }.take(4))
    }

    fun generate() {
        val len = state.requestedLength.toIntOrNull()
        if (len == null || len <= 0) {
            state = state.copy(error = "Enter valid length")
            return
        }
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            val res = repo.fetchAndSave(len)
            state = state.copy(isLoading = false)
            res.onFailure { e -> state = state.copy(error = e.message ?: "Unknown error") }
        }
    }

    fun deleteAll() {
        viewModelScope.launch { repo.deleteAll() }
    }

    fun deleteOne(id: Long) {
        viewModelScope.launch { repo.deleteOne(id) }
    }

    fun clearError() {
        state = state.copy(error = null)
    }
}
