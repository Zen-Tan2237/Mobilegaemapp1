
package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Page2(
    modifier: Modifier = Modifier,
    vm: StatsViewModel = viewModel()
) {

    val tabs = listOf("Goals", "Assists", "Yellow Cards", "Red Cards")
    val keys = listOf("goals", "assists", "yellowCards", "redCards")


    // Names shown on the league tabs
    val leagueNames = listOf(
        "Premier League",
        "La Liga",
        "Serie A",
        "Bundesliga",
        "Ligue 1"
    )

    // Keys used by the API
    val leagueKeys = listOf(
        "premierLeague",
        "laLiga",
        "serieA",
        "bundesliga",
        "ligue1"
    )


    // Read data and selected category from the ViewModel
    val state by vm.state.collectAsState()
    val selectedTab = vm.selectedTab

    // Handle leaving and returning to the page
    DisposableEffect(vm) {
        vm.resumeIfNeeded()

        onDispose {
            vm.stopLoading()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Tabs for switching between leagues
        ScrollableTabRow(
            selectedTabIndex = leagueKeys.indexOf(vm.selectedLeague),
            edgePadding = 0.dp
        ) {

            leagueNames.forEachIndexed { index, name ->

                Tab(
                    selected = vm.selectedLeague == leagueKeys[index],
                    onClick = { vm.selectLeague(leagueKeys[index]) },
                    text = { Text(name) }
                )

            }

        }

        Spacer(Modifier.height(12.dp))


        // Tabs for switching between statistics
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { vm.selectTab(index) },
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

        // Display the current API state
        when (val current = state) {

            StatsUiState.Loading -> {
                CircularProgressIndicator()
            }

            is StatsUiState.Failed -> {
                Text(current.message)

                Spacer(Modifier.height(12.dp))

                Button(onClick = { vm.load() }) {
                    Text("Retry")
                }
            }

            is StatsUiState.Ready -> {

                // Get players from the chosen league and category
                val players = current.stats[vm.selectedLeague] ?.get(keys[selectedTab])
                    .orEmpty()

                if (players.isEmpty()) {
                    Text("No players found")

                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = players,
                            key = { "${it.name}-${it.club}" }
                        ) { player ->

                            PlayerStatRow(player)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

// Display one player's name, club and statistic
@Composable
fun PlayerStatRow(
    player: PlayerStat,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
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
