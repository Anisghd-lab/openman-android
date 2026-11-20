package com.ghdinteractivestudio.openman.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository {
    // Defaults:
    // Android Emulator: http://10.0.2.2:3001
    // Termux / Localhost: http://127.0.0.1:3001
    private val _baseUrl = MutableStateFlow("http://10.0.2.2:3001")
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    private val _apiKey = MutableStateFlow("freellmapi-0dafad9d80b0deb3a0e9799aa1ef4e5a2f3215243e7487d0")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _selectedModel = MutableStateFlow("auto")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    fun updateBaseUrl(url: String) {
        _baseUrl.value = url.trim()
    }

    fun updateApiKey(key: String) {
        _apiKey.value = key.trim()
    }

    fun updateSelectedModel(model: String) {
        _selectedModel.value = model.trim()
    }
}
