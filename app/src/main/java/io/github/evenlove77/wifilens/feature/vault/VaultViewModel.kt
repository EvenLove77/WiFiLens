package io.github.evenlove77.wifilens.feature.vault

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.evenlove77.wifilens.data.database.CryptoManager
import io.github.evenlove77.wifilens.data.database.VaultSeedData
import io.github.evenlove77.wifilens.data.database.WifiLensDatabase
import io.github.evenlove77.wifilens.data.model.VaultItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class VaultUiState(
    val items: List<VaultItem> = emptyList(),
    val selectedCategory: String? = null,
    val isEditing: Boolean = false,
    val editingItem: VaultItem? = null
)

class VaultViewModel(application: Application) : AndroidViewModel(application) {

    private val db = WifiLensDatabase(application)
    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // 首次启动：自动填充弱密码字典
                if (!db.hasVaultData()) {
                    val seed = VaultSeedData.PASSWORDS.map { password ->
                        VaultItem(
                            ssid = "", password = CryptoManager.encrypt(password),
                            remark = "", category = "弱密码字典"
                        )
                    }
                    db.insertAllVault(seed)
                }
                val all = db.getAllVault()
                _uiState.value = _uiState.value.copy(items = all)
            }
        }
    }

    fun selectCategory(category: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val items = if (category != null) {
                    db.getVaultByCategory(category)
                } else {
                    db.getAllVault()
                }
                _uiState.value = _uiState.value.copy(items = items, selectedCategory = category)
            }
        }
    }

    fun startAdd() {
        _uiState.value = _uiState.value.copy(
            isEditing = true,
            editingItem = VaultItem(ssid = "", password = "", remark = "", category = "我的WiFi")
        )
    }

    fun startEdit(item: VaultItem) {
        val decrypted = item.copy(password = CryptoManager.decrypt(item.password))
        _uiState.value = _uiState.value.copy(isEditing = true, editingItem = decrypted)
    }

    fun cancelEdit() {
        _uiState.value = _uiState.value.copy(isEditing = false, editingItem = null)
    }

    fun saveItem(item: VaultItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val encrypted = item.copy(
                    password = CryptoManager.encrypt(item.password),
                    updatedAt = System.currentTimeMillis()
                )
                if (encrypted.id == 0L) {
                    db.insertVault(encrypted.copy(createdAt = System.currentTimeMillis()))
                } else {
                    db.updateVault(encrypted)
                }
                val all = db.getAllVault()
                _uiState.value = _uiState.value.copy(items = all, isEditing = false, editingItem = null)
            }
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.deleteVault(id)
                val all = db.getAllVault()
                _uiState.value = _uiState.value.copy(items = all)
            }
        }
    }
}
