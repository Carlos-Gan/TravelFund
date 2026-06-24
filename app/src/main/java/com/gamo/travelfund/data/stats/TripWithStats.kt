package com.gamo.travelfund.data.stats

import androidx.room.Embedded
import com.gamo.travelfund.data.model.entity.TripEntity

data class TripWithStats(
    @Embedded
    val trip: TripEntity,

    val savedAmount: Double
)