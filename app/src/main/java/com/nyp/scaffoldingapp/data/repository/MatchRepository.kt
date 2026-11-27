package com.nyp.scaffoldingapp.data.repository

import com.nyp.scaffoldingapp.data.model.FootballMatch
import java.util.Calendar
import com.nyp.scaffoldingapp.utils.ApiHelper
import org.json.JSONArray

/** Swap this implementation for an HTTP-backed repository when the API is ready. */
interface MatchRepository {
    suspend fun getUpcomingMatches(): List<FootballMatch>
}

/** Fictional fixtures for demonstration; these are NOT actual football schedules. */
class DemoMatchRepository : MatchRepository {
    override suspend fun getUpcomingMatches(): List<FootballMatch> = listOf(
        fixture("1", "Premier League", "Arsenal", "Chelsea", 0, 20, 0, "Emirates Stadium"),
        fixture("2", "La Liga", "Barcelona", "Valencia", 1, 19, 30, "Home stadium"),
        fixture("3", "Premier League", "Liverpool", "Newcastle", 1, 21, 0, "Anfield"),
        fixture("4", "Champions League", "Bayern Munich", "Inter", 2, 20, 0, "Allianz Arena"),
        fixture("5", "Premier League", "Man City", "Brighton", 4, 17, 30, "Etihad Stadium"),
        fixture("6", "La Liga", "Real Madrid", "Sevilla", 6, 21, 0, "Santiago Bernabéu"),
        fixture("7", "Champions League", "PSG", "Arsenal", 10, 20, 0, "Parc des Princes")
    ).sortedBy { it.kickOffMillis }

    private fun fixture(
        id: String, league: String, home: String, away: String,
        daysFromToday: Int, hour: Int, minute: Int, venue: String
    ): FootballMatch {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, daysFromToday)
        }
        return FootballMatch(id, league, home, away, calendar.timeInMillis, venue)
    }
}

class ApiMatchRepository : MatchRepository {

    companion object {
        private const val ENDPOINT = "https://pu31cc.mockapi.dog/Fixtures"
    }

    override suspend fun getUpcomingMatches(): List<FootballMatch> {

        val array = ApiHelper.getArray(ENDPOINT)
        val matches = mutableListOf<FootballMatch>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)

            val kickOffMillis = ApiHelper.relativeDate(
                obj.getInt("daysFromToday"),
                obj.getInt("hour"),
                obj.getInt("minute")
            )

            matches.add(
                FootballMatch(
                    id = obj.getString("id"),
                    league = obj.getString("league"),
                    homeTeam = obj.getString("homeTeam"),
                    awayTeam = obj.getString("awayTeam"),
                    kickOffMillis = kickOffMillis,
                    venue = obj.getString("venue")
                )
            )
        }

        return matches.sortedBy { it.kickOffMillis }
    }
}