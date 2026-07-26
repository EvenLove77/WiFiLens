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
        "147258369", "52013140", "13145200", "20080808",
        "19900101", "20100101",

        // 品牌出厂默认
        "admin", "admin123", "admin888",
        "password", "1234567890",

        // 英文 + 数字
        "password123", "qwertyuiop", "qwertyui",
        "abc123456", "welcome123", "iloveyou1",
        "passw0rd", "hello1234",

        // 拼音 + 数字
        "woaini1314", "mima123456", "wifi123456",
        "zhongguo88", "beijing88", "dianhua888",
        "jia688888", "xingming88",

        // 键盘模式
        "1qaz2wsx3edc", "1q2w3e4r5t", "qazwsxedc",

        // 品牌定制
        "tplink123", "huawei123", "xiaomi123",
        "a12345678", "cmcc12345",
    )
}
