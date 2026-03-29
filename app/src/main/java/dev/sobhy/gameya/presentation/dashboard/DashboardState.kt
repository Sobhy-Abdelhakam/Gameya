package dev.sobhy.gameya.presentation.dashboard

import dev.sobhy.gameya.domain.model.Group

data class DashboardState(
    val isLoading: Boolean = true,
    val groups: List<Group> = emptyList(),
    val summary: DashboardSummary = DashboardSummary(),
    val error: String? = null,
)
