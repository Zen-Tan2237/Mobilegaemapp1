package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
/**
 * Pages 1 to 3 are placeholders. They exist so the navigation bar has
 * somewhere to go.
 *
 * Replace them. The rubric asks for four sections, each doing a DIFFERENT
 * job — four labels is one section, four times.
 */

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

@Composable
fun Page1(modifier: Modifier = Modifier) {

    // Sample data, not live standings
    var teams by remember {
        mutableStateOf<List<Team>>(emptyList())
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        try {
            val json = withContext(Dispatchers.IO) {
                URL("https://49rq25.mockapi.dog/")
                    .openStream()
                    .bufferedReader()
                    .use { it.readText() }
            }

            val array = JSONArray(json)

            teams = (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)

                Team(
                    name = obj.getString("name"),
                    wins = obj.getInt("wins"),
                    draws = obj.getInt("draws"),
                    losses = obj.getInt("losses"),
                    goalsFor = obj.getInt("goalsFor"),
                    goalsAgainst = obj.getInt("goalsAgainst")
                )
            }

        } catch (e: Exception) {
            error = e.message ?: "Failed to load standings"
        }
    }


    // Column widths
    val posWidth = 30.dp
    val clubWidth = 130.dp
    val statWidth = 45.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Premier League",
            fontSize = 24.sp
        )

        Spacer(Modifier.height(16.dp))

        // Scrollable table
        Column(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {

            // TABLE HEADER
            Row {
                Text("#", Modifier.width(posWidth))
                Text("Club", Modifier.width(clubWidth))
                Text("MP", Modifier.width(statWidth))
                Text("W", Modifier.width(statWidth))
                Text("D", Modifier.width(statWidth))
                Text("L", Modifier.width(statWidth))
                Text("Pts", Modifier.width(statWidth))
                Text("GF", Modifier.width(statWidth))
                Text("GA", Modifier.width(statWidth))
                Text("GD", Modifier.width(statWidth))
            }

            HorizontalDivider()

            // TABLE DATA
            teams.forEachIndexed { index, team ->

                Row(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                ) {
                    Text("${index + 1}", Modifier.width(posWidth))
                    Text(team.name, Modifier.width(clubWidth))
                    Text("${team.mp}", Modifier.width(statWidth))
                    Text("${team.wins}", Modifier.width(statWidth))
                    Text("${team.draws}", Modifier.width(statWidth))
                    Text("${team.losses}", Modifier.width(statWidth))
                    Text("${team.pts}", Modifier.width(statWidth))
                    Text("${team.goalsFor}", Modifier.width(statWidth))
                    Text("${team.goalsAgainst}", Modifier.width(statWidth))
                    Text("${team.gd}", Modifier.width(statWidth))
                }

                HorizontalDivider()
            }
        }
    }
}


@Composable
fun Page2(modifier: Modifier = Modifier) = Placeholder("Page 2", modifier)

@Composable
fun Page3(modifier: Modifier = Modifier) = Placeholder("Page 3", modifier)

@Composable
private fun Placeholder(label: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Replace this with your own content.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
