package com.zahid.locknest.data.repository

import com.zahid.locknest.data.local.PasswordDao
import com.zahid.locknest.data.model.Password
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface PasswordRepository {
    fun getAllPasswords(): Flow<List<Password>>
    fun getPasswordsByCategory(category: String): Flow<List<Password>>
    fun getAllCategories(): Flow<List<String>>
    suspend fun getPasswordById(id: String): Password?
    suspend fun insertPassword(password: Password)
    suspend fun updatePassword(password: Password)
    suspend fun deletePassword(password: Password)
}

@Singleton
class PasswordRepositoryImpl @Inject constructor(
    private val passwordDao: PasswordDao
) : PasswordRepository {
    override fun getAllPasswords(): Flow<List<Password>> = passwordDao.getAllPasswords()

    override fun getPasswordsByCategory(category: String): Flow<List<Password>> =
        passwordDao.getPasswordsByCategory(category)

    override fun getAllCategories(): Flow<List<String>> = passwordDao.getAllCategories()

    override suspend fun getPasswordById(id: String): Password? = passwordDao.getPasswordById(id)

    override suspend fun insertPassword(password: Password) = passwordDao.insertPassword(password)

    override suspend fun updatePassword(password: Password) = passwordDao.updatePassword(password)

    override suspend fun deletePassword(password: Password) = passwordDao.deletePassword(password)
} 