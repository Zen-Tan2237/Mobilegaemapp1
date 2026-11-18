package com.nyp.scaffoldingapp.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// The possible states of the standings page
sealed interface StandingsUiState {
    object Loading : StandingsUiState
    data class Ready(val teams: List<Team>) : StandingsUiState
    data class Failed(val message: String) : StandingsUiState
}

class StandingsViewModel : ViewModel() {

    private val _state = MutableStateFlow<StandingsUiState>(
        StandingsUiState.Loading
    )

    val state: StateFlow<StandingsUiState> = _state

    private var request: Job? = null

    // Fetch data when the ViewModel is created
    init {
        load()
    }

    fun load() {
        request?.cancel()

        request = viewModelScope.launch {
            _state.value = StandingsUiState.Loading

            try {
                val teams = StandingsApi.fetchTeams()
                _state.value = StandingsUiState.Ready(teams)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _state.value = StandingsUiState.Failed(
                    "Unable to load standings. Check your internet connection."
                )
            }
        }
    }

    // Restart an interrupted request when returning to this page
    fun resumeIfNeeded() {
        if (_state.value == StandingsUiState.Loading &&
            request?.isActive != true) {
            load()
        }
    }

    // Cancel an unfinished request when leaving this page
    fun stopLoading() {
        if (_state.value == StandingsUiState.Loading) {
            request?.cancel()
        }
    }
}
