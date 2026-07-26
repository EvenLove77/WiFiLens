package io.github.evenlove77.wifilens.feature.detail

import androidx.lifecycle.ViewModel
import io.github.evenlove77.wifilens.data.model.WiFiNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DetailUiState(
    val network: WiFiNetwork? = null,
    val isVerifying: Boolean = false,
    val verifyResult: String? = null
)

class DetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadNetwork(network: WiFiNetwork) {
        _uiState.value = DetailUiState(network = network)
    }

    fun startVerification() {
        _uiState.value = _uiState.value.copy(isVerifying = true, verifyResult = null)
        // 验证功能后续实现
    }
}
