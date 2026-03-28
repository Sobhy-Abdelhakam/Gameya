package dev.sobhy.gameya.presentation.groupdetails

import android.R.id.tabs
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun GroupDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Members", "Shares", "Cycles")

    Column {
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        when (selectedTab) {
            0 -> {
                LazyColumn {
                    items(state.members) { member ->
                        Text(
                            text = "${member.name} - ${member.shares} shares",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            1 -> {
                LazyColumn {
                    items(state.shares) { share ->

                        val member = state.members.find { it.id == share.memberId }

                        Text(
                            text = buildString {
                                append("Order ${share.orderIndex + 1} - ")

                                append(member?.name ?: "Unknown")

                                append(" (${share.fraction} share)")
                            },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            2 -> {
                LazyColumn {
                    items(state.cycles) { cycle ->
                        Text(
                            text = "Cycle ${cycle.cycleIndex + 1}",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}