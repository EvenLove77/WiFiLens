package io.github.evenlove77.wifilens.feature.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val label: String) {
    DARK("深色"),
    LIGHT("浅色"),
    SYSTEM("跟随系统")
}

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val dataVersion: String = "v1.0"
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }
}
