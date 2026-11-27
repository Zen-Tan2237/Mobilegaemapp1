package com.nyp.scaffoldingapp.utils

import kotlin.random.Random

fun shortTeamName(name: String): String {
	return when (name) {
		"Borussia Monchengladbach",
		"Borussia Mönchengladbach",
		"Borussia Moenchengladbach" -> "Gladbach"

		"Borussia Dortmund" -> "Dortmund"
		"Brighton & Hove Albion" -> "Brighton"
		"Wolverhampton Wanderers" -> "Wolves"
		"Paris Saint-Germain" -> "PSG"
		"Manchester United" -> "Man Utd"
		"Manchester City" -> "Man City"
		"Eintracht Frankfurt" -> "Frankfurt"

		else -> name
	}
}

fun rollChance(chance : Int) : Boolean
{
	return Random.nextInt(100) < chance
}