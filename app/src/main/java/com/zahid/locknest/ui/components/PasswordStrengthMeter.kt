package com.zahid.locknest.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class PasswordStrength(val label: String, val color: Color, val score: Float) {
    WEAK("Weak", Color.Red, 0.25f),
    FAIR("Fair", Color.Yellow, 0.5f),
    GOOD("Good", Color(0xFF4CAF50), 0.75f),
    STRONG("Strong", Color(0xFF2E7D32), 1.0f)
}

@Composable
fun PasswordStrengthMeter(
    password: String,
    modifier: Modifier = Modifier
) {
    val strength = calculatePasswordStrength(password)
    val progress by animateFloatAsState(
        targetValue = strength.score,
        animationSpec = tween(300)
    )
    val color by animateColorAsState(
        targetValue = strength.color,
        animationSpec = tween(300)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password Strength",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = strength.label,
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
        
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

private fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.WEAK
    
    var score = 0
    
    // Length check
    if (password.length >= 8) score += 1
    if (password.length >= 12) score += 1
    
    // Complexity checks
    if (password.any { it.isUpperCase() }) score += 1
    if (password.any { it.isLowerCase() }) score += 1
    if (password.any { it.isDigit() }) score += 1
    if (password.any { !it.isLetterOrDigit() }) score += 1
    
    // Variety check
    val uniqueChars = password.toSet().size
    if (uniqueChars >= 8) score += 1
    
    return when {
        score < 3 -> PasswordStrength.WEAK
        score < 5 -> PasswordStrength.FAIR
        score < 7 -> PasswordStrength.GOOD
        else -> PasswordStrength.STRONG
    }
} 