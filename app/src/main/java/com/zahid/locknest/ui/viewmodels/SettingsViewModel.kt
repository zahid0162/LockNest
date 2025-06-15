package com.zahid.locknest.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zahid.locknest.ui.theme.ThemeManager
import com.zahid.locknest.util.EncryptionUtil
import com.zahid.locknest.util.PdfExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isAutoLockEnabled: Boolean = true,
    val autoLockTime: Int = 5,
    val currentPin: String = "",
    val newPin: String = "",
    val confirmPin: String = "",
    val isCurrentPinVerified: Boolean = false,
    val isPdfExporting: Boolean = false,
    val includePdfPasswords: Boolean = false,
    val exportVerificationPin: String = "",
    val exportPinError: String? = null,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager,
    private val encryptionUtil: EncryptionUtil,
    private val pdfExporter: PdfExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            themeManager.isDarkMode.collect { isDarkMode ->
                _uiState.update { it.copy(isDarkMode = isDarkMode) }
            }
        }
        loadSettings()
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            themeManager.setDarkMode(enabled)
        }
    }

    fun toggleBiometric() {
        _uiState.update { it.copy(isBiometricEnabled = !it.isBiometricEnabled) }
        saveSettings()
    }

    fun toggleAutoLock() {
        _uiState.update { it.copy(isAutoLockEnabled = !it.isAutoLockEnabled) }
        saveSettings()
    }

    fun updateAutoLockTime(time: Int) {
        if (time in 1..60) {
            _uiState.update { it.copy(autoLockTime = time) }
            saveSettings()
        }
    }

    fun updateCurrentPin(pin: String) {
        if (pin.length <= 6 && pin.all { it.isDigit() }) {
            _uiState.update { it.copy(currentPin = pin, error = null) }
        }
    }

    fun updateNewPin(pin: String) {
        if (pin.length <= 6 && pin.all { it.isDigit() }) {
            _uiState.update { it.copy(newPin = pin, error = null) }
        }
    }

    fun updateConfirmPin(pin: String) {
        if (pin.length <= 6 && pin.all { it.isDigit() }) {
            _uiState.update { it.copy(confirmPin = pin, error = null) }
        }
    }
    
    fun updateExportVerificationPin(pin: String) {
        if (pin.length <= 6 && pin.all { it.isDigit() }) {
            _uiState.update { it.copy(exportVerificationPin = pin, exportPinError = null) }
        }
    }
    
    fun clearExportVerificationPin() {
        _uiState.update { it.copy(exportVerificationPin = "", exportPinError = null) }
    }
    
    fun verifyExportPin(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val prefs = encryptionUtil.getEncryptedSharedPreferences()
                val storedPinHash = prefs.getString("pin_hash", null)
                
                // For testing/development, allow "1234" as a default PIN
                val pinToVerify = _uiState.value.exportVerificationPin
                val exportPinHash = encryptionUtil.hashPin(pinToVerify)
                val isDefaultPin = pinToVerify == "1234"
                
                if (storedPinHash == null && isDefaultPin) {
                    // First time setup with default PIN
                    _uiState.update { it.copy(exportPinError = null) }
                    onResult(true)
                } else if (storedPinHash == exportPinHash) {
                    _uiState.update { it.copy(exportPinError = null) }
                    onResult(true)
                } else {
                    _uiState.update { it.copy(exportPinError = "Incorrect PIN") }
                    onResult(false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(exportPinError = "Error verifying PIN: ${e.message}") }
                onResult(false)
            }
        }
    }

    fun verifyCurrentPin() {
        viewModelScope.launch {
            try {
                val prefs = encryptionUtil.getEncryptedSharedPreferences()
                val storedPinHash = prefs.getString("pin_hash", null)
                
                // For testing/development, allow "1234" as a default PIN
                val currentPinHash = encryptionUtil.hashPin(_uiState.value.currentPin)
                val isDefaultPin = _uiState.value.currentPin == "1234"
                
                if (storedPinHash == null && isDefaultPin) {
                    // First time setup with default PIN
                    _uiState.update { it.copy(isCurrentPinVerified = true, error = null) }
                } else if (storedPinHash == currentPinHash) {
                    _uiState.update { it.copy(isCurrentPinVerified = true, error = null) }
                } else {
                    _uiState.update { it.copy(error = "Incorrect PIN", isCurrentPinVerified = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error verifying PIN: ${e.message}", isCurrentPinVerified = false) }
            }
        }
    }

    fun changePin() {
        val state = _uiState.value
        if (state.newPin.length < 4) {
            _uiState.update { it.copy(error = "PIN must be at least 4 digits") }
            return
        }
        if (state.newPin != state.confirmPin) {
            _uiState.update { it.copy(error = "PINs do not match") }
            return
        }

        viewModelScope.launch {
            try {
                val prefs = encryptionUtil.getEncryptedSharedPreferences()
                val newPinHash = encryptionUtil.hashPin(state.newPin)
                prefs.edit().putString("pin_hash", newPinHash).apply()
                _uiState.update { 
                    it.copy(
                        currentPin = "",
                        newPin = "",
                        confirmPin = "",
                        isCurrentPinVerified = false,
                        error = "PIN changed successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error changing PIN: ${e.message}") }
            }
        }
    }
    
    fun exportPasswordsToPdf(uri: Uri, includePasswords: Boolean) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isPdfExporting = true, error = null) }
                
                val result = pdfExporter.exportToPdf(uri, includePasswords)
                
                result.fold(
                    onSuccess = { message ->
                        _uiState.update { it.copy(isPdfExporting = false, error = message) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isPdfExporting = false, error = error.message) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isPdfExporting = false, error = "Error exporting PDF: ${e.message}") }
            }
        }
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val prefs = encryptionUtil.getEncryptedSharedPreferences()
                val isBiometricEnabled = prefs.getBoolean("biometric_enabled", false)
                val isAutoLockEnabled = prefs.getBoolean("auto_lock_enabled", true)
                val autoLockTime = prefs.getInt("auto_lock_time", 5)
                
                _uiState.update { 
                    it.copy(
                        isBiometricEnabled = isBiometricEnabled,
                        isAutoLockEnabled = isAutoLockEnabled,
                        autoLockTime = autoLockTime
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error loading settings: ${e.message}") }
            }
        }
    }
    
    private fun saveSettings() {
        viewModelScope.launch {
            try {
                val prefs = encryptionUtil.getEncryptedSharedPreferences()
                val state = _uiState.value
                
                prefs.edit()
                    .putBoolean("biometric_enabled", state.isBiometricEnabled)
                    .putBoolean("auto_lock_enabled", state.isAutoLockEnabled)
                    .putInt("auto_lock_time", state.autoLockTime)
                    .apply()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error saving settings: ${e.message}") }
            }
        }
    }
} 
