package dev.sobhy.gameya.presentation.groupdetails

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sobhy.gameya.R
import dev.sobhy.gameya.domain.model.Cycle
import dev.sobhy.gameya.domain.model.Member
import dev.sobhy.gameya.domain.model.Share
import dev.sobhy.gameya.navigation.Screen
import dev.sobhy.gameya.presentation.common.EmptyStateCard
import dev.sobhy.gameya.presentation.common.MoneyText
import dev.sobhy.gameya.presentation.common.SectionHeader
import dev.sobhy.gameya.presentation.common.StatusChip
import dev.sobhy.gameya.presentation.common.StatusTone
import dev.sobhy.gameya.ui.theme.AppSpacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GroupDetailsContent(
    navController: NavController,
    state: GroupDetailsState,
    onTabChange: (Int) -> Unit,
    onReorder: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppSpacing.xs)
    ) {
        GroupHeader(state)

        GroupTabs(
            selectedTab = state.selectedTab,
            onTabChange = onTabChange
        )

        when (state.selectedTab) {
            0 -> MembersTab(state)
            1 -> SharesTab(state, onReorder)
            2 -> CyclesTab(state, navController)
        }
    }
}

@Composable
private fun GroupHeader(state: GroupDetailsState) {
    val group = state.group ?: return
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AppSpacing.xs, bottom = AppSpacing.xs),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(AppSpacing.sm)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)
        ) {
            SectionHeader(
                title = group.name,
                subtitle = stringResource(
                    R.string.group_details_header_stats,
                    state.members.size,
                    state.shares.size,
                    state.cycles.size
                )
            )
            MoneyText(
                amount = group.contributionPerShare,
                emphasized = true,
                prefix = stringResource(R.string.group_details_prefix_contribution_share)
            )
        }
    }
}

@Composable
private fun GroupTabs(
    selectedTab: Int,
    onTabChange: (Int) -> Unit
) {
    val tabs = listOf(
        stringResource(R.string.group_tab_members),
        stringResource(R.string.group_tab_shares),
        stringResource(R.string.group_tab_cycles)
    )

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
        Box(modifier = Modifier.padding(AppSpacing.sm)) {
            EmptyStateCard(
                title = stringResource(R.string.group_details_empty_members_title),
                description = stringResource(R.string.group_details_empty_members_body)
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        item {
            SectionHeader(title = stringResource(R.string.group_details_members_section))
        }
        items(state.members, key = { it.id }) { member ->
            Box(modifier = Modifier.animateItem()) {
                MemberItem(member)
            }
        }
    }
}
@Composable
private fun SharesTab(
    state: GroupDetailsState,
    onReorder: (Int, Int) -> Unit
) {
    if (state.shares.isEmpty()) {
        Box(modifier = Modifier.padding(AppSpacing.sm)) {
            EmptyStateCard(
                title = stringResource(R.string.group_details_empty_shares_title),
                description = stringResource(R.string.group_details_empty_shares_body)
            )
        }
        return
    }

    val listState = rememberLazyListState()

    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        item {
            SectionHeader(
                title = stringResource(R.string.group_details_shares_section),
                subtitle = stringResource(R.string.group_details_shares_hint)
            )
        }

        itemsIndexed(
            items = state.shares,
            key = { _, item -> item.id }
        ) { index, share ->

            val member = state.members.find { it.id == share.memberId }
            val isDragging = draggedIndex == index

            Box(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()

                    // ✨ حركة العنصر أثناء السحب
                    .graphicsLayer {
                        translationY = if (isDragging) offsetY else 0f
                        scaleX = if (isDragging) 1.03f else 1f
                        scaleY = if (isDragging) 1.03f else 1f
                        alpha = if (isDragging) 0.95f else 1f
                    }

                    // ✨ drag logic
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(

                            // 👉 بداية السحب
                            onDragStart = {
                                draggedIndex = index
                            },

                            // 👉 أثناء السحب
                            onDrag = { change, dragAmount ->
                                change.consume()

                                val currentIndex = draggedIndex ?: return@detectDragGesturesAfterLongPress

                                offsetY += dragAmount.y

                                val targetIndex = calculateNewIndex(
                                    currentIndex = currentIndex,
                                    offsetY = offsetY,
                                    itemHeightPx = 160f // 👈 هنتكلم عنها تحت
                                )

                                if (targetIndex != currentIndex &&
                                    targetIndex in state.shares.indices
                                ) {
                                    onReorder(currentIndex, targetIndex)

                                    draggedIndex = targetIndex
                                    offsetY = 0f
                                }
                            },

                            // 👉 نهاية السحب
                            onDragEnd = {
                                draggedIndex = null
                                offsetY = 0f
                            },

                            onDragCancel = {
                                draggedIndex = null
                                offsetY = 0f
                            }
                        )
                    }
            ) {
                ShareItem(
                    share = share,
                    member = member,
                    isDragging = isDragging
                )
            }
        }
    }
}
private fun calculateNewIndex(
    currentIndex: Int,
    offsetY: Float,
    itemHeightPx: Float
): Int {
    return when {
        offsetY > itemHeightPx -> currentIndex + 1
        offsetY < -itemHeightPx -> currentIndex - 1
        else -> currentIndex
    }
}
@Composable
private fun CyclesTab(state: GroupDetailsState, navController: NavController) {

    if (state.cycles.isEmpty()) {
        Box(modifier = Modifier.padding(AppSpacing.sm)) {
            EmptyStateCard(
                title = stringResource(R.string.group_details_empty_cycles_title),
                description = stringResource(R.string.group_details_empty_cycles_body)
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        item {
            SectionHeader(title = stringResource(R.string.group_details_cycles_section))
        }
        items(state.cycles, key = { it.id }) { cycle ->
            Box(modifier = Modifier.animateItem()) {
                val payoutShare = state.shares.firstOrNull { it.id == cycle.payoutShareId }
                val payoutMember = payoutShare?.let { share -> state.membersMap[share.memberId] }
                CycleItem(cycle, payoutMember, navController)
            }
        }
    }
}

@Composable
private fun MemberItem(member: Member) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = member.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = stringResource(
                    R.string.group_details_member_shares,
                    member.shares.toCleanNumber()
                ),
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
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                StatusChip(
                    label = stringResource(
                        R.string.group_details_payout_order_chip,
                        share.orderIndex + 1
                    ),
                    tone = StatusTone.NEUTRAL
                )
                Text(
                    text = member?.name
                        ?: stringResource(R.string.group_details_unknown_member),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = stringResource(
                    R.string.group_details_share_fraction,
                    share.fraction.toCleanNumber()
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
@Composable
private fun CycleItem(
    cycle: Cycle,
    payoutMember: Member?,
    navController: NavController
) {
    val openPaymentsA11y = stringResource(
        R.string.group_cycle_open_payments_a11y,
        cycle.cycleIndex + 1
    )
    val payoutLabel = payoutMember?.name
        ?: stringResource(R.string.group_details_payout_not_assigned)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.xxs)
            .semantics { contentDescription = openPaymentsA11y },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = {
            navController.navigate(
                Screen.CyclePayments.createRoute(cycle.id)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(AppSpacing.sm)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)
        ) {
            Text(
                text = stringResource(
                    R.string.group_details_cycle_line,
                    cycle.cycleIndex + 1,
                    cycle.date.toMonthYear()
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.group_details_cycle_payout_to, payoutLabel),
                style = MaterialTheme.typography.bodyMedium
            )
            if (cycle.isClosed) {
                StatusChip(
                    label = stringResource(R.string.group_details_status_closed),
                    tone = StatusTone.SUCCESS
                )
            } else {
                StatusChip(
                    label = stringResource(R.string.group_details_status_active),
                    tone = StatusTone.NEUTRAL
                )
            }
        }
    }
}

private fun Double.toCleanNumber(): String {
    return if (this % 1.0 == 0.0) this.toInt().toString() else this.toString()
}

private fun Long.toMonthYear(): String {
    val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    return formatter.format(Date(this))
}
