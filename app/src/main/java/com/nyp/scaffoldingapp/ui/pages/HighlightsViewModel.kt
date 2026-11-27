package com.nyp.scaffoldingapp.ui.pages

import com.nyp.scaffoldingapp.data.model.MatchHighlights
import com.nyp.scaffoldingapp.data.repository.ApiHighlightsRepository

class HighlightsViewModel : MatchListViewModel<MatchHighlights>(
	fetchMatches = {
		ApiHighlightsRepository().getMatchesFromLast10Days()
	}
)