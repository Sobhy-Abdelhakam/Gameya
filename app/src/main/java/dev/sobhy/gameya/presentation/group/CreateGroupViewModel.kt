package dev.sobhy.gameya.presentation.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sobhy.gameya.domain.model.Group
import dev.sobhy.gameya.domain.model.Member
import dev.sobhy.gameya.domain.usecase.CreateGroupUseCase
import dev.sobhy.gameya.ui.model.MemberInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel @Inject constructor(private val createGroupUseCase: CreateGroupUseCase): ViewModel() {
    private val _state = MutableStateFlow(CreateGroupState())
    val state = _state.asStateFlow()

    fun onEvent(event: CreateGroupEvent) {
        when (event){
            is CreateGroupEvent.OnNameChanged -> {
                _state.update { it.copy(name = event.name) }
            }
            is CreateGroupEvent.OnContributionChanged -> {
                _state.update { it.copy(contribution = event.value) }
            }
            is CreateGroupEvent.OnCycleTypeChanged -> {
                _state.update { it.copy(cycleType = event.type) }
            }
            is CreateGroupEvent.OnStartDateChanged -> {
                _state.update { it.copy(startDate = event.date) }
            }
            CreateGroupEvent.OnAddMember -> {
                _state.update { it.copy(members = it.members + MemberInput()) }
            }
            is CreateGroupEvent.OnRemoveMember -> {
                _state.update { it.copy(members = it.members.toMutableList().apply { removeAt(event.index) }) }
            }
            is CreateGroupEvent.OnMemberNameChanged -> {
                updateMember(event.index){ memberInput ->
                    memberInput.copy(name = event.name)
                }
            }
            is CreateGroupEvent.OnMemberPhoneChanged -> {
                updateMember(event.index){ memberInput ->
                    memberInput.copy(phone = event.phone)
                }
            }
            is CreateGroupEvent.OnMemberSharesChanged -> {
                updateMember(event.index){ memberInput ->
                    memberInput.copy(shares = event.shares)
                }
            }
            CreateGroupEvent.OnSubmit -> createGroup()
        }
    }

    private fun updateMember(index: Int, update: (MemberInput) -> MemberInput){
        _state.update { state ->
            val memberList = state.members.toMutableList()
            memberList[index] = update(memberList[index])
            state.copy(members = memberList)
        }
    }

    private fun createGroup(){
        val current = _state.value

        //validation
        if (current.name.isBlank() || current.contribution.isBlank()){
            _state.update { it.copy(error = "Invalid input") }
            return
        }

        val contribution = current.contribution.toDoubleOrNull()
            ?: return _state.update { it.copy(error = "Invalid contribution") }

        val members = current.members.mapNotNull { input ->
            val shares = input.shares.toDoubleOrNull() ?: return@mapNotNull null

            Member(
                groupId = 0,
                name = input.name,
                phone = input.phone,
                shares = shares
            )
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val groupId = createGroupUseCase(
                    Group(
                        name = current.name,
                        contributionPerShare = contribution,
                        cycleType = current.cycleType,
                        startDate = current.startDate
                    ),
                    members
                )

                _state.update {
                    it.copy(isLoading = false, isSuccess = true, createdGroupId = groupId)
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }
}