package com.zahid.locknest.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object NavigationUtils {

    fun NavGraphBuilder.horizontallyAnimatedComposable(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        content: @Composable
        AnimatedContentScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route = route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn()
            },
            arguments = arguments,
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut()
            },
            content = content
        )
    }

    fun NavGraphBuilder.verticallyAnimatedComposable(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        content: @Composable
        AnimatedContentScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route = route,
            enterTransition = {
                slideInVertically(initialOffsetY = { 1000 }) + fadeIn()
            },
            arguments = arguments,
            exitTransition = {
                slideOutVertically(targetOffsetY = { -1000 }) + fadeOut()
            },
            popEnterTransition = {
                slideInVertically(initialOffsetY = { -1000 }) + fadeIn()
            },
            popExitTransition = {
                slideOutVertically(targetOffsetY = { 1000 }) + fadeOut()
            },
            content = content
        )
    }
}