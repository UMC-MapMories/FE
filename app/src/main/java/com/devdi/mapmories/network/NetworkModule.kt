package com.devdi.mapmories.network

import android.content.Context
import android.content.SharedPreferences
import com.devdi.mapmories.community.DiaryApi
import com.devdi.mapmories.diary.GeocodingRepository
import com.devdi.mapmories.diary.GeocodingRepositoryImpl
import com.devdi.mapmories.login.LoginApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://43.201.246.74:8080/"

    // SharedPreferences 제공 (Application Context 필요)
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    }

    // AuthInterceptor 제공 - SharedPreferences에서 토큰 읽어오기
    @Provides
    @Singleton
    fun provideAuthInterceptor(sharedPreferences: SharedPreferences): AuthInterceptor {
        return AuthInterceptor(sharedPreferences)
    }

    // LoggingInterceptor 제공
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // OkHttpClient 제공 (토큰과 로깅 인터셉터 포함)
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // Retrofit 제공
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Login API 서비스 제공
    @Provides
    @Singleton
    fun provideLoginApi(retrofit: Retrofit): LoginApiService {
        return retrofit.create(LoginApiService::class.java)
    }

    // Diary API 서비스 제공
    @Provides
    @Singleton
    fun provideDiaryApi(retrofit: Retrofit): DiaryApi {
        return retrofit.create(DiaryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingRepository(
        @ApplicationContext context: Context
    ): GeocodingRepository = GeocodingRepositoryImpl(context)
}
