package io.github.evenlove77.wifilens.data.database

import io.github.evenlove77.wifilens.data.model.VaultItem

/**
 * 预装常见 WiFi 弱密码字典
 * 首次启动自动写入，后续不再重复
 * 全部 ≥ 8 位
 */
object VaultSeedData {
    fun getSeedData(): List<VaultItem> = listOf(
        // ===== 纯数字（8位） =====
        VaultItem(ssid = "常见-纯数字", password = "12345678", remark = "最常用8位顺子", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "11111111", remark = "全1", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "00000000", remark = "全0", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "22222222", remark = "全2", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "66666666", remark = "全6", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "88888888", remark = "全8（发财）", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "99999999", remark = "全9", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "123456789", remark = "1-9顺子", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "87654321", remark = "倒序顺子", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "11223344", remark = "重复模式", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "12341234", remark = "循环顺子", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "12121212", remark = "交替模式", category = "常见弱密码"),
        VaultItem(ssid = "常见-纯数字", password = "100200300", remark = "整百模式", category = "常见弱密码"),

        // ===== 手机号格式 =====
        VaultItem(ssid = "常见-手机号", password = "13800138000", remark = "移动经典号", category = "常见弱密码"),
        VaultItem(ssid = "常见-手机号", password = "13900139000", remark = "移动号段", category = "常见弱密码"),
        VaultItem(ssid = "常见-手机号", password = "18600186000", remark = "联通话段", category = "常见弱密码"),
        VaultItem(ssid = "常见-手机号", password = "18800188000", remark = "移动号段", category = "常见弱密码"),
        VaultItem(ssid = "常见-手机号", password = "13300133000", remark = "电信号段", category = "常见弱密码"),
        VaultItem(ssid = "常见-手机号", password = "15800158000", remark = "移动号段", category = "常见弱密码"),

        // ===== 生日格式 =====
        VaultItem(ssid = "常见-生日", password = "19900101", remark = "90年元旦", category = "常见弱密码"),
        VaultItem(ssid = "常见-生日", password = "19950808", remark = "常见生日格式", category = "常见弱密码"),
        VaultItem(ssid = "常见-生日", password = "20000101", remark = "千禧年元旦", category = "常见弱密码"),
        VaultItem(ssid = "常见-生日", password = "20080808", remark = "奥运开幕", category = "常见弱密码"),
        VaultItem(ssid = "常见-生日", password = "19880618", remark = "常见年份", category = "常见弱密码"),
        VaultItem(ssid = "常见-生日", password = "19991001", remark = "国庆日", category = "常见弱密码"),
        VaultItem(ssid = "常见-生日", password = "20100101", remark = "10年代元旦", category = "常见弱密码"),

        // ===== 键盘模式 =====
        VaultItem(ssid = "键盘模式", password = "qwertyuiop", remark = "键盘首行完整", category = "常见弱密码"),
        VaultItem(ssid = "键盘模式", password = "qwertyui", remark = "键盘首行", category = "常见弱密码"),
        VaultItem(ssid = "键盘模式", password = "asdfghjkl", remark = "键盘中行", category = "常见弱密码"),
        VaultItem(ssid = "键盘模式", password = "zxcvbnm123", remark = "键盘末行+数字", category = "常见弱密码"),
        VaultItem(ssid = "键盘模式", password = "1qaz2wsx3edc", remark = "键盘竖排", category = "常见弱密码"),
        VaultItem(ssid = "键盘模式", password = "1q2w3e4r5t", remark = "数字字母交替", category = "常见弱密码"),
        VaultItem(ssid = "键盘模式", password = "qazwsxedc", remark = "键盘对角", category = "常见弱密码"),

        // ===== 英文单词 + 数字 =====
        VaultItem(ssid = "常见-英数混合", password = "password123", remark = "#1最常见密码", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "admin12345", remark = "admin变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "admin888", remark = "admin+888", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "abc123456", remark = "abc+数字", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "abcd1234", remark = "abcd+1234", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "qwer1234", remark = "qwer+1234", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "welcome123", remark = "welcome+123", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "hello1234", remark = "hello+1234", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "iloveyou1", remark = "iloveyou变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "passw0rd", remark = "password变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "p@ssw0rd", remark = "特殊字符变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "master123", remark = "master+123", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "test1234", remark = "test变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "guest1234", remark = "guest变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "letmein12", remark = "letmein变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "monkey123", remark = "monkey+123", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "dragon123", remark = "dragon+123", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "shadow123", remark = "shadow+123", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "sunshine8", remark = "sunshine变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "princess8", remark = "princess变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "football8", remark = "football变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "baseball8", remark = "baseball变体", category = "常见弱密码"),
        VaultItem(ssid = "常见-英数混合", password = "charlie88", remark = "常见英文名", category = "常见弱密码"),

        // ===== 品牌默认密码 =====
        VaultItem(ssid = "TP-LINK", password = "admin123", remark = "TP-LINK常见默认", category = "品牌默认密码"),
        VaultItem(ssid = "TP-LINK", password = "tplink123", remark = "TP-LINK变体", category = "品牌默认密码"),
        VaultItem(ssid = "TP-LINK", password = "admin888", remark = "TP-LINK变体", category = "品牌默认密码"),
        VaultItem(ssid = "水星/MERCURY", password = "admin", remark = "水星路由器默认", category = "品牌默认密码"),
        VaultItem(ssid = "水星/MERCURY", password = "admin123", remark = "水星变体", category = "品牌默认密码"),
        VaultItem(ssid = "FAST/迅捷", password = "admin888", remark = "FAST路由器默认", category = "品牌默认密码"),
        VaultItem(ssid = "FAST/迅捷", password = "12345678", remark = "FAST变体", category = "品牌默认密码"),
        VaultItem(ssid = "腾达/Tenda", password = "12345678", remark = "Tenda常见默认", category = "品牌默认密码"),
        VaultItem(ssid = "腾达/Tenda", password = "admin888", remark = "Tenda变体", category = "品牌默认密码"),
        VaultItem(ssid = "小米/Xiaomi", password = "12345678", remark = "小米路由器默认", category = "品牌默认密码"),
        VaultItem(ssid = "小米/Xiaomi", password = "xiaomi123", remark = "小米变体", category = "品牌默认密码"),
        VaultItem(ssid = "华为/Huawei", password = "12345678", remark = "华为光猫默认", category = "品牌默认密码"),
        VaultItem(ssid = "华为/Huawei", password = "huawei123", remark = "华为变体", category = "品牌默认密码"),
        VaultItem(ssid = "移动光猫/CMCC", password = "a12345678", remark = "移动光猫常见", category = "品牌默认密码"),
        VaultItem(ssid = "移动光猫/CMCC", password = "cmcc12345", remark = "移动变体", category = "品牌默认密码"),
        VaultItem(ssid = "电信光猫/ChinaNet", password = "12345678", remark = "电信光猫常见", category = "品牌默认密码"),
        VaultItem(ssid = "联通光猫/CU", password = "12345678", remark = "联通光猫常见", category = "品牌默认密码"),
        VaultItem(ssid = "NETGEAR", password = "password", remark = "NETGEAR默认", category = "品牌默认密码"),
        VaultItem(ssid = "D-LINK", password = "admin123", remark = "D-LINK默认", category = "品牌默认密码"),
        VaultItem(ssid = "ASUS", password = "admin123", remark = "华硕路由器默认", category = "品牌默认密码"),

        // ===== 特殊数字组合 =====
        VaultItem(ssid = "特殊数字", password = "147258369", remark = "小键盘对角", category = "常见弱密码"),
        VaultItem(ssid = "特殊数字", password = "159357456", remark = "小键盘交错", category = "常见弱密码"),
        VaultItem(ssid = "特殊数字", password = "123698745", remark = "数字模式", category = "常见弱密码"),
        VaultItem(ssid = "特殊数字", password = "52013140", remark = "我爱你一生一世", category = "常见弱密码"),
        VaultItem(ssid = "特殊数字", password = "13145200", remark = "一生一世我爱你", category = "常见弱密码"),
        VaultItem(ssid = "特殊数字", password = "77585210", remark = "亲亲我吧我爱你", category = "常见弱密码"),
        VaultItem(ssid = "特殊数字", password = "1234567890", remark = "0-9完整顺子", category = "常见弱密码"),
        VaultItem(ssid = "特殊数字", password = "0987654321", remark = "倒序0-9", category = "常见弱密码"),

        // ===== 中英混合场景 =====
        VaultItem(ssid = "场景密码", password = "woaini1314", remark = "我爱你一生一世拼音", category = "常见弱密码"),
        VaultItem(ssid = "场景密码", password = "mima123456", remark = "密码123456", category = "常见弱密码"),
        VaultItem(ssid = "场景密码", password = "wifi123456", remark = "wifi123456", category = "常见弱密码"),
        VaultItem(ssid = "场景密码", password = "123456789a", remark = "数字+字母", category = "常见弱密码"),
        VaultItem(ssid = "场景密码", password = "jia688888", remark = "家门牌号格式", category = "常见弱密码"),
        VaultItem(ssid = "场景密码", password = "dianhua888", remark = "电话+888", category = "常见弱密码"),
        VaultItem(ssid = "场景密码", password = "zhongguo88", remark = "中国+数字", category = "常见弱密码"),
        VaultItem(ssid = "场景密码", password = "beijing88", remark = "北京+数字", category = "常见弱密码"),
    )
}
