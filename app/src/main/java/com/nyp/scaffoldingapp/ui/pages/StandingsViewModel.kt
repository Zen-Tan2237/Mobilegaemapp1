package com.nyp.scaffoldingapp.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


// The possible states of the standings page
sealed interface StandingsUiState {
    object Loading : StandingsUiState
    data class Ready(val leagues: Map<String, List<Team>>) : StandingsUiState
    data class Failed(val message: String) : StandingsUiState
}

class StandingsViewModel : ViewModel() {

    // Store the selected league

    // Store the selected league and club
    var selectedLeague by mutableStateOf("premierLeague")
        private set

    var selectedClub by mutableStateOf<String?>(null)
        private set

    // Change the league manually
    fun selectLeague(league: String) {
        selectedLeague = league
        selectedClub = null
        searchQuery = ""
    }

    // Open a club from the search results
    fun openClub(league: String, club: String) {
        selectedLeague = league
        selectedClub = club
        searchQuery = ""
    }

    // Show the full league table again
    fun showAllClubs() {
        selectedClub = null
    }

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
                val leagues = StandingsApi.fetchLeagues()
                _state.value = StandingsUiState.Ready(leagues)

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
    // Store the user's search input
    var searchQuery by mutableStateOf("")
        private set

    // Update the search input
    fun updateSearchQuery(text: String) {
        searchQuery = text
    }

}
