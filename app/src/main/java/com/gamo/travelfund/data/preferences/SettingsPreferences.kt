package com.gamo.travelfund.data.preferences

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "settings_preferences"
)

class SettingsPreferences(
    private val context: Context
) {
    private object Keys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFY_FEW_DAYS = booleanPreferencesKey("notify_few_days")
        val NOTIFY_SAVING_GOAL = booleanPreferencesKey("notify_saving_goal")
        val NOTIFY_NO_SAVINGS = booleanPreferencesKey("notify_no_savings")
        val NOTIFY_EXCHANGE_RATE = booleanPreferencesKey("notify_exchange_rate")
    }

    val settingsFlow = context.dataStore.data.map { preferences ->
        NotificationSettings(
            notificationsEnabled = preferences[Keys.NOTIFICATIONS_ENABLED] ?: false,
            notifyFewDays = preferences[Keys.NOTIFY_FEW_DAYS] ?: true,
            notifySavingGoal = preferences[Keys.NOTIFY_SAVING_GOAL] ?: true,
            notifyNoSavings = preferences[Keys.NOTIFY_NO_SAVINGS] ?: false,
            notifyExchangeRate = preferences[Keys.NOTIFY_EXCHANGE_RATE] ?: false
        )
    }

    suspend fun setNotificationsEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = value }
    }

    suspend fun setNotifyFewDays(value: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFY_FEW_DAYS] = value }
    }

    suspend fun setNotifySavingGoal(value: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFY_SAVING_GOAL] = value }
    }

    suspend fun setNotifyNoSavings(value: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFY_NO_SAVINGS] = value }
    }

    suspend fun setNotifyExchangeRate(value: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFY_EXCHANGE_RATE] = value }
    }

}

data class NotificationSettings(
    val notificationsEnabled: Boolean = false,
    val notifyFewDays: Boolean = true,
    val notifySavingGoal: Boolean = true,
    val notifyNoSavings: Boolean = false,
    val notifyExchangeRate: Boolean = false
)