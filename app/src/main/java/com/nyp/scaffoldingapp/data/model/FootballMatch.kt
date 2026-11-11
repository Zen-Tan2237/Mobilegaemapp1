package com.nyp.scaffoldingapp.data.model

/** Shared fixture model: Page 4 can reuse this later for completed matches. */
data class FootballMatch(
    val id: String,
    val league: String,
    val homeTeam: String,
    val awayTeam: String,
    val kickOffMillis: Long,
    val venue: String
)
