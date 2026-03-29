package dev.sobhy.gameya.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sobhy.gameya.domain.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: GroupRepository
) : ViewModel(){
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            repository.getGroups().collect { groups ->

                val totalMoney = groups.sumOf {
                    it.contributionPerShare
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        groups = groups,
                        summary = DashboardSummary(
                            totalGroups = groups.size,
                            totalMoney = totalMoney
                        )
                    )
                }
            }
        }
    }
}