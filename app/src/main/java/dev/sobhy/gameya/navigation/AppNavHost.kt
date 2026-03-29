package dev.sobhy.gameya.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.sobhy.gameya.presentation.group.CreateGroupScreen
import dev.sobhy.gameya.presentation.groupdetails.GroupDetailsScreen

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.CreateGroup.route, modifier = modifier){
        composable(Screen.CreateGroup.route){
            CreateGroupScreen(navController)
        }
        composable(
            route = Screen.GroupDetails.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.LongType }
            )
        ) {
            GroupDetailsScreen()
        }
    }
}

