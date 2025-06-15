package com.zahid.locknest.data.local

import androidx.room.*
import com.zahid.locknest.data.model.Password
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords ORDER BY updatedAt DESC")
    fun getAllPasswords(): Flow<List<Password>>

    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: String): Password?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: Password)

    @Update
    suspend fun updatePassword(password: Password)

    @Delete
    suspend fun deletePassword(password: Password)

    @Query("SELECT * FROM passwords WHERE category = :category ORDER BY updatedAt DESC")
    fun getPasswordsByCategory(category: String): Flow<List<Password>>

    @Query("SELECT DISTINCT category FROM passwords ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>
} 