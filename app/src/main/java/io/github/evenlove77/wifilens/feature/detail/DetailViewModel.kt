package io.github.evenlove77.wifilens.feature.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.evenlove77.wifilens.data.database.CryptoManager
import io.github.evenlove77.wifilens.data.database.VaultSeedData
import io.github.evenlove77.wifilens.data.database.WifiLensDatabase
import io.github.evenlove77.wifilens.data.model.WiFiNetwork
import io.github.evenlove77.wifilens.data.wifi.WifiTester
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val db = WifiLensDatabase(application)
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "DetailViewModel"
    }

    fun load(ssid: String, bssid: String, rssi: Int, frequency: Int, capabilities: String) {
        val network = WiFiNetwork(ssid, bssid, rssi, frequency, capabilities)

        viewModelScope.launch {
            val candidates = withContext(Dispatchers.IO) {
                buildCandidates(ssid)
            }
            _uiState.value = DetailUiState(
                network = network,
                candidates = candidates
            )
        }
    }

    private fun buildCandidates(ssid: String): List<PasswordCandidate> {
        val all = mutableListOf<PasswordCandidate>()

        // 1. SSID 本身（最高优先级）
        all.add(PasswordCandidate(ssid, "SSID 本身"))

        // 2. 从数据库加载解密后的密码
        val dbPasswords = db.getAllVault().map { CryptoManager.decrypt(it.password) }

        // 3. 合并种子数据
        val unique = (dbPasswords + VaultSeedData.PASSWORDS).distinct()

        // 4. 智能排序：品牌匹配优先
        val ssidLower = ssid.lowercase()
        val brandKeywords = mapOf(
            "tp-link" to listOf("tplink123", "admin", "admin123", "admin888", "password"),
            "tplink" to listOf("tplink123", "admin", "admin123", "admin888", "password"),
            "小米" to listOf("12345678", "xiaomi123"),
            "xiaomi" to listOf("12345678", "xiaomi123"),
            "华为" to listOf("12345678", "huawei123"),
            "huawei" to listOf("12345678", "huawei123"),
            "honor" to listOf("12345678", "huawei123"),
            "cmcc" to listOf("a12345678", "cmcc12345"),
            "chinanet" to listOf("12345678"),
            "水星" to listOf("admin", "admin123"),
            "mercury" to listOf("admin", "admin123"),
            "fast" to listOf("admin888", "12345678"),
            "tenda" to listOf("12345678", "admin888"),
            "netgear" to listOf("password", "admin123"),
            "d-link" to listOf("admin123"),
            "dlink" to listOf("admin123"),
            "asus" to listOf("admin123"),
        )

        val brandPreferred = mutableSetOf<String>()
        for ((keyword, preferred) in brandKeywords) {
            if (ssidLower.contains(keyword)) {
                brandPreferred.addAll(preferred)
            }
        }

        // 排序：品牌优先 → 其他
        val sorted = unique.sortedByDescending {
            if (it == ssid) 999
            else if (it in brandPreferred) 500
            else if (it.length >= 8 && it.any { c -> c.isLetter() } && it.any { c -> c.isDigit() }) 100
            else 0
        }

        for (p in sorted) {
            if (p != ssid) {  // SSID 本身已经加了
                val label = when {
                    p in brandPreferred && ssidLower.contains("tp-link") -> "TP-LINK 默认"
                    p in brandPreferred && ssidLower.contains("xiaomi") -> "小米默认"
                    p in brandPreferred && ssidLower.contains("huawei") -> "华为默认"
                    p in brandPreferred -> "品牌默认"
                    else -> "常见密码"
                }
                all.add(PasswordCandidate(p, label))
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
