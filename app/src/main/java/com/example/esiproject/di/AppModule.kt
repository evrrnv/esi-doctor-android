package com.example.esiproject.di

import android.content.Context
import com.example.esiproject.data.AuthRepository
import com.example.esiproject.utils.AppAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppAuthManager(@ApplicationContext context: Context): AppAuthManager {
        return AppAuthManager(context)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(appAuthManager: AppAuthManager): AuthRepository {
        return AuthRepository(appAuthManager)
    }

}