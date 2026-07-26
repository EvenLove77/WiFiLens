package io.github.evenlove77.wifilens.data.database

import io.github.evenlove77.wifilens.data.model.VaultItem

/**
 * 预装常见 WiFi 弱密码库
 * 首次启动时自动写入数据库
 */
object VaultSeedData {
    fun getSeedData(): List<VaultItem> = listOf(
        // 纯数字
        VaultItem(ssid = "常见-8位数字", password = "12345678", remark = "最常见的8位数字密码", category = "常见弱密码"),
        VaultItem(ssid = "常见-生日",    password = "88888888", remark = "全8", category = "常见弱密码"),
        VaultItem(ssid = "常见-顺子",    password = "66666666", remark = "全6", category = "常见弱密码"),
        VaultItem(ssid = "常见-8个0",    password = "00000000", remark = "全0", category = "常见弱密码"),
        VaultItem(ssid = "常见-手机号",   password = "13800138000", remark = "手机号格式", category = "常见弱密码"),
        VaultItem(ssid = "常见-6位数字",  password = "123456", remark = "最短数字密码", category = "常见弱密码"),

        // 字母序列
        VaultItem(ssid = "常见-字母",    password = "qwertyui", remark = "键盘首行", category = "常见弱密码"),
        VaultItem(ssid = "常见-字母",    password = "asdfghjk", remark = "键盘中行", category = "常见弱密码"),
        VaultItem(ssid = "常见-字母",    password = "password", remark = "最常见英文", category = "常见弱密码"),

        // 字母+数字
        VaultItem(ssid = "常见-混合",    password = "abc12345", remark = "字母+数字顺子", category = "常见弱密码"),
        VaultItem(ssid = "常见-混合",    password = "abcd1234", remark = "abc+123", category = "常见弱密码"),
        VaultItem(ssid = "常见-混合",    password = "qwer1234", remark = "qwer+123", category = "常见弱密码"),
        VaultItem(ssid = "常见-混合",    password = "admin888", remark = "admin+888", category = "常见弱密码"),
        VaultItem(ssid = "常见-混合",    password = "passw0rd", remark = "password变体", category = "常见弱密码"),

        // 品牌默认
        VaultItem(ssid = "TP-LINK",     password = "admin123", remark = "TP-LINK常见默认", category = "品牌默认密码"),
        VaultItem(ssid = "水星",        password = "admin",    remark = "水星常见默认", category = "品牌默认密码"),
        VaultItem(ssid = "FAST",        password = "admin888", remark = "FAST路由器默认", category = "品牌默认密码"),
        VaultItem(ssid = "CMCC",        password = "a12345678", remark = "移动光猫常见", category = "品牌默认密码"),
        VaultItem(ssid = "ChinaNet",    password = "12345678", remark = "电信光猫常见", category = "品牌默认密码"),
        VaultItem(ssid = "CU",          password = "123456",   remark = "联通光猫常见", category = "品牌默认密码"),
    )
}
