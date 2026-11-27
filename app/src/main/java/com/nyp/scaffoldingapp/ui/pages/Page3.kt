package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import com.nyp.scaffoldingapp.data.model.FootballMatch
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
import com.nyp.scaffoldingapp.utils.shortTeamName
import com.nyp.scaffoldingapp.ui.components.FilterDropdown
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.HorizontalDivider

@Composable
fun  Page3(modifier: Modifier = Modifier, vm: ScheduleViewModel = viewModel()) {
    val pageHorizontalPadding = 12.dp
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
            modifier = Modifier.padding(horizontal = pageHorizontalPadding),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = (-0.8).sp,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Upcoming Fixtures",
            modifier = Modifier.padding(horizontal = pageHorizontalPadding),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = (-0.8).sp,
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = pageHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterDropdown(
                label = "League",
                options = leagues,
                selected = selectedLeague,
                onSelected = { selectedLeague = it },
                modifier = Modifier.weight(1f)
            )

            FilterDropdown(
                label = "When",
                options = dates,
                selected = selectedDate,
                onSelected = { selectedDate = it },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 12.dp, end = 12.dp, bottom = 16.dp
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
                            Text(current.message,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                letterSpacing = (-0.8).sp
                            )

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
fun MatchFixtureCard(match: FootballMatch) {

    val date = SimpleDateFormat(
        "EEE, dd MMM yyyy",
        Locale.getDefault()
    ).format(Date(match.kickOffMillis))

    val time = SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    ).format(Date(match.kickOffMillis))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassSurface(cornerRadius = 24)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // League and date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = match.league,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = (-0.8).sp
            )

            Text(
                text = date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = (-0.8).sp
            )
        }

        // Teams and kick-off time
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = shortTeamName(match.homeTeam),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.8).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.8).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Kick-off",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = shortTeamName(match.awayTeam),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.8).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

        // Venue
        Text(
            text = match.venue,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            letterSpacing = (-0.8).sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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