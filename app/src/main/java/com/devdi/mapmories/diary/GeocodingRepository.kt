package com.devdi.mapmories.diary

interface GeocodingRepository {
    suspend fun getCountry(latitude: Double, longitude: Double): String
}