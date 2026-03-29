package dev.sobhy.gameya.presentation.groupdetails

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sobhy.gameya.domain.model.Cycle
import dev.sobhy.gameya.domain.model.Member
import dev.sobhy.gameya.domain.model.Share
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun GroupDetailsContent(
    state: GroupDetailsState,
    onTabChange: (Int) -> Unit,
    onReorder: (Int, Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        GroupTabs(
            selectedTab = state.selectedTab,
            onTabChange = onTabChange
        )

        when (state.selectedTab) {
            0 -> MembersTab(state)
            1 -> SharesTab(state, onReorder)
            2 -> CyclesTab(state)
        }
    }
}

@Composable
private fun GroupTabs(
    selectedTab: Int,
    onTabChange: (Int) -> Unit
) {
    val tabs = listOf("Members", "Shares", "Cycles")

    PrimaryTabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabChange(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
private fun MembersTab(state: GroupDetailsState) {

    if (state.members.isEmpty()) {
        EmptyState("No members yet")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.members, key = { it.id }) { member ->
            MemberItem(member)
        }
    }
}
@Composable
private fun SharesTab(
    state: GroupDetailsState,
    onReorder: (Int, Int) -> Unit
) {
    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to -> onReorder(from.index, to.index) }
    )

    LazyColumn(
        state = reorderState.listState,
        modifier = Modifier
            .reorderable(reorderState)
            .detectReorderAfterLongPress(reorderState),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(state.shares, key = { it.id }) { share ->

            val member = state.members.find { it.id == share.memberId }

            ReorderableItem(reorderState, key = share.id) { isDragging ->
                ShareItem(share, member, isDragging)
            }
        }
    }
}
@Composable
private fun CyclesTab(state: GroupDetailsState) {

    if (state.cycles.isEmpty()) {
        EmptyState("No cycles yet")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.cycles, key = { it.id }) { cycle ->
            CycleItem(cycle)
        }
    }
}

@Composable
private fun MemberItem(member: Member) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = member.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${member.shares} shares",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
@Composable
private fun ShareItem(
    share: Share,
    member: Member?,
    isDragging: Boolean
) {
    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)

    Card(
        elevation = CardDefaults.cardElevation(elevation),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = "Order ${share.orderIndex + 1}",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = member?.name ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = "${share.fraction} share",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
@Composable
private fun CycleItem(cycle: Cycle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Cycle ${cycle.cycleIndex + 1}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
