package dev.sobhy.gameya.domain.usecase

import dev.sobhy.gameya.domain.enums.CycleType
import java.util.Calendar

fun calculateDate(start: Long, index: Int, type: CycleType): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = start }

    when (type) {
        CycleType.DAILY -> calendar.add(Calendar.DAY_OF_MONTH, index)
        CycleType.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, index)
        CycleType.MONTHLY -> calendar.add(Calendar.MONTH, index)
    }

    return calendar.timeInMillis
}