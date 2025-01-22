package com.devdi.mapmories.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.devdi.mapmories.MainActivity
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    lateinit var binding : ActivityLoginBinding
    val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = loginViewModel
        binding.activity = this
        binding.lifecycleOwner = this
        auth = FirebaseAuth.getInstance()
        setObserve()

    }

    fun setObserve(){
        loginViewModel.showInputNumberActivity.observe(this){
            if(it){
                finish()
                startActivity(Intent(this,InputNumberActivity::class.java))
            }
        }
        loginViewModel.showFindIdActivity.observe(this){
            if(it){
                startActivity(Intent(this,FindIdActivity::class.java))
            }
        }
        loginViewModel.showMainActivity.observe(this){
            if(it){
                startActivity(Intent(this,MainActivity::class.java))
            }
        }
    }
    fun loginFacebook(){
        loginViewModel.showMainActivity.value=true
    }

    fun loginWithSignupEmail(){
        print("Email")
        auth.createUserWithEmailAndPassword(loginViewModel.id.value.toString(),loginViewModel.password.value.toString()).addOnCompleteListener {
            if(it.isSuccessful){
                loginViewModel.showInputNumberActivity.value=true
            }else{
                //아이디가 있을 경우
            }
        }
    }
    fun findId(){
        println("findId")
        loginViewModel.showFindIdActivity.value = true
    }
}