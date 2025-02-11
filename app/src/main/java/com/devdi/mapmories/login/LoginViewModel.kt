package com.devdi.mapmories.login

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devdi.mapmories.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val id: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")
    val showMainActivity = SingleLiveEvent<Unit>()
    val showFindIdActivity = SingleLiveEvent<Unit>()
    val showInputNumberActivity = SingleLiveEvent<Unit>()

    private val context = getApplication<Application>().applicationContext

    private val auth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient

    init {
        // 🔹 Google 로그인 클라이언트 설정
         val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    // 🔹 이메일 로그인
    fun loginEmail() {
        val email = id.value ?: ""
        val pass = password.value ?: ""

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(context, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            try {
                val request = LoginRequest(email, pass)
                val response = RetrofitInstance.api.login(request)

                if (response.isSuccessful) {
                    // 🔹 JWT 토큰을 Authorization 헤더에서 가져오기
                    val token = response.headers()["Authorization"]
                    if (token != null) {
                        saveToken(token)
                        showMainActivity.call()
                    } else {
                        Toast.makeText(context, "로그인 실패: 토큰이 없음", Toast.LENGTH_SHORT).show()
                        Log.e("LoginViewModel", "No Authorization Header")
                    }
                } else {
                    Log.e("LoginViewModel", "Login Request Failed: ${response.code()}")
                }
            } catch (e: HttpException) {
                Log.e("LoginViewModel", "Server Error: ${e.message}")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Network Error: ${e.message}")
            }
        }
    }

    fun loginGoogle() {
        Log.d("LoginViewModel", "Google login button clicked!")
        val signInIntent = googleSignInClient.signInIntent
        Log.d("LoginViewModel", "Launching Google Sign-In intent...")
        (context as? LoginActivity)?.googleLoginResult?.launch(signInIntent)
    }

    // 🔹 Google 로그인 Intent 실행 (LoginActivity에서 실행해야 함)
    fun getGoogleSignInClient(): GoogleSignInClient {
        return googleSignInClient
    }

    // 🔹 Google 로그인 결과 처리 → 서버로 ID Token 전송
    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val googleToken = account?.idToken

            if (googleToken != null) {
                Log.d("LoginViewModel", "Got Google token: ${googleToken.take(10)}...")
                val credential = GoogleAuthProvider.getCredential(googleToken, null)
                firebaseAuthWithGoogle(credential)
            } else {
                Log.e("LoginViewModel", "Google ID Token is null")
                Toast.makeText(context, "Google 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e("LoginViewModel", "Google Sign-in failed: ${e.statusCode}", e)
            Toast.makeText(context, "Google 로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(credential: AuthCredential) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                Log.d("LoginViewModel", "Firebase auth successful")
                authResult.user?.getIdToken(true)?.addOnSuccessListener { result ->
                    val firebaseToken = result.token
                    if (firebaseToken != null) {
                        sendTokenToServer(firebaseToken)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("LoginViewModel", "Firebase auth failed", e)
                Toast.makeText(context, "인증 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun sendTokenToServer(firebaseToken: String?) {
        if (firebaseToken == null) {
            Log.e("LoginViewModel", "Firebase token is null")
            return
        }

        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Sending token to server...")
                val response = RetrofitInstance.api.loginWithGoogle(firebaseToken)

                if (response.isSuccessful) {
                    val authHeader = response.headers()["authorization"]
                    if (authHeader != null) {
                        Log.d("LoginViewModel", "Server authentication successful")
                        saveToken(authHeader)
                        showMainActivity.call()
                    } else {
                        Log.e("LoginViewModel", "No authorization header in response")
                        Toast.makeText(context, "로그인 실패: 토큰이 없음", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginViewModel", "Server login failed: ${response.code()}, Error: $errorBody")
                    Toast.makeText(context, "서버 로그인 실패 (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Network error", e)
                Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔹 Google ID Token을 서버에 전송하여 JWT 토큰 발급 받기
//    private fun sendGoogleTokenToServer(googleToken: String) {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.api.loginWithGoogle(googleToken)
//
//                if (response.isSuccessful) {
//                    // 🔹 JWT 토큰이 JSON body에 있는지, 헤더에 있는지 확인 필요
//                    val token = response.headers()["Authorization"] ?: response.body()?.result
//                    if (token != null) {
//                        saveToken(token.toString())
//                        showMainActivity.call()
//                    } else {
//                        Toast.makeText(context, "Google 로그인 실패: 토큰이 없음", Toast.LENGTH_SHORT).show()
//                        Log.e("LoginViewModel", "서버에서 토큰을 반환하지 않음")
//                    }
//                } else {
//                    Log.e("LoginViewModel", "Google login request failed with code: ${response.code()}")
//                }
//            } catch (e: HttpException) {
//                Log.e("LoginViewModel", "Server error: ${e.message}")
//            } catch (e: Exception) {
//                Log.e("LoginViewModel", "Network error: ${e.message}")
//            }
//        }
//    }

    // 🔹 받은 JWT 토큰을 SharedPreferences에 저장
    private fun saveToken(accessToken: String?) {
        if (accessToken.isNullOrEmpty()) {
            Log.e("LoginViewModel", "저장할 토큰이 없음")
            return
        }

        val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("accessToken", accessToken)
            apply()
        }
        Log.d("LoginViewModel", "Token saved successfully: $accessToken")
    }

    fun onFindIdClick() {
        showFindIdActivity.call()
    }
    fun startSignup() {
        showInputNumberActivity.call()
    }
}

