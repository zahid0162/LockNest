package com.zahid.locknest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.zahid.locknest.util.PasswordGenerator

@Composable
fun PasswordGeneratorDialog(
    onDismissRequest: () -> Unit,
    onPasswordGenerated: (String) -> Unit,
    passwordGenerator: PasswordGenerator
) {
    var passwordLength by remember { mutableIntStateOf(16) }
    var includeUppercase by remember { mutableStateOf(true) }
    var includeLowercase by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSpecial by remember { mutableStateOf(true) }
    var generatedPassword by remember { mutableStateOf("") }
    
    val clipboardManager = LocalClipboardManager.current
    
    LaunchedEffect(Unit) {
        generatedPassword = passwordGenerator.generatePassword(
            length = passwordLength,
            includeUppercase = includeUppercase,
            includeLowercase = includeLowercase,
            includeNumbers = includeNumbers,
            includeSpecial = includeSpecial
        )
    }
    
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Generate Strong Password") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Generated password display
                OutlinedTextField(
                    value = generatedPassword,
                    onValueChange = { generatedPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Generated Password") },
                    trailingIcon = {
                        Row {
                            IconButton(
                                onClick = {
                                    generatedPassword = passwordGenerator.generatePassword(
                                        length = passwordLength,
                                        includeUppercase = includeUppercase,
                                        includeLowercase = includeLowercase,
                                        includeNumbers = includeNumbers,
                                        includeSpecial = includeSpecial
                                    )
                                }
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Generate New")
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(generatedPassword))
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Password")
                            }
                        }
                    },
                    readOnly = true
                )
                
                // Password strength meter
                PasswordStrengthMeter(password = generatedPassword)
                
                // Password length slider
                Text(
                    text = "Password Length: $passwordLength",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = passwordLength.toFloat(),
                    onValueChange = { passwordLength = it.toInt() },
                    valueRange = 8f..32f,
                    steps = 24,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Character type options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includeUppercase,
                            onCheckedChange = { includeUppercase = it }
                        )
                        Text("Uppercase Letters (A-Z)")
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includeLowercase,
                            onCheckedChange = { includeLowercase = it }
                        )
                        Text("Lowercase Letters (a-z)")
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includeNumbers,
                            onCheckedChange = { includeNumbers = it }
                        )
                        Text("Numbers (0-9)")
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includeSpecial,
                            onCheckedChange = { includeSpecial = it }
                        )
                        Text("Special Characters (!@#$%^&*)")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPasswordGenerated(generatedPassword)
                    onDismissRequest()
                }
            ) {
                Text("Use This Password")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        }
    )
} 