package com.zahid.locknest.util

import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for password generation functionality
 */
interface PasswordGenerator {
    /**
     * Generates a secure random password with customizable options
     * 
     * @param length The length of the password to generate
     * @param includeUppercase Whether to include uppercase letters
     * @param includeLowercase Whether to include lowercase letters
     * @param includeNumbers Whether to include numbers
     * @param includeSpecial Whether to include special characters
     * @return A securely generated random password
     */
    fun generatePassword(
        length: Int = 16,
        includeUppercase: Boolean = true,
        includeLowercase: Boolean = true,
        includeNumbers: Boolean = true,
        includeSpecial: Boolean = true
    ): String
}

