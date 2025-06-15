package com.zahid.locknest.util

import android.content.Context
import android.net.Uri
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.zahid.locknest.data.model.Password
import com.zahid.locknest.data.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    private val context: Context,
    private val passwordRepository: PasswordRepository,
    private val encryptionUtil: EncryptionUtil
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Creates an encrypted backup of all passwords
     * @param uri The destination URI to save the backup
     * @return Result with success message or error
     */

    suspend fun exportData(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Get all passwords
            val passwords = passwordRepository.getAllPasswords().first()

            // Convert to JSON
            val jsonData = gson.toJson(passwords)

            // Encrypt the data
            val secretKey = generateSecretKey()
            val encryptedData = encryptionUtil.encryptData(jsonData, secretKey)

            // Write to file
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = OutputStreamWriter(outputStream)
                writer.write(encryptedData.toString(Charsets.UTF_8))
                writer.flush()
            } ?: return@withContext Result.failure(Exception("Failed to open output stream"))

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Date()
            )
            Result.success("Backup created successfully at $timestamp")
        } catch (e: Exception) {
            Result.failure(Exception("Export failed: ${e.message}"))
        }
    }

    /**
     * Imports passwords from an encrypted backup file
     * @param uri The source URI to read the backup from
     * @return Result with success message or error
     */
    suspend fun importData(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Read encrypted data from file
            val encryptedData = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                stringBuilder.toString().toByteArray(Charsets.UTF_8)
            } ?: return@withContext Result.failure(Exception("Failed to open input stream"))

            // Decrypt the data
            val secretKey = generateSecretKey()
            val jsonData = encryptionUtil.decryptData(encryptedData, secretKey)

            // Parse JSON to password list
            val typeToken = object : TypeToken<List<Password>>() {}.type
            val passwords = gson.fromJson<List<Password>>(jsonData, typeToken)

            // Save passwords to database
            passwords.forEach { password ->
                passwordRepository.insertPassword(password)
            }

            Result.success("Successfully imported ${passwords.size} passwords")
        } catch (e: Exception) {
            Result.failure(Exception("Import failed: ${e.message}"))
        }
    }

    /**
     * Generates a SecretKey for encryption/decryption
     * @return SecretKey for AES encryption
     */
    private fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "backup_encryption_key",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
            
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }
} 