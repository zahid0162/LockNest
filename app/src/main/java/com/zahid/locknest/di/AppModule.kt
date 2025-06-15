package com.zahid.locknest.di

import android.content.Context
import com.zahid.locknest.data.local.AppDatabase
import com.zahid.locknest.data.local.PasswordDao
import com.zahid.locknest.data.repository.PasswordRepository
import com.zahid.locknest.data.repository.PasswordRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun providePasswordDao(database: AppDatabase): PasswordDao {
        return database.passwordDao()
    }

    @Provides
    @Singleton
    fun providePasswordRepository(
        passwordDao: PasswordDao
    ): PasswordRepository {
        return PasswordRepositoryImpl(passwordDao)
    }
} 