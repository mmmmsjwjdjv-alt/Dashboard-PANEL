package com.example.data.model

import java.util.Calendar

data class UserLicense(
    val deviceId: String = "",
    val key: String = "",
    val expiryDate: String = "",
    val allowOffline: Boolean = true,
    val isDisabled: Boolean = false
) {
    val daysLeft: Int?
        get() = parseDaysLeft(expiryDate)

    val isActive: Boolean
        get() {
            if (isDisabled) return false
            val d = daysLeft
            return d != null && d >= 0
        }

    val statusText: String
        get() {
            if (isDisabled) return "Paused"
            val d = daysLeft
            return if (d != null && d >= 0) "$d days left" else "Expired"
        }

    companion object {
        fun calculateExpiry(days: Int): String {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, days)
            val day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
            val month = String.format("%02d", cal.get(Calendar.MONTH) + 1)
            val year = cal.get(Calendar.YEAR)
            return "$day-$month-$year"
        }

        fun parseDaysLeft(expiryStr: String?): Int? {
            if (expiryStr.isNullOrBlank()) return null
            return try {
                val parts = expiryStr.split("-")
                if (parts.size < 3) return null
                val day = parts[0].trim().toInt()
                val month = parts[1].trim().toInt()
                val year = parts[2].trim().toInt()

                val target = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month - 1)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }
                val now = Calendar.getInstance()
                val diffMs = target.timeInMillis - now.timeInMillis
                val days = Math.ceil(diffMs.toDouble() / (1000.0 * 60.0 * 60.0 * 24.0)).toInt()
                days
            } catch (e: Exception) {
                null
            }
        }
    }
}
