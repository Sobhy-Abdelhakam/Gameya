package dev.sobhy.gameya.presentation.groupdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sobhy.gameya.domain.model.Share
import dev.sobhy.gameya.domain.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
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
        observeGroupDetails()
    }

    // ✅ بدل load مرة واحدة → observe (reactive)
    private fun observeGroupDetails() {
        viewModelScope.launch {
            repository.getGroupDetails(groupId)
                .onStart {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Something went wrong"
                        )
                    }
                }
                .collect { details ->

                    val membersMap = details.members.associateBy { it.id }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            group = details.group,
                            members = details.members,
                            membersMap = membersMap, // 🔥 performance fix
                            shares = details.shares.sortedBy { it.orderIndex },
                            cycles = details.cycles
                        )
                    }
                }
        }
    }

    // ✅ UI state driven from ViewModel
    fun onTabChange(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    fun onReorder(from: Int, to: Int) {
        if (from == to) return

        val current = _state.value.shares.toMutableList()
        val safeTo = to.coerceIn(0, current.lastIndex)

        val item = current.removeAt(from)
        current.add(safeTo, item)

        val updated = current.mapIndexed { index, share ->
            share.copy(orderIndex = index)
        }

        _state.update {
            it.copy(shares = updated)
        }

        // ✅ async persist
        persistShareOrder()
    }

    private fun persistShareOrder() {
        val shares = _state.value.shares
        viewModelScope.launch {
            try {
                repository.updateSharesOrderBulk(
                    shares.map { it.id to it.orderIndex }
                )
            } catch (e: Exception) {
                // optional: rollback أو show error
                _state.update {
                    it.copy(error = "Failed to save order")
                }
            }
        }
    }
}