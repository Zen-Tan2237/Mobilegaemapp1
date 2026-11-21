
package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// The possible states of the statistics page
sealed interface StatsUiState {
    object Loading : StatsUiState

    data class Ready(
        val stats: Map<String, List<PlayerStat>>
    ) : StatsUiState

    data class Failed(val message: String) : StatsUiState
}

class StatsViewModel : ViewModel() {

    private val _state = MutableStateFlow<StatsUiState>(
        StatsUiState.Loading
    )

    val state: StateFlow<StatsUiState> = _state

    // Remember which statistics category is selected
    var selectedTab by mutableIntStateOf(0)
        private set

    private var request: Job? = null

    init {
        load()
    }

    fun selectTab(index: Int) {
        selectedTab = index
    }

    // Fetch player statistics
    fun load() {
        request?.cancel()

        request = viewModelScope.launch {
            _state.value = StatsUiState.Loading

            try {
                val stats = StatsApi.fetchStats()
                _state.value = StatsUiState.Ready(stats)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _state.value = StatsUiState.Failed(
                    "Unable to load player stats. Check your connection."
                )
            }
        }
    }

    // Resume loading if a previous request was cancelled
    fun resumeIfNeeded() {
        if (_state.value == StatsUiState.Loading &&
            request?.isActive != true) {
            load()
        }
    }

    // Cancel the request when leaving the page
    fun stopLoading() {
        if (_state.value == StatsUiState.Loading) {
            request?.cancel()
        }
    }
}
