package com.gamo.travelfund.ui.views.modelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gamo.travelfund.data.preferences.SettingsPreferences
import com.gamo.travelfund.ui.views.viewmodel.SettingsViewModel

class SettingsViewModelFactory(
    private val preferences: SettingsPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(preferences) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}