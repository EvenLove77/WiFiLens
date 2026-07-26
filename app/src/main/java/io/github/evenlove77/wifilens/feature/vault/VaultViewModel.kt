package io.github.evenlove77.wifilens.feature.vault

import androidx.lifecycle.ViewModel
import io.github.evenlove77.wifilens.data.mock.MockVaultItems
import io.github.evenlove77.wifilens.data.model.VaultItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class VaultUiState(
    val items: List<VaultItem> = emptyList(),
    val selectedCategory: String? = null
)

class VaultViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        _uiState.value = VaultUiState(
            items = MockVaultItems.getItems()
        )
    }

    fun selectCategory(category: String?) {
        val all = MockVaultItems.getItems()
        _uiState.value = if (category == null) {
            VaultUiState(items = all, selectedCategory = null)
        } else {
            VaultUiState(
                items = all.filter { it.category == category },
                selectedCategory = category
            )
        }
    }
}
