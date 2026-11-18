
package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Page1(
    modifier: Modifier = Modifier,
    vm: StandingsViewModel = viewModel()
) {

    // Read the standings state from the ViewModel
    val state by vm.state.collectAsState()

    // Handle leaving and returning to the page
    DisposableEffect(vm) {
        vm.resumeIfNeeded()

        onDispose {
            vm.stopLoading()
        }
    }

    val posWidth = 30.dp
    val clubWidth = 130.dp
    val statWidth = 45.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Premier League", fontSize = 24.sp)

        Spacer(Modifier.height(16.dp))

        // Choose what to display based on the API state
        when (val current = state) {

            StandingsUiState.Loading -> {

                // Show placeholder rows while loading
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

                if (current.teams.isEmpty()) {
                    Text("No standings available")

                    Button(onClick = { vm.load() }) {
                        Text("Retry")
                    }

                } else {

                    // Display the standings table
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
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
                        current.teams.forEachIndexed { index, team ->

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
        }
    }
}
