package dev.sobhy.gameya.presentation.dashboard

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sobhy.gameya.R
import dev.sobhy.gameya.domain.model.Group
import dev.sobhy.gameya.navigation.Screen
import dev.sobhy.gameya.presentation.common.EmptyStateCard
import dev.sobhy.gameya.presentation.common.MoneyText
import dev.sobhy.gameya.presentation.common.SectionHeader
import dev.sobhy.gameya.ui.theme.AppSpacing


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Screen.DataSafety.route) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.data_safety_nav_content_description)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateGroup.route) }
            ) {
                Text("+")
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
        contentPadding = PaddingValues(
            start = AppSpacing.sm,
            top = padding.calculateTopPadding() + AppSpacing.xs,
            end = AppSpacing.sm,
            bottom = padding.calculateBottomPadding() + 88.dp
        ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        modifier = Modifier.fillMaxSize()
    ) {

        item {
            SummaryCard(state.summary)
        }

        items(state.groups) { group ->
            GroupCard(
                group = group,
                onClick = { onGroupClick(group.id) }
            )
        }
    }
}

@Composable
fun GroupCard(
    group: Group,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.sm)
        ) {
            SectionHeader(
                title = group.name,
                subtitle = group.cycleType.name.lowercase().replaceFirstChar { it.uppercase() }
            )

            Spacer(Modifier.height(AppSpacing.xs))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                MoneyText(
                    amount = group.contributionPerShare,
                    prefix = stringResource(R.string.prefix_contribution)
                )

                Text(
                    text = stringResource(R.string.dashboard_group_view_details),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
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
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyStateCard(
            title = stringResource(R.string.dashboard_empty_title),
            description = stringResource(R.string.dashboard_empty_body)
        )

        Spacer(Modifier.height(AppSpacing.md))

        Button(
            onClick = onCreateClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.sm)
        ) {
            Text(stringResource(R.string.dashboard_create_group))
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