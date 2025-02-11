package com.devdi.mapmories.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.ActivityInputNumberBinding

class InputNumberActivity : AppCompatActivity() {

    lateinit var binding : ActivityInputNumberBinding
    val inputNumberViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(InputNumberViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input_number)
        binding.viewModel = inputNumberViewModel
        binding.lifecycleOwner = this

        binding.backIcon.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 🔹 기존 액티비티를 스택에서 모두 제거
            startActivity(intent)
        }

        inputNumberViewModel.nextPage.observe(this) { isSuccess ->
            if (isSuccess) {
                Log.d("Navigation", "회원가입 성공 → ProfileActivity 이동 시도")

                try {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // 🔹 강제 실행
                    startActivity(intent)
                    Log.d("Navigation", "ProfileActivity 실행 성공")
                    finish()
                } catch (e: Exception) {
                    Log.e("Navigation", "ProfileActivity 실행 실패: ${e.message}")
                }
            }
        }


        setObserve()
    }

    fun setObserve() {
        inputNumberViewModel.nextPage.observe(this) {
            if (it) {
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}