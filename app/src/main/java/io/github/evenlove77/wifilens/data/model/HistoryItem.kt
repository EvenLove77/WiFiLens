package io.github.evenlove77.wifilens.data.model

/**
 * WiFi 检测历史记录
 */
data class HistoryItem(
    val id: Long = 0,
    val ssid: String,
    val status: HistoryStatus,
    val time: Long = System.currentTimeMillis()
)

enum class HistoryStatus(val label: String) {
    SUCCESS("检测成功"),
    FAILED("检测失败"),
    TIMEOUT("连接超时"),
    WEAK_SIGNAL("信号弱")
}
