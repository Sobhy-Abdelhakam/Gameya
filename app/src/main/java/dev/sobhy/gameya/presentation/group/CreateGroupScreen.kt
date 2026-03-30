package dev.sobhy.gameya.presentation.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sobhy.gameya.domain.enums.CycleType
import dev.sobhy.gameya.navigation.Screen

@Composable
fun CreateGroupScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CreateGroupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess && state.createdGroupId != null) {
            val route = Screen.GroupDetails.createRoute(state.createdGroupId!!)
            navController.navigate(route) {
                popUpTo(Screen.CreateGroup.route) {
                    inclusive = true
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Create a new Gam'eya",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Add your members and define contribution settings.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // ✅ Group Name
            item {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = {
                        viewModel.onEvent(CreateGroupEvent.OnNameChanged(it))
                    },
                    label = { Text("Group Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // ✅ Contribution
            item {
                OutlinedTextField(
                    value = state.contribution,
                    onValueChange = {
                        viewModel.onEvent(CreateGroupEvent.OnContributionChanged(it))
                    },
                    label = { Text("Contribution per Share") },
                    supportingText = { Text("Example: 500 EGP") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )
            }

            // ✅ Cycle Type (Better UX)
            item {
                Text("Collection frequency", style = MaterialTheme.typography.titleMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CycleType.entries.forEach { type ->
                        FilterChip(
                            selected = state.cycleType == type,
                            onClick = {
                                viewModel.onEvent(
                                    CreateGroupEvent.OnCycleTypeChanged(type)
                                )
                            },
                            label = {
                                Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        )
                    }
                }
            }

            // ✅ Members Header
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Members",
                        style = MaterialTheme.typography.titleMedium
                    )

                    TextButton(onClick = {
                        viewModel.onEvent(CreateGroupEvent.OnAddMember)
                    }) {
                        Text("Add Member")
                    }
                }
            }

            // ✅ Members List
            itemsIndexed(state.members) { index, member ->
                MemberItem(
                    member = member,
                    onNameChange = {
                        viewModel.onEvent(
                            CreateGroupEvent.OnMemberNameChanged(index, it)
                        )
                    },
                    onSharesChange = {
                        viewModel.onEvent(
                            CreateGroupEvent.OnMemberSharesChanged(index, it)
                        )
                    },
                    onRemove = {
                        viewModel.onEvent(
                            CreateGroupEvent.OnRemoveMember(index)
                        )
                    }
                )
            }

            // ✅ Submit Button
            item {
                Button(
                    onClick = {
                        viewModel.onEvent(CreateGroupEvent.OnSubmit)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Gam'eya")
                }
            }

            // ✅ Error
            state.error?.let {
                item {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // ✅ Success
            if (state.isSuccess) {
                item {
                    Text(
                        "Group created successfully.",
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }

        // ✅ Loading Overlay (Better UX)
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}