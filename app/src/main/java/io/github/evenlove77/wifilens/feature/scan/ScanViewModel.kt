package io.github.evenlove77.wifilens.feature.scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.evenlove77.wifilens.data.model.WiFiNetwork
import io.github.evenlove77.wifilens.data.wifi.WifiScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScanUiState(
    val networks: List<WiFiNetwork> = emptyList(),
    val isScanning: Boolean = false,
    val hasPermission: Boolean = false,
    val isWifiEnabled: Boolean = false,
    val errorMessage: String? = null
)

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val scanner = WifiScanner(application)
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    init {
        refreshPermissionState()
    }

    fun refreshPermissionState() {
        _uiState.value = _uiState.value.copy(
            hasPermission = scanner.hasPermission(),
            isWifiEnabled = scanner.isWifiEnabled()
        )
    }

    fun scan() {
        refreshPermissionState()

        if (!_uiState.value.hasPermission) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "需要 WiFi 扫描权限"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, errorMessage = null)

            try {
                val results = scanner.scanAsync()
                _uiState.value = _uiState.value.copy(
                    networks = results,
                    isScanning = false,
                    errorMessage = if (results.isEmpty()) "未扫描到 WiFi 网络，请重试" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    errorMessage = "扫描失败: ${e.message}"
                )
            }
        }
    }
}
