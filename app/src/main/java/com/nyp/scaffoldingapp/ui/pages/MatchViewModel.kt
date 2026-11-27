package com.nyp.scaffoldingapp.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface MatchListUiState<out T> {

	object Loading : MatchListUiState<Nothing>

	data class Ready<T>(val matches: List<T>) : MatchListUiState<T>

	data class Failed(val message: String) : MatchListUiState<Nothing>
}

abstract class MatchListViewModel<T>(private val fetchMatches: suspend () -> List<T>) : ViewModel() {

	private val _state =
		MutableStateFlow<MatchListUiState<T>>(
			MatchListUiState.Loading
		)

	val state: StateFlow<MatchListUiState<T>> = _state

	private var request: Job? = null

	init {
		load()
	}

	fun load() {
		request?.cancel()

		request = viewModelScope.launch {

			_state.value = MatchListUiState.Loading

			try {
				val matches = fetchMatches()

				_state.value = MatchListUiState.Ready(matches)

			} catch (e: CancellationException) {
				throw e

			} catch (e: Exception) {

				_state.value = MatchListUiState.Failed(
					"Unable to load matches. Please try again."
				)
			}
		}
	}

	fun resumeIfNeeded() {
		if (_state.value == MatchListUiState.Loading &&
			request?.isActive != true
		) {
			load()
		}
	}

	fun stopLoading() {
		if (_state.value == MatchListUiState.Loading) {
			request?.cancel()
		}
	}
}