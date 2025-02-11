package com.devdi.mapmories.login

import android.app.Activity
import android.content.Intent

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.devdi.mapmories.MainActivity
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    val loginViewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    val googleLoginResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("LoginActivity", "Google Sign-In result received: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            loginViewModel.handleGoogleSignInResult(task)
        } else {
            Log.e("LoginActivity", "Google Sign-In failed with result code: ${result.resultCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up click listener directly in the Activity
        binding.googleLoginButton.setOnClickListener {
            Log.d("LoginActivity", "Google login button clicked in Activity")
            startGoogleSignIn()
        }

        binding.viewModel = loginViewModel
        setObserve()
    }

    private fun startGoogleSignIn() {
        Log.d("LoginActivity", "Starting Google Sign-In")
        try {
            val signInIntent = googleSignInClient.signInIntent
            googleLoginResult.launch(signInIntent)
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error launching Google Sign-In", e)
        }
    }

    private fun setObserve() {
        loginViewModel.showMainActivity.observe(this) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        loginViewModel.showInputNumberActivity.observe(this) {
            startActivity(Intent(this, InputNumberActivity::class.java))
            finish()
        }

        loginViewModel.showFindIdActivity.observe(this) {
            startActivity(Intent(this, FindIdActivity::class.java))
            finish()
        }
    }

    // 🔹 Google 로그인 버튼 클릭
//    private fun startGoogleLogin() {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        val googleSignInClient = GoogleSignIn.getClient(this, gso)
//        val signInIntent = googleSignInClient.signInIntent
//        googleLoginResult.launch(signInIntent)
//    }
}
