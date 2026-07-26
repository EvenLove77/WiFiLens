package io.github.evenlove77.wifilens.feature.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.evenlove77.wifilens.data.database.VaultSeedData
import io.github.evenlove77.wifilens.data.model.WiFiNetwork
import io.github.evenlove77.wifilens.data.wifi.WifiTester
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PasswordCandidate(
    val password: String,
    val label: String  // "SSID本身" / "品牌默认" / "常见密码"
)

data class DetailUiState(
    val network: WiFiNetwork? = null,
    val candidates: List<PasswordCandidate> = emptyList(),
    val isTesting: Boolean = false,
    val testIndex: Int = -1,
    val testTotal: Int = 0,
    val testResult: String? = null
)

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "DetailViewModel"
    }

    fun load(ssid: String, bssid: String, rssi: Int, frequency: Int, capabilities: String) {
        val network = WiFiNetwork(ssid, bssid, rssi, frequency, capabilities)

        viewModelScope.launch {
            val candidates = buildCandidates(ssid)
            _uiState.value = DetailUiState(
                network = network,
                candidates = candidates
            )
        }
    }

    private fun buildCandidates(ssid: String): List<PasswordCandidate> {
        val all = mutableListOf<PasswordCandidate>()

        // 1. SSID 本身（第一个测试）
        all.add(PasswordCandidate(ssid, "WiFi 名称本身"))

        // 2. 按种子数据固定顺序排列
        for (p in VaultSeedData.PASSWORDS) {
            if (p != ssid) {
                all.add(PasswordCandidate(p, "候选密码"))
            }
        }

        return all
    }

    fun startTest(context: android.content.Context) {
        val candidates = _uiState.value.candidates
        if (candidates.isEmpty() || _uiState.value.isTesting) return

        val ssid = _uiState.value.network?.ssid ?: return

        _uiState.value = _uiState.value.copy(
            isTesting = true, testIndex = 0, testTotal = candidates.size,
            testResult = null
        )

        viewModelScope.launch {
            for ((index, candidate) in candidates.withIndex()) {
                _uiState.value = _uiState.value.copy(testIndex = index + 1)

                val success = WifiTester.tryPassword(context, ssid, candidate.password)

                if (success) {
                    _uiState.value = _uiState.value.copy(
                        isTesting = false,
                        testResult = "密码已找到: ${candidate.password}"
                    )
                    return@launch
                }

                // 避免太快被限速
                delay(500)
            }

            _uiState.value = _uiState.value.copy(
                isTesting = false,
                testResult = "测试完成，未找到弱密码——WiFi 安全性良好"
            )
        }
    }
}
