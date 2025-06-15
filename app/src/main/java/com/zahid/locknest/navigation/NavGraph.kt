package com.zahid.locknest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zahid.locknest.ui.screens.*
import com.zahid.locknest.util.BiometricManager
import com.zahid.locknest.util.NavigationUtils.horizontallyAnimatedComposable
import com.zahid.locknest.util.NavigationUtils.verticallyAnimatedComposable
import com.zahid.locknest.util.PasswordGenerator

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object AddPassword : Screen("add_password")
    object PasswordDetail : Screen("password_detail/{passwordId}") {
        fun createRoute(passwordId: String) = "password_detail/$passwordId"
    }

    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    biometricManager: BiometricManager,
    passwordGenerator: PasswordGenerator
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        horizontallyAnimatedComposable(
            Screen.Login.route,

            ) {
            LoginScreen(

                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                biometricManager = biometricManager
            )
        }

        verticallyAnimatedComposable(Screen.Home.route) {
            HomeScreen(
                onAddPassword = {
                    navController.navigate(Screen.AddPassword.route)
                },
                onPasswordClick = { passwordId ->
                    navController.navigate(Screen.PasswordDetail.createRoute(passwordId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        horizontallyAnimatedComposable(Screen.AddPassword.route) {
            AddPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                passwordGenerator = passwordGenerator
            )
        }

        horizontallyAnimatedComposable(
            Screen.PasswordDetail.route,
        ) {
            PasswordDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

       horizontallyAnimatedComposable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}


