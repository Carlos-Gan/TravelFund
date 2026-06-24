package com.gamo.travelfund.utils

fun getSavingMilestone(
    previousPercent:Int,
    currentPercent: Int
) : Int? {
    val milestones = listOf(25, 50, 75, 100)

    return milestones.find { milestone ->
        milestone in (previousPercent + 1)..currentPercent
    }
}