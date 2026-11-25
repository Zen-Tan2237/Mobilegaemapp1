package com.nyp.scaffoldingapp.ui.pages

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Team(
    val name: String,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int
) {
    val mp get() = wins + draws + losses
    val pts get() = wins * 3 + draws
    val gd get() = goalsFor - goalsAgainst
}

object StandingsApi {
    private const val ENDPOINT = "https://dt2z7j.mockapi.dog"
    private const val TIMEOUT_MS = 10_000

    // Fetch standings from MockAPI Dog

    suspend fun fetchLeagues(): Map<String, List<Team>> = withContext(Dispatchers.IO) {

        val connection = URL(ENDPOINT).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = TIMEOUT_MS
        connection.readTimeout = TIMEOUT_MS

        try {
            val data = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(data)

            val leagues = mutableMapOf<String, List<Team>>()
            val keys = listOf("premierLeague", "laLiga", "serieA", "bundesliga", "ligue1")

            for (key in keys) {
                val array = json.getJSONArray(key)
                val teams = mutableListOf<Team>()

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)

                    teams.add(
                        Team(
                            name = obj.getString("name"),
                            wins = obj.getInt("wins"),
                            draws = obj.getInt("draws"),
                            losses = obj.getInt("losses"),
                            goalsFor = obj.getInt("goalsFor"),
                            goalsAgainst = obj.getInt("goalsAgainst")
                        )
                    )
                }

                leagues[key] = teams
            }

            leagues

        } finally
        {
            connection.disconnect()
        }
    }
}

