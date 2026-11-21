
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

    private const val ENDPOINT = "https://s4zj4o.mockapi.dog"
    private const val TIMEOUT_MS = 10_000

    // Fetch player statistics from MockAPI Dog
    suspend fun fetchStats(): Map<String, List<PlayerStat>> =
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

                mapOf(
                    "goals" to readPlayers(json, "goals"),
                    "assists" to readPlayers(json, "assists"),
                    "yellowCards" to readPlayers(json, "yellowCards"),
                    "redCards" to readPlayers(json, "redCards")
                )

            } finally {
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
