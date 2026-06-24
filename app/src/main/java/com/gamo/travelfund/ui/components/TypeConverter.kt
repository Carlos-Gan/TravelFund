package com.gamo.travelfund.ui.components

import androidx.room.TypeConverter
import com.gamo.travelfund.data.model.entity.TripStatus

class TypeStatusConverter {
    @TypeConverter
    fun fromTripStatus(status: TripStatus): String = status.name

    @TypeConverter
    fun toTripStatus(value: String): TripStatus = TripStatus.valueOf(value)

}