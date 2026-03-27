package dev.sobhy.gameya.domain.usecase

import dev.sobhy.gameya.domain.model.Group
import dev.sobhy.gameya.domain.model.Member
import dev.sobhy.gameya.domain.repository.GroupRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(
        group: Group,
        members: List<Member>
    ) {
        require(members.isNotEmpty())
        require(members.all { it.shares > 0 })

        repository.createGroup(group, members)
    }
}