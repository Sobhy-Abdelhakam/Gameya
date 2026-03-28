package dev.sobhy.gameya.presentation.groupdetails

import dev.sobhy.gameya.domain.model.Cycle
import dev.sobhy.gameya.domain.model.Group
import dev.sobhy.gameya.domain.model.Member
import dev.sobhy.gameya.domain.model.Share

data class GroupDetailsState(
    val isLoading: Boolean = true,
    val group: Group? = null,
    val members: List<Member> = emptyList(),
    val shares: List<Share> = emptyList(),
    val cycles: List<Cycle> = emptyList(),
    val error: String? = null
)
