package io.github.evenlove77.wifilens.data.mock

import io.github.evenlove77.wifilens.data.model.VaultItem

object MockVaultItems {
    fun getItems(): List<VaultItem> = listOf(
        VaultItem(id = 1, ssid = "Home WiFi 5G", password = "MyHome@2024", remark = "家里 5G 网络", category = "家庭"),
        VaultItem(id = 2, ssid = "Home WiFi 2.4G", password = "MyHome@2024", remark = "家里 2.4G 备用", category = "家庭"),
        VaultItem(id = 3, ssid = "Office WiFi", password = "Office#2024", remark = "公司主网络", category = "办公室"),
        VaultItem(id = 4, ssid = "Office Guest", password = "guest123", remark = "公司访客网络", category = "办公室"),
        VaultItem(id = 5, ssid = "Starbucks WiFi", password = "", remark = "需网页登录", category = "其他"),
        VaultItem(id = 6, ssid = "Coffee Lab", password = "coffee@lab", remark = "常去的咖啡店", category = "其他"),
    )
}
