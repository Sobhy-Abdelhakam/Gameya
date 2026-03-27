package dev.sobhy.gameya.presentation.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.sobhy.gameya.domain.enums.CycleType

@Composable
fun CreateGroupScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateGroupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = state.name,
            onValueChange = {
                viewModel.onEvent(CreateGroupEvent.OnNameChanged(it))
            },
            label = { Text("Group Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.contribution,
            onValueChange = {
                viewModel.onEvent(CreateGroupEvent.OnContributionChanged(it))
            },
            label = { Text("Contribution per Share") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Text("Cycle Type")

        Row {
            CycleType.entries.forEach { type ->
                Button(
                    onClick = {
                        viewModel.onEvent(
                            CreateGroupEvent.OnCycleTypeChanged(type)
                        )
                    }
                ) {
                    Text(type.name)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text("Members")

        state.members.forEachIndexed { index, member ->
            Column {
                OutlinedTextField(
                    value = member.name,
                    onValueChange = {
                        viewModel.onEvent(
                            CreateGroupEvent.OnMemberNameChanged(index, it)
                        )
                    },
                    label = { Text("Name") }
                )

                OutlinedTextField(
                    value = member.shares,
                    onValueChange = {
                        viewModel.onEvent(
                            CreateGroupEvent.OnMemberSharesChanged(index, it)
                        )
                    },
                    label = { Text("Shares (0.25, 1, 2...)") }
                )

                Button(
                    onClick = {
                        viewModel.onEvent(
                            CreateGroupEvent.OnRemoveMember(index)
                        )
                    }
                ) {
                    Text("Remove")
                }

                Spacer(Modifier.height(8.dp))
            }
        }
        Button(
            onClick = {
                viewModel.onEvent(CreateGroupEvent.OnAddMember)
            }
        ) {
            Text("Add Member")
        }
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onEvent(CreateGroupEvent.OnSubmit)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Group")
        }
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.error?.let {
            Text(text = it, color = Color.Red)
        }

        if (state.isSuccess) {
            Text("✅ Group Created Successfully")
        }
    }
}