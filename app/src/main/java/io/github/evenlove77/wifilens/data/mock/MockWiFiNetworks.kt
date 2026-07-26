package io.github.evenlove77.wifilens.data.mock

import io.github.evenlove77.wifilens.data.model.WiFiNetwork

object MockWiFiNetworks {
    fun getNetworks(): List<WiFiNetwork> = listOf(
        WiFiNetwork(ssid = "My Home 5G",        bssid = "AA:BB:CC:DD:EE:01", rssi = -42, frequency = 5180, capabilities = "[WPA3-PSK-CCMP][RSN-PSK+SAE-CCMP][ESS]"),
        WiFiNetwork(ssid = "My Home 2.4G",       bssid = "AA:BB:CC:DD:EE:02", rssi = -55, frequency = 2437, capabilities = "[WPA2-PSK-CCMP][ESS]"),
        WiFiNetwork(ssid = "Office WiFi",        bssid = "AA:BB:CC:DD:EE:03", rssi = -60, frequency = 5240, capabilities = "[WPA2-Enterprise-CCMP][RSN-EAP-CCMP][ESS]"),
        WiFiNetwork(ssid = "Starbucks WiFi",     bssid = "AA:BB:CC:DD:EE:04", rssi = -68, frequency = 2462, capabilities = "[ESS]"),
        WiFiNetwork(ssid = "Neighbor_5G",        bssid = "AA:BB:CC:DD:EE:05", rssi = -72, frequency = 5500, capabilities = "[WPA2-PSK-CCMP][ESS]"),
        WiFiNetwork(ssid = "Coffee Lab Guest",   bssid = "AA:BB:CC:DD:EE:06", rssi = -75, frequency = 2412, capabilities = "[WPA2-PSK-CCMP][ESS]"),
        WiFiNetwork(ssid = "Library Public WiFi", bssid = "AA:BB:CC:DD:EE:07", rssi = -80, frequency = 5785, capabilities = "[OWE][ESS]"),
        WiFiNetwork(ssid = "Xiaomi_Router",      bssid = "AA:BB:CC:DD:EE:08", rssi = -85, frequency = 2472, capabilities = "[WPA-PSK-CCMP][ESS]"),
    )
}
