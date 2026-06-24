package com.gamo.travelfund.ui.views.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamo.travelfund.data.preferences.SettingsPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: SettingsPreferences
) : ViewModel() {
    val settings = preferences.settingsFlow

    fun setNotificationsEnabled(value: Boolean) {
        viewModelScope.launch {
            preferences.setNotificationsEnabled(value)
        }
    }

    fun setNotifyFewDays(value: Boolean) {
        viewModelScope.launch {
            preferences.setNotifyFewDays(value)
        }
    }

    fun setNotifySavingGoal(value: Boolean) {
        viewModelScope.launch {
            preferences.setNotifySavingGoal(value)
        }
    }

    fun setNotifyNoSavings(value: Boolean) {
        viewModelScope.launch {
            preferences.setNotifyNoSavings(value)
        }
    }

    fun setNotifyExchangeRate(value: Boolean) {
        viewModelScope.launch {
            preferences.setNotifyExchangeRate(value)
        }
    }
}