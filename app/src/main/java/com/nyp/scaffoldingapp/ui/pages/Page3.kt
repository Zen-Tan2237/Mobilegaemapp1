package com.nyp.scaffoldingapp.ui.pages

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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Page 3: upcoming football fixtures. Data is deliberately labelled DEMO. */
@Composable
fun Page3(modifier: Modifier = Modifier) {
    val matches = remember { DemoMatchRepository().getUpcomingMatches() }
    val leagues = remember(matches) { listOf("All leagues") + matches.map { it.league }.distinct() }
    val dates = listOf("All dates", "Today", "Tomorrow", "Next 7 days")
    var selectedLeague by rememberSaveable { mutableStateOf("All leagues") }
    var selectedDate by rememberSaveable { mutableStateOf("All dates") }

    val shownMatches = remember(matches, selectedLeague, selectedDate) {
        matches.filter { match ->
            (selectedLeague == "All leagues" || match.league == selectedLeague) &&
                    matchesDateFilter(match.kickOffMillis, selectedDate)
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(top = 16.dp)) {
        Text(
            "Match schedule",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Upcoming fixtures • Demo data (not live)",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        Text("League", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leagues.forEach { league ->
                FilterChip(
                    selected = selectedLeague == league,
                    onClick = { selectedLeague = league },
                    label = { Text(league) }
                )
            }
        }
        Text("When", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dates.forEach { date ->
                FilterChip(
                    selected = selectedDate == date,
                    onClick = { selectedDate = date },
                    label = { Text(date) }
                )
            }
        }
        HorizontalDivider()
        Text(
            "${shownMatches.size} fixture${if (shownMatches.size == 1) "" else "s"}",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (shownMatches.isEmpty()) {
            Text(
                "No fixtures match these filters. Try another league or date.",
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(shownMatches, key = { it.id }) { match -> MatchFixtureCard(match) }
            }
        }
    }
}

@Composable
private fun MatchFixtureCard(match: FootballMatch) {
    val day = remember(match.kickOffMillis) { formatDate(match.kickOffMillis, "EEE, d MMM yyyy") }
    val time = remember(match.kickOffMillis) { formatDate(match.kickOffMillis, "HH:mm") }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(match.league, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(day, style = MaterialTheme.typography.bodySmall)
                Text(time, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Text(match.homeTeam, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("vs", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(match.awayTeam, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(match.venue, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun formatDate(timeMillis: Long, pattern: String): String =
    SimpleDateFormat(pattern, Locale.UK).format(Date(timeMillis))

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