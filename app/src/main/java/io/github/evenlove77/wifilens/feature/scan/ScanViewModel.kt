package io.github.evenlove77.wifilens.feature.scan

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.evenlove77.wifilens.data.database.CryptoManager
import io.github.evenlove77.wifilens.data.database.VaultSeedData
import io.github.evenlove77.wifilens.data.database.WifiLensDatabase
import io.github.evenlove77.wifilens.data.model.WiFiNetwork
import io.github.evenlove77.wifilens.data.wifi.WifiScanner
import io.github.evenlove77.wifilens.data.wifi.WifiTester
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class TestResult(
    val ssid: String,
    val password: String
)

data class ScanUiState(
    val networks: List<WiFiNetwork> = emptyList(),
    val isScanning: Boolean = false,
    val hasPermission: Boolean = false,
    val isWifiEnabled: Boolean = false,
    val errorMessage: String? = null,
    // 全部测试
    val isTesting: Boolean = false,
    val testProgress: String = "",
    val testCurrentWifi: String = "",
    val testCurrentIndex: Int = 0,    // 第几个 WiFi
    val testTotalWifi: Int = 0,
    val testResults: List<TestResult> = emptyList(),
    val testComplete: Boolean = false,
)

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val scanner = WifiScanner(application)
    private val db = WifiLensDatabase(application)
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()
    private var testJob: Job? = null

    fun refreshState() {
        _uiState.value = _uiState.value.copy(
            hasPermission = scanner.hasPermission(),
            isWifiEnabled = scanner.isWifiEnabled()
        )
    }

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
        if (_uiState.value.isScanning) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, errorMessage = null)
            try {
                val result = scanner.scanAsync()
                _uiState.value = _uiState.value.copy(
                    networks = result.networks, isScanning = false, errorMessage = result.error
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isScanning = false, errorMessage = "扫描失败: ${e.message}")
            }
        }
    }

    fun startFullTest(context: Context, simple: Boolean = false) {
        // 只测信号好的（信号 ≥ 40%）
        val networks = _uiState.value.networks.filter { it.signalPercent >= 40 }
        if (networks.isEmpty() || _uiState.value.isTesting) return

        // 简单测试只测 3 个最常见密码，复杂测试测全部
        val passwords = if (simple) {
            listOf("12345678", "123456789", "88888888")
        } else {
            VaultSeedData.PASSWORDS.toList()
        }

        val mode = if (simple) "简单测试" else "复杂测试"
        _uiState.value = _uiState.value.copy(
            isTesting = true, testComplete = false,
            testResults = emptyList(), testProgress = "$mode: ${networks.size} 个 WiFi",
            testCurrentWifi = "", testCurrentIndex = 0, testTotalWifi = networks.size
        )

        testJob = viewModelScope.launch(Dispatchers.IO) {
            val results = mutableListOf<TestResult>()

            for ((wifiIndex, network) in networks.withIndex()) {
                if (!isActive) break

                _uiState.value = _uiState.value.copy(
                    testCurrentWifi = network.ssid,
                    testCurrentIndex = wifiIndex + 1,
                    testProgress = "第 ${wifiIndex + 1}/${networks.size} 个 WiFi: ${network.ssid}"
                )

                // SSID 本身作为第一候选
                val candidates = listOf(network.ssid) + passwords

                for (password in candidates) {
                    if (!isActive) break

                    val success = WifiTester.tryPassword(context, network.ssid, password)
                    if (success) {
                        results.add(TestResult(network.ssid, password))
                        break // 找到就跳到下一个 WiFi
                    }
                    delay(500)
                }
            }

            if (isActive) {
                _uiState.value = _uiState.value.copy(
                    isTesting = false, testComplete = true,
                    testResults = results,
                    testProgress = if (results.isEmpty()) "测试完成，未发现弱密码" else "找到 ${results.size} 个弱密码"
                )
            }
        }
    }

    fun stopTest() {
        testJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isTesting = false,
            testProgress = "已停止测试"
        )
    }

    fun dismissTestResults() {
        _uiState.value = _uiState.value.copy(testComplete = false, testResults = emptyList(), testProgress = "")
    }
}
