package dev.sobhy.gameya.presentation.group

import dev.sobhy.gameya.domain.enums.CycleType

sealed class CreateGroupEvent {

    data class OnNameChanged(val name: String) : CreateGroupEvent()
    data class OnContributionChanged(val value: String) : CreateGroupEvent()

    data class OnCycleTypeChanged(val type: CycleType) : CreateGroupEvent()
    data class OnStartDateChanged(val date: Long) : CreateGroupEvent()

    object OnAddMember : CreateGroupEvent()
    data class OnRemoveMember(val index: Int) : CreateGroupEvent()

    data class OnMemberNameChanged(val index: Int, val name: String) : CreateGroupEvent()
    data class OnMemberPhoneChanged(val index: Int, val phone: String) : CreateGroupEvent()
    data class OnMemberSharesChanged(val index: Int, val shares: String) : CreateGroupEvent()

    object OnSubmit : CreateGroupEvent()
}