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

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = loginViewModel
        binding.activity = this
        binding.lifecycleOwner = this
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

    fun loginEmail(){
        print("Email")
        loginViewModel.showInputNumberActivity.value = true
    }
    fun findId(){
        println("findId")
        loginViewModel.showFindIdActivity.value = true
    }
}