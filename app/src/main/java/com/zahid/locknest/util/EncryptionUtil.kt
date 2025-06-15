package com.zahid.locknest.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionUtil @Inject constructor(
    private val context: Context
) {
    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private val KEY_SIZE = 256
    private val GCM_TAG_LENGTH = 128

    fun createMasterKey(): MasterKey {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .build()

        return MasterKey.Builder(context)
            .setKeyGenParameterSpec(keyGenParameterSpec)
            .build()
    }

    fun getEncryptedSharedPreferences(): EncryptedSharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            createMasterKey(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    fun encryptData(data: String, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(data.toByteArray(Charsets.UTF_8))
    }

    fun decryptData(encryptedData: ByteArray, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, cipher.iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return String(cipher.doFinal(encryptedData), Charsets.UTF_8)
    }

    fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
} 