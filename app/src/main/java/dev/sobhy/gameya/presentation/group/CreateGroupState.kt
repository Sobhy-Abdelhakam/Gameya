package dev.sobhy.gameya.presentation.group

import dev.sobhy.gameya.domain.enums.CycleType
import dev.sobhy.gameya.ui.model.MemberInput

data class CreateGroupState(
    val name: String = "",
    val contribution: String = "",
    val cycleType: CycleType = CycleType.MONTHLY,
    val startDate: Long = System.currentTimeMillis(),

    val members: List<MemberInput> = emptyList(),

    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,

    val createdGroupId: Long? = null
)
