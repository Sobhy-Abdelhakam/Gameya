package dev.sobhy.gameya.navigation

sealed class Screen(val route: String) {
    object CreateGroup: Screen("create_group")
    object GroupDetails: Screen("group_details/{groupId}"){
        fun createRoute(groupId: Long) = "group_details/$groupId"
    }
}