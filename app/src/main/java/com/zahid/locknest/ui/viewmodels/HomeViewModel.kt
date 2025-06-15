package com.zahid.locknest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zahid.locknest.data.model.Password
import com.zahid.locknest.data.repository.PasswordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val passwords: List<Password> = emptyList(),
    val categories: List<String> = listOf("General", "Social", "Work", "Personal", "Finance"),
    val selectedCategory: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPasswords()
    }

    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadPasswords()
    }

    private fun loadPasswords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                passwordRepository.getAllPasswords()
                    .catch { e ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = "Failed to load passwords: ${e.message}"
                            )
                        }
                    }
                    .collect { passwords ->
                        val filteredPasswords = _uiState.value.selectedCategory?.let { category ->
                            passwords.filter { it.category == category }
                        } ?: passwords
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                passwords = filteredPasswords
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load passwords: ${e.message}"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadPasswords()
    }

    fun deletePassword(password: Password) {
        viewModelScope.launch {
            try {
                passwordRepository.deletePassword(password)
                loadPasswords() // Refresh the list after deletion
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
} 