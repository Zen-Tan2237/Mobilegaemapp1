
package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class PlayerStat(
    val name: String,
    val club: String,
    val value: Int
)

// Read a list of players from the JSON response
fun readPlayers(json: JSONObject, category: String): List<PlayerStat> {
    val array = json.getJSONArray(category)
    val players = mutableListOf<PlayerStat>()

    for (i in 0 until array.length()) {
        val player = array.getJSONObject(i)

        players.add(
            PlayerStat(
                player.getString("name"),
                player.getString("club"),
                player.getInt("value")
            )
        )
    }

    return players.sortedByDescending { it.value }.take(20)
}

@Composable
fun Page2(modifier: Modifier = Modifier) {

    val tabs = listOf("Goals", "Assists", "Yellow Cards", "Red Cards")
    val keys = listOf("goals", "assists", "yellowCards", "redCards")

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var stats by remember {
        mutableStateOf<Map<String, List<PlayerStat>>>(emptyMap())
    }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    // Fetch player stats from the API
    LaunchedEffect(Unit) {
        try {
            val data = withContext(Dispatchers.IO) {
                URL("https://s4zj4o.mockapi.dog").readText()
            }

            val json = JSONObject(data)

            stats = mapOf(
                "goals" to readPlayers(json, "goals"),
                "assists" to readPlayers(json, "assists"),
                "yellowCards" to readPlayers(json, "yellowCards"),
                "redCards" to readPlayers(json, "redCards")
            )

        } catch (e: Exception) {
            error = "Failed to load player stats"
        }

        loading = false
    }

    val players = stats[keys[selectedTab]].orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Tabs for switching between statistics
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Table header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Player")
            Text(tabs[selectedTab])
        }

        HorizontalDivider()

        // Display the player list
        if (loading) {
            CircularProgressIndicator()
        } else if (error.isNotEmpty()) {
            Text(error)
        } else if (players.isEmpty()) {
            Text("No players found")
        } else {
            LazyColumn {
                items(players) { player ->
                    PlayerStatRow(player)
                    HorizontalDivider()
                }
            }
        }
    }
}

// Display one player's name, club and statistic
@Composable
fun PlayerStatRow(player: PlayerStat) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {
            Text(
                player.name,
                fontWeight = FontWeight.Medium
            )

            Text(
                player.club,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            "${player.value}",
            fontWeight = FontWeight.Bold
        )
    }
}
