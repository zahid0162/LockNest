package com.zahid.locknest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.zahid.locknest.navigation.NavGraph
import com.zahid.locknest.ui.theme.LockNestTheme
import com.zahid.locknest.ui.viewmodels.MainViewModel
import com.zahid.locknest.util.BiometricManager
import com.zahid.locknest.util.PasswordGenerator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var biometricManager: BiometricManager
    
    @Inject
    lateinit var passwordGenerator: PasswordGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState(initial = isSystemInDarkTheme())
            val navController = rememberNavController()

            LockNestTheme(darkTheme = isDarkMode) {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        biometricManager = biometricManager,
                        passwordGenerator = passwordGenerator
                    )
                }
            }
        }
    }
}