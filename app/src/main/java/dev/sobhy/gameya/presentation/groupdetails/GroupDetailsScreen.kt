package dev.sobhy.gameya.presentation.groupdetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GroupDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> LoadingState()
        state.error != null -> ErrorState(state.error?: "an error happen")
        else -> GroupDetailsContent(
            state = state,
            onTabChange = viewModel::onTabChange,
            onReorder = viewModel::onReorder
        )
    }
}