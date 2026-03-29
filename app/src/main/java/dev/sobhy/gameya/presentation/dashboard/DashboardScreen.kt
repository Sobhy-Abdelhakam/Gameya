package dev.sobhy.gameya.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sobhy.gameya.domain.model.Group
import dev.sobhy.gameya.navigation.Screen


@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateGroup.route) }
            ) {
                Text("+")
//                Icon(Icons.Default.Add, contentDescription = "Add Group")
            }
        }
    ) { padding ->

        when {
            state.isLoading -> {
                LoadingState()
            }

            state.groups.isEmpty() -> {
                EmptyState(
                    modifier = Modifier.padding(padding),
                    onCreateClick = {
                        navController.navigate(Screen.CreateGroup.route)
                    }
                )
            }

            else -> {
                DashboardContent(
                    state = state,
                    padding = padding,
                    onGroupClick = { groupId ->
                        navController.navigate(
                            Screen.GroupDetails.createRoute(groupId)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    state: DashboardState,
    padding: PaddingValues,
    onGroupClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {

        item {
            SummaryCard(state.summary)
        }

        items(state.groups) { group ->
            GroupItem(
                group = group,
                onClick = { onGroupClick(group.id) }
            )
        }
    }
}

@Composable
fun GroupItem(
    group: Group,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = group.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "💰 ${group.contributionPerShare}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "🔁 ${group.cycleType}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "📭",
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "No groups yet",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Start by creating your first Gam'eya",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = onCreateClick) {
            Text("Create Group")
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}