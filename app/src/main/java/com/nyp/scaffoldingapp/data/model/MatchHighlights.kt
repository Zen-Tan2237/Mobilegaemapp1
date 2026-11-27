
package com.nyp.scaffoldingapp.data.model

data class MatchHighlights(
	val id: String,
	val league: String,
	val homeTeam: String,
	val awayTeam: String,
	val homeScore: Int,
	val awayScore: Int,
	val dateMillis: Long,
	val description: String,
	val highlightURL: String
)
