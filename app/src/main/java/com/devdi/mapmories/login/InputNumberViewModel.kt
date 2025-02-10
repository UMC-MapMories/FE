package com.devdi.mapmories.login

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException


data class FindIdModel(var id : String? = null, var phoneNumber: String?=null)

class InputNumberViewModel(application: Application) : AndroidViewModel(application) { // 🔹 AndroidViewModel로 변경
    var id = MutableLiveData("") // 이메일 입력 필드
    var password = MutableLiveData("") // 비밀번호 입력 필드
    var nextPage = MutableLiveData(false) // 성공 시 다음 페이지 이동

    fun isValidPassword(password: String): Boolean {
        val specialCharacterRegex = ".*[!@#\$%^&*(),.?\":{}|<>].*".toRegex()
        return password.length >= 8 && specialCharacterRegex.containsMatchIn(password)
    }

    fun signup() {
        val email = id.value ?: ""
        val pass = password.value ?: ""

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getApplication(), "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidPassword(pass)) {
            Toast.makeText(getApplication(), "비밀번호는 8자 이상이며, 최소 1개의 특수문자가 포함되어야 합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            try {
                val request = SignupRequest(email, pass)
                Log.d("SignupRequest", "Sending request: $request")

                val response = RetrofitInstance.api.signup(request)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("SignupResponse", "Received response: ${responseBody.toString()}")

                    if (responseBody?.isSuccess == true) {
                        nextPage.postValue(true)
                        Toast.makeText(getApplication(), "회원가입 성공!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(getApplication(), "회원가입 실패: ${responseBody?.message ?: "서버 응답 없음"}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "응답 본문 없음"
                    Toast.makeText(getApplication(), "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("Signup", "HTTP 요청 실패 - 응답 코드: ${response.code()}, 메시지: ${response.message()}, 오류 본문: $errorBody")
                }
            } catch (e: HttpException) {
                Log.e("Signup", "서버 오류: ${e.message}")
                Toast.makeText(getApplication(), "서버 오류 발생", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("Signup", "네트워크 오류: ${e.message}")
                Toast.makeText(getApplication(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }
}