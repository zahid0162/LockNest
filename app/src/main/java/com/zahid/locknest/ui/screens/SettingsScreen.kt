package com.zahid.locknest.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zahid.locknest.ui.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPinDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showPdfExportDialog by remember { mutableStateOf(false) }
    var showExportPinDialog by remember { mutableStateOf(false) }
    var includePdfPasswords by remember { mutableStateOf(false) }

    // PDF export launcher
    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        uri?.let {
            viewModel.exportPasswordsToPdf(it, includePdfPasswords)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Theme Toggle
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("Dark Mode")
                    }
                    Switch(
                        checked = uiState.isDarkMode,
                        onCheckedChange = viewModel::setDarkMode
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Security
            Text(
                text = "Security",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Biometric Authentication")
                        }
                        Switch(
                            checked = uiState.isBiometricEnabled,
                            onCheckedChange = { viewModel.toggleBiometric() }
                        )
                    }

                    HorizontalDivider()

                    // Change PIN
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { showPinDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Change PIN")
                        }
                        Icon(
                            Icons.Default.ChevronRight, 
                            contentDescription = "Change PIN"
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockClock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Auto Lock")
                        }
                        Switch(
                            checked = uiState.isAutoLockEnabled,
                            onCheckedChange = { viewModel.toggleAutoLock() }
                        )
                    }

                    if (uiState.isAutoLockEnabled) {
                        HorizontalDivider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Lock after")
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${uiState.autoLockTime} minutes")
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { viewModel.updateAutoLockTime(uiState.autoLockTime - 1) }
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                IconButton(
                                    onClick = { viewModel.updateAutoLockTime(uiState.autoLockTime + 1) }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Data Management Section
            Text(
                text = "Data Management",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Export to PDF
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { showExportPinDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Export Passwords to PDF")
                        }
                        Icon(
                            Icons.Default.ChevronRight, 
                            contentDescription = "Export to PDF"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // About
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "LockNest",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A secure, offline password manager for Android",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Enter Current PIN") },
            text = {
                OutlinedTextField(
                    value = uiState.currentPin,
                    onValueChange = viewModel::updateCurrentPin,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.verifyCurrentPin()
                        if (uiState.isCurrentPinVerified) {
                            showPinDialog = false
                            showConfirmDialog = true
                        }
                    },
                    enabled = uiState.currentPin.length >= 4
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showConfirmDialog && uiState.isCurrentPinVerified) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Enter New PIN") },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.newPin,
                        onValueChange = viewModel::updateNewPin,
                        label = { Text("New PIN") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.confirmPin,
                        onValueChange = viewModel::updateConfirmPin,
                        label = { Text("Confirm PIN") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.changePin()
                        showConfirmDialog = false
                    },
                    enabled = uiState.newPin.length >= 4 && uiState.newPin == uiState.confirmPin
                ) {
                    Text("Change PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // PIN verification dialog for export
    if (showExportPinDialog) {
        AlertDialog(
            onDismissRequest = { showExportPinDialog = false },
            title = { Text("Security Verification") },
            text = {
                Column {
                    Text("Please enter your PIN to continue with the export")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.exportVerificationPin,
                        onValueChange = viewModel::updateExportVerificationPin,
                        label = { Text("PIN") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    
                    if (uiState.exportPinError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.exportPinError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.verifyExportPin { success ->
                            if (success) {
                                showExportPinDialog = false
                                showPdfExportDialog = true
                            }
                        }
                    },
                    enabled = uiState.exportVerificationPin.length >= 4
                ) {
                    Text("Verify")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showExportPinDialog = false
                    viewModel.clearExportVerificationPin()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (showPdfExportDialog) {
        AlertDialog(
            onDismissRequest = { showPdfExportDialog = false },
            title = { Text("Export Passwords to PDF") },
            text = {
                Column {
                    Text("This will export all your passwords to a PDF file.")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includePdfPasswords,
                            onCheckedChange = { includePdfPasswords = it }
                        )
                        Text("Include actual passwords in export")
                    }
                    
                    if (includePdfPasswords) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Warning: Including passwords in the export creates a security risk. " +
                            "Make sure to keep the file secure.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    if (uiState.isPdfExporting) {
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Exporting passwords...")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pdfExportLauncher.launch("locknest_passwords.pdf")
                        showPdfExportDialog = false
                    },
                    enabled = !uiState.isPdfExporting
                ) {
                    Text("Export")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPdfExportDialog = false },
                    enabled = !uiState.isPdfExporting
                ) {
                    Text("Cancel")
                }
            }
        )
    }
} 