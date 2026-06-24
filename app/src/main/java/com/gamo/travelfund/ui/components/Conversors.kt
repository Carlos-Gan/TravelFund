package com.gamo.travelfund.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun formatDateTime(millis: Long): String {
    val formatter = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    )
    return formatter.format(Date(millis))
}

fun formatDate(millis: Long): String {
    return SimpleDateFormat(
        "d MMM yyyy",
        Locale("es", "MX")
    ).format(Date(millis))
}