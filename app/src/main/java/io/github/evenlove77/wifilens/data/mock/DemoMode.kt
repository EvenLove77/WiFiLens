package io.github.evenlove77.wifilens.data.mock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 演示模式：开启后用模拟 WiFi 数据代替真实扫描
 * 用于 vivo 等限制 WiFi 扫描的设备上测试 UI
 */
object DemoMode {
    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    fun setEnabled(value: Boolean) {
        _enabled.value = value
    }
}
