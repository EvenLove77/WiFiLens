package io.github.evenlove77.wifilens.feature.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.evenlove77.wifilens.data.database.WifiLensDatabase
import io.github.evenlove77.wifilens.data.model.HistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HistoryUiState(
    val items: List<HistoryItem> = emptyList()
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = WifiLensDatabase(application)
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uiState.value = HistoryUiState(items = db.getAllHistory())
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.deleteAllHistory()
                _uiState.value = HistoryUiState(items = emptyList())
            }
        }
    }
}
