package com.devdi.mapmories

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MapMoriesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        // 여기에서 앱 전체 초기화 작업(예: 로깅, Crashlytics, Analytics 초기화 등)을 수행할 수 있습니다.
    }

    companion object {
        lateinit var instance: MapMoriesApplication
            private set
    }
}
