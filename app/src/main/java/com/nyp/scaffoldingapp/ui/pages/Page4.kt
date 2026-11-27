package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import com.nyp.scaffoldingapp.ui.theme.glassSurface
import com.nyp.scaffoldingapp.utils.ExternalURLHelper
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.nyp.scaffoldingapp.utils.YouTubeThumbnail
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.nyp.scaffoldingapp.data.model.MatchHighlights
import java.util.Calendar
import androidx.compose.foundation.lazy.items
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.text.style.TextAlign


@Composable
fun Page4(
    modifier: Modifier = Modifier,
    vm: HighlightsViewModel = viewModel()) {
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
    val leagues = remember (matches) { listOf("All leagues") + matches.map{it.league}.distinct() }
    val dates = remember { listOf("All dates", "Today", "Yesterday", "Last Week") }

    var selectedLeague by rememberSaveable { mutableStateOf("All leagues") }
    var selectedDate by rememberSaveable { mutableStateOf("All dates")}

    val shownResults = remember (matches, selectedLeague, selectedDate)
    {
        matches.filter { match ->
            (selectedLeague == "All leagues" || match.league == selectedLeague) &&
                    matchesDateFilter(match.dateMillis, selectedDate)
        }
    }


    Column(Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)
    ) {
        Text("Match Highlights",
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))
        Text("Watch the best moments in football",
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
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
            when (val current = state)
            {
                MatchListUiState.Loading -> {
                    item {
                        Box(modifier = Modifier
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

                            Button(onClick = {vm.load()}) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is MatchListUiState.Ready -> {
                    if (shownResults.isEmpty()){
                        item{
                            Text("No Highlights found",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    else {
                        items(shownResults, key = {it.id}) { match ->
                            MatchHighlightsCard(match)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchHighlightsCard(match : MatchHighlights){
    val day = remember(match.dateMillis) { formatDate(match.dateMillis, "EEE, d MMM yyyy") }
    val time = remember(match.dateMillis) { formatDate(match.dateMillis, "HH:mm") }
    Column(Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .glassSurface(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    )
    {
        Box(Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center)
        {
            YouTubeThumbnail(match.highlightURL)
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally)
        {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
            {
                Text(match.league,
                    modifier = Modifier.padding(horizontal = 2.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text (day,
                    modifier = Modifier.padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(time,
                    modifier = Modifier.padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
            {
                Text(
                    text = match.homeTeam,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyLarge

                )

                Text(
                    text = "${match.homeScore} - ${match.awayScore}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = match.awayTeam,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(match.description,
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            WatchHighlightsButton(match.highlightURL)
        }
    }
}

@Composable
fun WatchHighlightsButton(highlightUrl: String) {
    val context = LocalContext.current

    Button(
        onClick = {
            ExternalURLHelper.openURL(
                context = context,
                url = highlightUrl
            )
        },
        enabled = highlightUrl.isNotBlank()
    )
    {
        Text("Watch Highlights")
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
    val yesterday = (startOfToday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
	val lastWeek = (startOfToday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -7) }.timeInMillis
    return when (selection) {
        "Today" -> timeMillis >= start
        "Yesterday" -> timeMillis in yesterday..<start
        "Last Week" -> timeMillis in lastWeek..<start
        else -> true
    }
}
private fun formatDate(timeMillis: Long, pattern: String) : String = SimpleDateFormat(pattern, Locale.UK).format(Date(timeMillis))