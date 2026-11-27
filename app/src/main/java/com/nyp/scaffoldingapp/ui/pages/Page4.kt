package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.nyp.scaffoldingapp.ui.theme.glassSurface
import com.nyp.scaffoldingapp.utils.ExternalURLHelper
import androidx.compose.foundation.lazy.LazyColumn
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
import com.nyp.scaffoldingapp.utils.shortTeamName
import com.nyp.scaffoldingapp.utils.rollChance
import com.nyp.scaffoldingapp.ui.components.FilterDropdown


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
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.8).sp,
            modifier = Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))
        Text("Watch the best moments in football",
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = (-0.8).sp,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
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
                            Text(current.message,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                letterSpacing = (-0.8).sp
                            )

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
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                letterSpacing = (-0.8).sp
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = (-0.8).sp,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text (day,
                    modifier = Modifier.padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = (-0.8).sp,
                    fontSize = 12.sp
                )
                Text(time,
                    modifier = Modifier.padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = (-0.8).sp,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
            {
                Text(
                    text = shortTeamName(match.homeTeam),
                    modifier = Modifier.weight(1f).padding(vertical = 3.dp),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = (-0.8).sp,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium

                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "${match.homeScore} - ${match.awayScore}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = (-0.8).sp,
                    fontSize = 28.sp
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = shortTeamName(match.awayTeam),
                    modifier = Modifier.weight(1f).padding(vertical = 3.dp),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = (-0.8).sp,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(match.description,
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = (-0.8).sp,
                fontSize = 14.sp
            )

            WatchHighlightsButton(match.highlightURL)
        }
    }
}

@Composable
fun WatchHighlightsButton(highlightUrl: String) {
    val context = LocalContext.current

    Button(
        onClick = {
            // I think I very funny so I can rick roll u
            val url = if (rollChance(5)){
                "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            }
            else{
                highlightUrl
            }
            ExternalURLHelper.openURL(
                context = context,
                url = url
            )
        },
        enabled = highlightUrl.isNotBlank()
    )
    {
        Text("Watch Highlights",
            letterSpacing = (-0.8).sp,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
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