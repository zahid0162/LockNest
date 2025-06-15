package com.zahid.locknest.util

import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordGeneratorImpl @Inject constructor() : PasswordGenerator {

    private val secureRandom = SecureRandom()

    private val uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowercaseChars = "abcdefghijklmnopqrstuvwxyz"
    private val numberChars = "0123456789"
    private val specialChars = "!@#$%^&*()-_=+[]{}|;:,.<>?/"

    override fun generatePassword(
        length: Int,
        includeUppercase: Boolean,
        includeLowercase: Boolean,
        includeNumbers: Boolean,
        includeSpecial: Boolean
    ): String {
        require(length >= 4) { "Password length must be at least 4 characters" }
        require(includeUppercase || includeLowercase || includeNumbers || includeSpecial) {
            "At least one character type must be included"
        }

        val charPool = StringBuilder()
        if (includeUppercase) charPool.append(uppercaseChars)
        if (includeLowercase) charPool.append(lowercaseChars)
        if (includeNumbers) charPool.append(numberChars)
        if (includeSpecial) charPool.append(specialChars)

        val password = StringBuilder(length)

        // Ensure at least one character from each selected type
        if (includeUppercase) password.append(uppercaseChars[secureRandom.nextInt(uppercaseChars.length)])
        if (includeLowercase) password.append(lowercaseChars[secureRandom.nextInt(lowercaseChars.length)])
        if (includeNumbers) password.append(numberChars[secureRandom.nextInt(numberChars.length)])
        if (includeSpecial) password.append(specialChars[secureRandom.nextInt(specialChars.length)])

        // Fill the rest of the password with random characters
        while (password.length < length) {
            val randomIndex = secureRandom.nextInt(charPool.length)
            password.append(charPool[randomIndex])
        }

        // Shuffle the password to avoid predictable patterns
        return password.toString().toCharArray().apply { shuffle() }.joinToString("")
    }
}