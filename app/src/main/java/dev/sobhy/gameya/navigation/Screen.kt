package dev.sobhy.gameya.navigation

sealed class Screen(val route: String) {
    object Dashboard: Screen("dashboard")
    object CreateGroup: Screen("create_group")
    object GroupDetails: Screen("group_details/{groupId}"){
        fun createRoute(groupId: Long) = "group_details/$groupId"
    }

    object CyclePayments : Screen("cycle_payments/{cycleId}") {
        fun createRoute(cycleId: Long) = "cycle_payments/$cycleId"
    }

    object DataSafety : Screen("data_safety")
}