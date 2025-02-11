package com.devdi.mapmories.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devdi.mapmories.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    private val profileViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(ProfileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Navigation", "ProfileActivity 실행됨!") // 🔹 로그 추가
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = profileViewModel
        binding.lifecycleOwner = this

        // 🔹 이미지 선택 이벤트 감지
        profileViewModel.pickImageEvent.observe(this, Observer {
            pickImageFromGallery()
        })

        // 🔹 프로필 저장 후 LoginActivity로 이동
        profileViewModel.profileUpdated.observe(this) {
            if (it) {
                Toast.makeText(this, "프로필 등록 완료!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java)) // 🔹 LoginActivity로 이동
                finish() // 🔹 LoginActivity 실행 후 현재 화면 종료 (순서 변경)
            }
        }
    }

    // 🔹 갤러리에서 이미지 선택
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getResult.launch(intent)
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                profileViewModel.profileImg.value = it.toString() // 🔹 ViewModel에 이미지 URI 저장
                binding.profileImage.setImageURI(it) // 🔹 이미지 UI 업데이트
            }
        }
    }
}
