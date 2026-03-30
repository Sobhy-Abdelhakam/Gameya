package dev.sobhy.gameya.presentation.groupdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.sobhy.gameya.R
import dev.sobhy.gameya.presentation.common.EmptyStateCard
import dev.sobhy.gameya.ui.theme.AppSpacing

@Composable
fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(AppSpacing.md),
        contentAlignment = Alignment.Center
    ) {
        EmptyStateCard(
            title = stringResource(R.string.group_details_empty_generic_title),
            description = message
        )
    }
}

@Composable
fun ErrorState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.error)
    }
}