package com.nyp.scaffoldingapp.ui.pages

import android.R
import android.service.autofill.FieldClassification
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyp.scaffoldingapp.data.model.FootballMatch
import com.nyp.scaffoldingapp.data.repository.DemoMatchRepository
import com.nyp.scaffoldingapp.ui.theme.glassSurface
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment

/** Page 3: upcoming football fixtures. Data is deliberately labelled DEMO. */
@Composable
fun  Page3(modifier: Modifier = Modifier, vm: ScheduleViewModel = viewModel()) {
    // Use the match repository
    val repository = remember { DemoMatchRepository() }
    // Get the matches using the repository

    val state by vm.state.collectAsState()

    DisposableEffect(vm) {
        vm.resumeIfNeeded()

        onDispose {
            vm.stopLoading()
        }
    }

    val matches = when (val current = state) {
        is MatchListUiState.Ready -> current.matches
        else -> emptyList()
    }
    // Get all the leagues from the repository and all distinct league names
    val leagues = remember(matches){listOf("All leagues") + matches.map{it.league}.distinct()}
    // List of all selectable dates
    val dates = listOf("All dates", "Today", "Tomorrow", "Next 7 days")
    // Tracks the current selected league
    var selectedLeague by rememberSaveable { mutableStateOf("All leagues") }
    var selectedDate by rememberSaveable { mutableStateOf("All dates") }

    val shownMatches = remember(matches, selectedLeague, selectedDate) {
        matches.filter { match ->
            (selectedLeague == "All leagues" || match.league == selectedLeague) &&
                    matchesDateFilter(match.kickOffMillis, selectedDate)
        }
    }

    Column(Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)
    ) {
        Text(
            "Scheduled Matches",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(15.dp))
        Text(
            "Upcoming Fixtures",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))
        Text(
            "League",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row( Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            leagues.forEach { league ->
                FilterChip(
                    selected = selectedLeague == league,
                    onClick = {selectedLeague = league},
                    label = {Text(league)}
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "When",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            dates.forEach { date ->
                FilterChip(
                    selected = selectedDate == date,
                    onClick = {selectedDate = date},
                    label = {Text(date)}
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (val current  = state) {
                MatchListUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        )
                        {
                            CircularProgressIndicator()
                        }
                    }
                }

                is MatchListUiState.Failed -> {
                    item{
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            Text(current.message)

                            Button(onClick = {vm.load()}){
                                Text("Retry")
                            }
                        }
                    }
                }

                is MatchListUiState.Ready -> {
                    if (shownMatches.isEmpty()){
                        item {
                            Text("No Matches Found",
                                modifier = Modifier.padding(16.dp))
                        }
                    }
                    else
                    {
                        items(shownMatches, key = {it.id}) { match ->
                            MatchFixtureCard(match)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchFixtureCard(match: FootballMatch) {
    val day = remember(match.kickOffMillis) { formatDate(match.kickOffMillis, "EEE, d MMM yyyy") }
    val time = remember(match.kickOffMillis) { formatDate(match.kickOffMillis, "HH:mm") }
    Spacer(Modifier.height(6.dp))
    Card(Modifier.fillMaxWidth())
    {
        Column(Modifier.fillMaxWidth() .glassSurface())
        {
            Spacer(Modifier.height(12.dp))
            // This is when the
            Row{
                Text("Date:",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(day,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.height(5.dp))
            // and this is when the time is the
            Row{
                Text("Time:",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(time,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.height(5.dp))
            // and when the time is the
            Row{
                Text("Venue:",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(match.venue,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.height(5.dp))
            // Man Utd is shit
            Text(match.homeTeam,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(5.dp))
            Text("VS",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(5.dp))
            Text(match.awayTeam,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

private fun matchesDateFilter(timeMillis: Long, selection: String): Boolean {
    if (selection == "All dates") return true
    val startOfToday = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val start = startOfToday.timeInMillis
    val tomorrow = (startOfToday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
    val dayAfter = (startOfToday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 2) }.timeInMillis
    val nextWeek = (startOfToday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 7) }.timeInMillis
    return when (selection) {
        "Today" -> timeMillis in start until tomorrow
        "Tomorrow" -> timeMillis in tomorrow until dayAfter
        "Next 7 days" -> timeMillis in start until nextWeek
        else -> true
    }
}

private fun formatDate(timeMillis: Long, pattern: String) : String = SimpleDateFormat(pattern, Locale.UK).format(Date(timeMillis))