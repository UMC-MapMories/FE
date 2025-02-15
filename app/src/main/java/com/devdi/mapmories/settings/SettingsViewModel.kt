package com.devdi.mapmories.settings

import androidx.lifecycle.*
import com.devdi.mapmories.RetrofitInstance
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

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
}