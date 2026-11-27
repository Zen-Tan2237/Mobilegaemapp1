package com.nyp.scaffoldingapp.data.repository

import com.nyp.scaffoldingapp.data.model.MatchHighlights
import java.util.Calendar
import com.nyp.scaffoldingapp.utils.ApiHelper
import org.json.JSONArray

interface HighlightsRepository {
	suspend fun getMatchesFromLast10Days() : List<MatchHighlights>
}

class DemoHighlightsRepository : HighlightsRepository
{
	override suspend fun getMatchesFromLast10Days(): List<MatchHighlights> = listOf(
		playedMatches("1", "Premier League", "Liverpool", "Man Utd", 4, 0, "A shit show from Man Utd (everyone here is trash)", "https://youtu.be/vDch-wB7v84?is=1zKvP3c_yFDda69E", 1, 1,30),
		playedMatches("2", "La Liga", "Real Madrid", "Barcelona", 1,1, "Average El Clasico", "https://youtu.be/ecnJ4ScASPM?is=8jN70k0AJXwsv30O", 2, 5, 25),
		playedMatches("3", "Premier League", "Liverpool", "Newcastle", 4, 2, "Liverpool win a six-goal encounter.", "https://www.youtube.com/watch?v=eR3p5jEzcOQ", 1, 4, 0),
		playedMatches("4", "La Liga", "Real Madrid", "Sevilla", 4, 2, "Real Madrid score four at home.", "https://www.youtube.com/watch?v=ezZfe_whR4M", 3, 2, 30),
		playedMatches("5", "Premier League", "Brighton", "Man City", 2, 1, "Brighton edge Manchester City.", "https://www.youtube.com/watch?v=yEoTiIYSW68", 5, 1, 0),
		playedMatches("6", "Champions League", "Bayern Munich", "Inter", 1, 2, "Inter earn an away victory.", "https://www.uefa.com/uefachampionsleague/history/video/0298-1d7995e253c8-257d4506d25a-1000--highlights-bayern-munchen-1-2-inter/", 7, 3, 0),
		playedMatches("7", "Champions League", "PSG", "Arsenal", 2, 1, "PSG win a close European encounter.", "https://www.uefa.com/uefachampionsleague/history/video/0299-1db3e33c2d21-31bfed0aeaa2-1000--highlights-paris-2-1-arsenal/", 9, 3, 0),
		playedMatches("8", "Premier League", "Arsenal", "Chelsea", 2, 1, "A narrow London derby victory for Arsenal.", "https://www.youtube.com/watch?v=nALnEAhBCY4", 0, 0, 30),
		playedMatches("9", "La Liga", "Barcelona", "Real Madrid", 4, 3, "A seven-goal El Clasico thriller.", "https://www.youtube.com/watch?v=0c8RD3fd9hc", 1, 3, 0),
	).sortedByDescending { it.dateMillis }


	private fun playedMatches(
		id : String, league : String, homeTeam : String, awayTeam : String, homeScore : Int, awayScore : Int,
		description : String, highlightsURL : String, daysAgo: Int, hour : Int, minute : Int
	) : MatchHighlights
	{
		val calendar = Calendar.getInstance().apply {
			add(Calendar.DAY_OF_YEAR, -daysAgo)
			set(Calendar.HOUR_OF_DAY, hour)
			set(Calendar.MINUTE, minute)
			set(Calendar.SECOND, 0)
			set(Calendar.MILLISECOND, 0)
		}
		return MatchHighlights(id, league, homeTeam, awayTeam, homeScore, awayScore, calendar.timeInMillis,description, highlightsURL)
	}
}

class ApiHighlightsRepository : HighlightsRepository {

	companion object {
		private const val ENDPOINT = "https://pu31cc.mockapi.dog/Highlights"
	}

	override suspend fun getMatchesFromLast10Days():
			List<MatchHighlights> {

		val array = ApiHelper.getArray(ENDPOINT)
		val matches = mutableListOf<MatchHighlights>()

		for (i in 0 until array.length()) {
			val obj = array.getJSONObject(i)

			val daysAgo = obj.getInt("daysAgo")

			if (daysAgo !in 0..9)
				continue

			val dateMillis = ApiHelper.relativeDate(
				-daysAgo,
				obj.getInt("hour"),
				obj.getInt("minute")
			)

			matches.add(
				MatchHighlights(
					id = obj.getString("id"),
					league = obj.getString("league"),
					homeTeam = obj.getString("homeTeam"),
					awayTeam = obj.getString("awayTeam"),
					homeScore = obj.getInt("homeScore"),
					awayScore = obj.getInt("awayScore"),
					dateMillis = dateMillis,
					description = obj.getString("description"),
					highlightURL = obj.getString("highlightURL")
				)
			)
		}

		return matches.sortedByDescending { it.dateMillis }
	}
}