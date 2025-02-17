package com.devdi.mapmories.diary

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageApiModule {

    @Provides
    @Singleton
    fun provideImageApi(retrofit: Retrofit): ImageApi {
        return retrofit.create(ImageApi::class.java)
    }
}