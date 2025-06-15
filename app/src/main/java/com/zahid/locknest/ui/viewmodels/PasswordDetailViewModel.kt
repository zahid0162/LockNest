package com.zahid.locknest.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zahid.locknest.data.model.Password
import com.zahid.locknest.data.repository.PasswordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PasswordDetailUiState(
    val password: Password? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDeleted: Boolean = false
)

@HiltViewModel
class PasswordDetailViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordDetailUiState())
    val uiState: StateFlow<PasswordDetailUiState> = _uiState.asStateFlow()

    init {
        savedStateHandle.get<String>("passwordId")?.let { passwordId ->
            loadPassword(passwordId)
        }
    }

    private fun loadPassword(passwordId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val password = passwordRepository.getPasswordById(passwordId)
                if (password != null) {
                    _uiState.update { it.copy(password = password, isLoading = false) }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Password not found",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Error loading password: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deletePassword() {
        val currentPassword = _uiState.value.password ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                passwordRepository.deletePassword(currentPassword)
                _uiState.update { it.copy(isDeleted = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Error deleting password: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

} 