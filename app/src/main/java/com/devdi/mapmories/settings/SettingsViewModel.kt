package com.devdi.mapmories.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.devdi.mapmories.network.RetrofitInstance
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    private val _logoutResult = MutableLiveData<Pair<Boolean, String>>()
    val logoutResult: LiveData<Pair<Boolean, String>> get() = _logoutResult

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userApi.getUserProfile()
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _userProfile.postValue(response.body()?.result)
                } else {
                    // 필요 시 에러 처리 (예: 로그 출력)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 필요 시 에러 처리
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // SharedPreferences에서 토큰 가져오기 (혹은 interceptor가 이미 처리하는지 확인)
            val sharedPref = getApplication<Application>().getSharedPreferences("auth", Context.MODE_PRIVATE)
            val token = sharedPref.getString("accessToken", null)
            if (token.isNullOrEmpty()) {
                _logoutResult.postValue(false to "로그아웃 실패: 토큰이 없음")
                return@launch
            }

            try {
                // Interceptor에서 토큰을 헤더에 추가하도록 함
                val response = RetrofitInstance.api.customLogout()
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    clearToken()
                    _logoutResult.postValue(true to (response.body()?.message ?: "로그아웃 성공"))
                } else {
                    _logoutResult.postValue(false to (response.body()?.message ?: "로그아웃 실패"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _logoutResult.postValue(false to (e.message ?: "오류 발생"))
            }
        }
    }



    private fun clearToken() {
        val sharedPref = getApplication<Application>().getSharedPreferences("auth", Context.MODE_PRIVATE)

        sharedPref.edit().clear().apply()
    }


}