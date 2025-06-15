package com.zahid.locknest.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.zahid.locknest.util.BiometricManager
import com.zahid.locknest.util.EncryptionUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class LoginUiState(
    val isBiometricAvailable: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val showPinInput: Boolean = false,
    val pin: String = "",
    val isFirstTimeSetup: Boolean = false,
    val confirmPin: String = "",
    val pinSetupStep: PinSetupStep = PinSetupStep.ENTER_PIN
)

enum class PinSetupStep {
    ENTER_PIN,
    CONFIRM_PIN
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val biometricManager: BiometricManager,
    private val encryptionUtil: EncryptionUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkBiometricAvailability()
        checkFirstTimeSetup()
    }

    private fun checkBiometricAvailability() {
        _uiState.update { it.copy(isBiometricAvailable = biometricManager.canAuthenticate()) }
    }

    private fun checkFirstTimeSetup() {
        val prefs = encryptionUtil.getEncryptedSharedPreferences()
        val isFirstTime = !prefs.contains("pin_hash")
        _uiState.update { it.copy(isFirstTimeSetup = isFirstTime) }
    }

    fun onAuthenticationSuccess() {
        _uiState.update { it.copy(isAuthenticated = true, error = null) }
    }

    fun onAuthenticationError() {
        _uiState.update { it.copy(error = "Authentication failed. Please try again.") }
    }

    fun showPinInput() {
        _uiState.update { it.copy(showPinInput = true) }
    }

    fun updatePin(pin: String) {
        _uiState.update { it.copy(pin = pin) }
    }

    fun updateConfirmPin(pin: String) {
        _uiState.update { it.copy(confirmPin = pin) }
    }

    fun verifyPin() {
        val prefs = encryptionUtil.getEncryptedSharedPreferences()
        val storedPinHash = prefs.getString("pin_hash", null)

        if (_uiState.value.isFirstTimeSetup) {
            if (_uiState.value.pinSetupStep == PinSetupStep.ENTER_PIN) {
                if (_uiState.value.pin.length < 4) {
                    _uiState.update { it.copy(error = "PIN must be at least 4 digits") }
                    return
                }
                _uiState.update { it.copy(pinSetupStep = PinSetupStep.CONFIRM_PIN) }
            } else {
                if (_uiState.value.pin != _uiState.value.confirmPin) {
                    _uiState.update { it.copy(error = "PINs do not match") }
                    return
                }
                // Save PIN hash using the proper hashing method
                val pinHash = encryptionUtil.hashPin(_uiState.value.pin)
                prefs.edit().putString("pin_hash", pinHash).apply()
                onAuthenticationSuccess()
            }
        } else {
            if (storedPinHash == null) {
                _uiState.update { it.copy(error = "No PIN set. Please set up a PIN.") }
                return
            }
            
            // Use the proper hashing method for verification
            val pinHash = encryptionUtil.hashPin(_uiState.value.pin)
            if (pinHash == storedPinHash) {
                onAuthenticationSuccess()
            } else {
                _uiState.update { it.copy(error = "Incorrect PIN") }
            }
        }
    }
} 