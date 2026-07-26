package io.github.evenlove77.wifilens.feature.history

import androidx.lifecycle.ViewModel
import io.github.evenlove77.wifilens.data.mock.MockHistory
import io.github.evenlove77.wifilens.data.model.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HistoryUiState(
    val items: List<HistoryItem> = emptyList()
)

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = HistoryUiState(
            items = MockHistory.getItems()
        )
    }
}
