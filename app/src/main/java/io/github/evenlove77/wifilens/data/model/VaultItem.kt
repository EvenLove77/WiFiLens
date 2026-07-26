package io.github.evenlove77.wifilens.data.model

/**
 * 密码库条目
 */
data class VaultItem(
    val id: Long = 0,
    val ssid: String,
    val password: String = "",
    val remark: String = "",
    val category: String = "其他",  // 家庭 / 办公室 / 其他
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
