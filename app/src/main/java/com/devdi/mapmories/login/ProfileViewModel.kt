package com.devdi.mapmories.login

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devdi.mapmories.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val name = MutableLiveData("")
    val profileImg = MutableLiveData<String?>(null)
    val email = MutableLiveData("")
    val profileUpdated = MutableLiveData(false)

    // 🔹 이미지 선택을 위한 이벤트
    val pickImageEvent = MutableLiveData<Unit>()

    fun pickImage() {
        pickImageEvent.value = Unit
    }

    fun updateProfile() {
        val userName = name.value ?: ""
        val userProfileImg = profileImg.value ?: ""
        val userEmail = email.value ?: ""

        if (userName.isEmpty() || userEmail.isEmpty()) {
            Toast.makeText(getApplication(), "이름과 이메일을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            try {
                val request = ProfileRequest(userName, userProfileImg, userEmail)
                Log.d("ProfileRequest", "Sending request: $request")

                val response = RetrofitInstance.api.updateProfile(request)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("ProfileResponse", "Received response: ${responseBody.toString()}")

                    if (responseBody?.isSuccess == true) {
                        profileUpdated.postValue(true)
                        Toast.makeText(getApplication(), "프로필 등록 성공!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(getApplication(), "프로필 등록 실패: ${responseBody?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "응답 본문 없음"
                    Log.e("Profile", "HTTP 요청 실패 - 응답 코드: ${response.code()}, 메시지: ${response.message()}, 오류 본문: $errorBody")
                }
            } catch (e: HttpException) {
                Log.e("Profile", "서버 오류: ${e.message}")
            } catch (e: Exception) {
                Log.e("Profile", "네트워크 오류: ${e.message}")
            }
        }
    }
}