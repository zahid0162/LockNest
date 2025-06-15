package com.zahid.locknest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.zahid.locknest.R
import com.zahid.locknest.ui.viewmodels.LoginUiState
import com.zahid.locknest.ui.viewmodels.LoginViewModel
import com.zahid.locknest.ui.viewmodels.PinSetupStep
import com.zahid.locknest.util.BiometricManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    biometricManager: BiometricManager,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = if (uiState.showPinInput) Icons.Default.Lock else Icons.Default.Fingerprint,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to LockNest",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Secure Password Manager",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.showPinInput) {
                PinInputSection(uiState, viewModel)
            } else {
                AuthenticationOptions(
                    isBiometricAvailable = uiState.isBiometricAvailable,
                    onBiometricClick = {
                        biometricManager.showBiometricPrompt(
                            activity = context as FragmentActivity,
                            onSuccess = viewModel::onAuthenticationSuccess,
                            onError = viewModel::onAuthenticationError
                        )
                    },
                    onPinClick = viewModel::showPinInput
                )
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.error!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AuthenticationOptions(
    isBiometricAvailable: Boolean,
    onBiometricClick: () -> Unit,
    onPinClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isBiometricAvailable) {
            Button(
                onClick = onBiometricClick,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.authenticate_with_biometrics))
            }
        }

        OutlinedButton(
            onClick = onPinClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Use PIN Instead")
        }
    }
}

@Composable
private fun PinInputSection(
    uiState: LoginUiState,
    viewModel: LoginViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Text(
            text = when {
                uiState.isFirstTimeSetup && uiState.pinSetupStep == PinSetupStep.ENTER_PIN -> "Create a PIN"
                uiState.isFirstTimeSetup && uiState.pinSetupStep == PinSetupStep.CONFIRM_PIN -> "Confirm your PIN"
                else -> "Enter your PIN"
            },
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = if (uiState.pinSetupStep == PinSetupStep.CONFIRM_PIN) uiState.confirmPin else uiState.pin,
            onValueChange = { newValue ->
                if (newValue.length <= 6) {
                    if (uiState.pinSetupStep == PinSetupStep.CONFIRM_PIN) {
                        viewModel.updateConfirmPin(newValue)
                    } else {
                        viewModel.updatePin(newValue)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = viewModel::verifyPin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = when {
                    uiState.isFirstTimeSetup && uiState.pinSetupStep == PinSetupStep.ENTER_PIN -> "Continue"
                    uiState.isFirstTimeSetup && uiState.pinSetupStep == PinSetupStep.CONFIRM_PIN -> "Confirm"
                    else -> "Login"
                }
            )
        }
    }
} 