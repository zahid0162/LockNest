package com.zahid.locknest.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.zahid.locknest.data.local.AppDatabase
import com.zahid.locknest.data.local.PasswordDao
import com.zahid.locknest.data.repository.PasswordRepository
import com.zahid.locknest.data.repository.PasswordRepositoryImpl
import com.zahid.locknest.util.BackupManager
import com.zahid.locknest.util.EncryptionUtil
import com.zahid.locknest.util.PasswordGenerator
import com.zahid.locknest.util.PasswordGeneratorImpl
import com.zahid.locknest.util.PdfExporter
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
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "locknest_database"
        )
            .fallbackToDestructiveMigration()
            .build()
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

    @Provides
    @Singleton
    fun providePasswordGenerator(): PasswordGenerator {
        return PasswordGeneratorImpl()
    }

    @Provides
    @Singleton
    fun provideBackupManager(
        @ApplicationContext context: Context,
        passwordRepository: PasswordRepository,
        encryptionUtil: EncryptionUtil
    ): BackupManager {
        return BackupManager(context, passwordRepository, encryptionUtil)
    }

    @Provides
    @Singleton
    fun providePdfExporter(
        @ApplicationContext context: Context,
        passwordRepository: PasswordRepository
    ): PdfExporter {
        return PdfExporter(context, passwordRepository)
    }
} 