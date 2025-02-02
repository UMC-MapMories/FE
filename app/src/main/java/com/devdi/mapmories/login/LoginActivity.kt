package com.devdi.mapmories.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.devdi.mapmories.MainActivity
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.ActivityLoginBinding

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException



class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding // 바인딩 객체 선언 -> XML과 Kotlin 코드를 연결
    val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = loginViewModel // ViewModel과 연결(ViewModel과 데이터 바인딩)
        binding.activity = this // 액티비티 참조 설정(Activity를 바인딩하여 XML에서 직접 사용 가능)
        binding.lifecycleOwner = this // LiveData와 연결(LiveData가 자동으로 UI를 업데이트하도록 설정)
        setObserve()
    }

    fun setObserve() {
        loginViewModel.showInputNumberActivity.observe(this) {
            startActivity(Intent(this, InputNumberActivity::class.java))
            finish()
        }

        loginViewModel.showFindIdActivity.observe(this) {
            startActivity(Intent(this, FindIdActivity::class.java))
            finish()
        }

        loginViewModel.showMainActivity.observe(this) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // Google 로그인 결과 처리
    var googleLoginResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                loginViewModel.firebaseAuthWithGoogle(token)
            }
        } catch (e: ApiException) {
            Log.e("GoogleLogin", "Google sign-in failed", e)
            Toast.makeText(this, "Google 로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun startSignup() {
        startActivity(Intent(this, InputNumberActivity::class.java))
    }
}