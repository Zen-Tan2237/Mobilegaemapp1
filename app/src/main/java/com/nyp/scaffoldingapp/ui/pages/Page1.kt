
package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Page1(
    modifier: Modifier = Modifier,
    vm: StandingsViewModel = viewModel()
) {

    // Get the data from the ViewModel
    val state by vm.state.collectAsState()
    val current = state

    // Check the search input
    val searchText = vm.searchQuery.trim()

    val searchError = when {
        vm.searchQuery.isEmpty() -> null
        searchText.length < 2 -> "Enter at least 2 characters"
        else -> null
    }

    // Find matching clubs from all five leagues
    val matches = if (current is StandingsUiState.Ready && searchText.length >= 2) {
        current.leagues.flatMap { (leagueKey, clubs) ->
            clubs.filter {
                it.name.contains(searchText, ignoreCase = true)
            }.map {
                leagueKey to it.name
            }
        }
    } else {
        emptyList<Pair<String, String>>()
    }

    // Handle leaving and returning to the page
    DisposableEffect(vm) {
        vm.resumeIfNeeded()

        onDispose {
            vm.stopLoading()
        }
    }

    val posWidth = 30.dp
    val clubWidth = 180.dp
    val statWidth = 45.dp

    val leagueNames = listOf(
        "Premier League", "La Liga", "Serie A", "Bundesliga", "Ligue 1"
    )

    val leagueKeys = listOf(
        "premierLeague", "laLiga", "serieA", "bundesliga", "ligue1"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Page title
        Text("Football League Tables", fontSize = 24.sp)

        Spacer(Modifier.height(12.dp))

        // Search across all five leagues
        OutlinedTextField(
            value = vm.searchQuery,
            onValueChange = { vm.updateSearchQuery(it) },
            label = { Text("Search clubs") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = searchError != null,
            supportingText = {
                if (searchError != null) {
                    Text(searchError)
                }
            }
        )

        // Show matching clubs below the search bar
        if (current is StandingsUiState.Ready && searchText.length >= 2) {

            if (matches.isEmpty()) {
                Text("No clubs found")
            } else {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 160.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    matches.take(5).forEach { (leagueKey, clubName) ->

                        val leagueIndex = leagueKeys.indexOf(leagueKey)
                        val leagueName = leagueNames.getOrElse(leagueIndex) { leagueKey }

                        TextButton(
                            onClick = { vm.openClub(leagueKey, clubName) }
                        ) {
                            Text("$clubName ($leagueName)")
                        }

                    }

                }

            }

        }

        Spacer(Modifier.height(12.dp))

        // Switch between leagues
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

        // Show the selected club and a button to reset
        if (vm.selectedClub != null) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Showing: ${vm.selectedClub}",
                    modifier = Modifier.weight(1f)
                )

                TextButton(onClick = { vm.showAllClubs() }) {
                    Text("Show all")
                }

            }

            Spacer(Modifier.height(8.dp))

        }

        // Display the API state
        when (current) {

            StandingsUiState.Loading -> {

                // Show placeholders while loading
                repeat(5) {

                    Spacer(
                        Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(0.8f)
                            .height(20.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                }

            }

            is StandingsUiState.Failed -> {

                Text(current.message)

                Spacer(Modifier.height(12.dp))

                Button(onClick = { vm.load() }) {
                    Text("Retry")
                }

            }

            is StandingsUiState.Ready -> {

                // Get the teams from the selected league
                val teams = current.leagues[vm.selectedLeague].orEmpty()

                if (teams.isEmpty()) {

                    Text("No standings available")

                    Button(onClick = { vm.load() }) {
                        Text("Retry")
                    }

                } else {

                    // Keep the original positions when filtering
                    val visibleTeams = teams.withIndex().filter {
                        vm.selectedClub == null || it.value.name == vm.selectedClub
                    }

                    if (visibleTeams.isEmpty()) {
                        Text("No clubs found")
                    }

                    // Display the standings table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .horizontalScroll(rememberScrollState())
                            .verticalScroll(rememberScrollState())
                    ) {

                        // Table header
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

                        // Display each team
                        visibleTeams.forEach { (index, team) ->

                            Row(
                                modifier = Modifier.padding(vertical = 10.dp)
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

        }

    }

}
