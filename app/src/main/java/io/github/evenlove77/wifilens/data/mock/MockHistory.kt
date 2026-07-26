package io.github.evenlove77.wifilens.data.mock

import io.github.evenlove77.wifilens.data.model.HistoryItem
import io.github.evenlove77.wifilens.data.model.HistoryStatus

object MockHistory {
    fun getItems(): List<HistoryItem> = listOf(
        HistoryItem(id = 1, ssid = "Home WiFi 5G", status = HistoryStatus.SUCCESS, time = 1751404800000L),
        HistoryItem(id = 2, ssid = "Office Network", status = HistoryStatus.SUCCESS, time = 1751232000000L),
        HistoryItem(id = 3, ssid = "Coffee Shop WiFi", status = HistoryStatus.FAILED, time = 1751059200000L),
        HistoryItem(id = 4, ssid = "Guest Network", status = HistoryStatus.TIMEOUT, time = 1750886400000L),
        HistoryItem(id = 5, ssid = "Home WiFi 2.4G", status = HistoryStatus.WEAK_SIGNAL, time = 1750713600000L),
        HistoryItem(id = 6, ssid = "Library WiFi", status = HistoryStatus.SUCCESS, time = 1750540800000L),
    )
}
