package io.github.evenlove77.wifilens.feature.scan

import android.app.Application
import android.util.Log
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

    companion object {
        private const val TAG = "ScanViewModel"
    }

    init {
        refreshState()
    }

    fun refreshState() {
        _uiState.value = _uiState.value.copy(
            hasPermission = scanner.hasPermission(),
            isWifiEnabled = scanner.isWifiEnabled()
        )
    }

    /** 进入页面时调用 — 自动扫描 */
    fun onScreenEnter() {
        refreshState()
        if (_uiState.value.hasPermission && _uiState.value.isWifiEnabled) {
            scan()
        }
    }

    fun scan() {
        refreshState()

        if (!_uiState.value.hasPermission) {
            _uiState.value = _uiState.value.copy(errorMessage = "需要 WiFi 扫描权限")
            return
        }

        if (!_uiState.value.isWifiEnabled) {
            _uiState.value = _uiState.value.copy(errorMessage = "WiFi 未开启")
            return
        }

        // 防止重复点击
        if (_uiState.value.isScanning) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, errorMessage = null)

            try {
                val result = scanner.scanAsync()
                Log.d(TAG, "扫描完成: ${result.networks.size} 个网络, error=${result.error}")
                _uiState.value = _uiState.value.copy(
                    networks = result.networks,
                    isScanning = false,
                    errorMessage = result.error
                )
            } catch (e: Exception) {
                Log.e(TAG, "扫描异常: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    errorMessage = "扫描失败: ${e.message}"
                )
            }
        }
    }
}
