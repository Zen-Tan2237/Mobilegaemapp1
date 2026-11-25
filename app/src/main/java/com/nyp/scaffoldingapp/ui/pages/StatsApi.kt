
package com.nyp.scaffoldingapp.ui.pages

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class PlayerStat(
    val name: String,
    val club: String,
    val value: Int
)

object StatsApi {

    private const val ENDPOINT = "https://9pjm1e.mockapi.dog"
    private const val TIMEOUT_MS = 10_000

    // Fetch player statistics from MockAPI Dog

    suspend fun fetchStats(): Map<String, Map<String, List<PlayerStat>>> =
        withContext(Dispatchers.IO) {

            val connection = URL(ENDPOINT)
                .openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS

            try {
                val data = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val json = JSONObject(data)

                // Read statistics for all five leagues
                val leagueKeys = listOf(
                    "premierLeague",
                    "laLiga",
                    "serieA",
                    "bundesliga",
                    "ligue1"
                )

                val allLeagues = mutableMapOf<String, Map<String, List<PlayerStat>>>()

                for (league in leagueKeys) {

                    val leagueJson = json.getJSONObject(league)

                    allLeagues[league] = mapOf(
                        "goals" to readPlayers(leagueJson, "goals"),
                        "assists" to readPlayers(leagueJson, "assists"),
                        "yellowCards" to readPlayers(leagueJson, "yellowCards"),
                        "redCards" to readPlayers(leagueJson, "redCards")
                    )

                }

                allLeagues


            }
            finally {
                connection.disconnect()
            }
        }

    // Convert JSON into a list of players
    private fun readPlayers(
        json: JSONObject,
        category: String
    ): List<PlayerStat> {

        val array = json.getJSONArray(category)
        val players = mutableListOf<PlayerStat>()

        for (i in 0 until array.length()) {
            val player = array.getJSONObject(i)

            players.add(
                PlayerStat(
                    name = player.getString("name"),
                    club = player.getString("club"),
                    value = player.getInt("value")
                )
            )
        }

        return players.sortedByDescending { it.value }.take(20)
    }
}
