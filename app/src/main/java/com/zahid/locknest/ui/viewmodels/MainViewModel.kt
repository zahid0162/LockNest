package com.zahid.locknest.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.zahid.locknest.ui.theme.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    themeManager: ThemeManager
) : ViewModel() {
    val isDarkMode = themeManager.isDarkMode
} 