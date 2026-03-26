package dev.sobhy.gameya.domain.usecase

import dev.sobhy.gameya.domain.model.Cycle
import dev.sobhy.gameya.domain.model.Group
import dev.sobhy.gameya.domain.model.Share

fun generateCycles(group: Group, shares: List<Share>): List<Cycle> {
    return shares.mapIndexed { index, share ->
        Cycle(
            groupId = group.id,
            cycleIndex = index,
            date = calculateDate(group.startDate, index, group.cycleType),
            payoutShareId = share.id,
            isClosed = false,
        )
    }
}