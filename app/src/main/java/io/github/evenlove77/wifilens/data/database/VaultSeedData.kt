package io.github.evenlove77.wifilens.data.database

/**
 * 弱密码字典（纯密码列表）
 * 首次启动写入数据库，后续不再重复
 * 全部 ≥ 8 位
 */
object VaultSeedData {

    /** 测试时自动把 WiFi 名称也作为候选密码 */
    fun withSsid(ssid: String): List<String> {
        return listOf(ssid) + PASSWORDS
    }

    val PASSWORDS: List<String> = listOf(
        // 纯数字
        "12345678", "88888888", "66666666", "00000000",
        "11111111", "22222222", "99999999", "87654321",
        "11223344", "12341234", "123456789", "1234567890",
        "147258369", "52013140", "13145200",

        // 品牌出厂默认
        "admin123", "admin888",
        "password",

        // 英文 + 数字
        "password123", "qwertyuiop", "qwertyui",
        "abc123456", "welcome123", "hello1234",

        // 运营商默认
        "a12345678", "cmcc12345",
    )
}
