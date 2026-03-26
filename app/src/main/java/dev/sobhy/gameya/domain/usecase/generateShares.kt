package dev.sobhy.gameya.domain.usecase

import dev.sobhy.gameya.domain.model.Member
import dev.sobhy.gameya.domain.model.Share

fun generateShares(members: List<Member>): List<Share> {
    val shares = mutableListOf<Share>()
    var order = 0

    members.forEach { member ->
        val fullShares = member.shares.toInt()
        val remainder = member.shares - fullShares

        repeat(fullShares) {
            shares.add(
                Share(
                    groupId = member.groupId,
                    memberId = member.id,
                    fraction = 1.0,
                    orderIndex = order++
                )
            )
        }

        if (remainder > 0) {
            shares.add(
                Share(
                    groupId = member.groupId,
                    memberId = member.id,
                    fraction = remainder,
                    orderIndex = order++
                )
            )
        }
    }

    return shares
}