package com.devdi.mapmories.diary

import android.content.Context
import android.location.Address
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GeocodingRepository {

    override suspend fun getCountry(latitude: Double, longitude: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                // 반환값을 nullable로 받고 안전하게 처리
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].countryName ?: "Unknown"
                } else {
                    "Unknown"
                }
            } catch (e: Exception) {
                "Unknown"
            }
        }
    }
}
