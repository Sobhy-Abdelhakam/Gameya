package dev.sobhy.gameya.presentation.groupdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sobhy.gameya.domain.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupDetailsViewModel @Inject constructor(
    private val repository: GroupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val groupId: Long = checkNotNull(
        savedStateHandle["groupId"]
    )

    private val _state = MutableStateFlow(GroupDetailsState())
    val state = _state.asStateFlow()

    init {
        loadGroupDetails()
    }
    private fun loadGroupDetails() {
        viewModelScope.launch {
            repository.getGroupDetails(groupId)
                .collect { details ->

                    _state.update {
                        it.copy(
                            isLoading = false,
                            group = details.group,
                            members = details.members,
                            shares = details.shares,
                            cycles = details.cycles
                        )
                    }
                }
        }
    }
}