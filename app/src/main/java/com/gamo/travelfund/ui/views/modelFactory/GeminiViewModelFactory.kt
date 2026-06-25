package com.gamo.travelfund.ui.views.modelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gamo.travelfund.data.repository.GeminiRepository
import com.gamo.travelfund.ui.views.viewmodel.GeminiViewModel

class GeminiViewModelFactory(
    private val repository: GeminiRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GeminiViewModel(repository) as T
    }
}