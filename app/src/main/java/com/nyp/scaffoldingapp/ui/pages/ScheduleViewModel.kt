package com.nyp.scaffoldingapp.ui.pages

import com.nyp.scaffoldingapp.data.model.FootballMatch
import com.nyp.scaffoldingapp.data.repository.ApiMatchRepository

class ScheduleViewModel : MatchListViewModel<FootballMatch>(
	fetchMatches = {
		ApiMatchRepository().getUpcomingMatches()
	}
)