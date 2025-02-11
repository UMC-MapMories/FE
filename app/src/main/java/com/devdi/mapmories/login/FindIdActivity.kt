package com.devdi.mapmories.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.ActivityFindIdBinding

class FindIdActivity : AppCompatActivity() {
    lateinit var binding: ActivityFindIdBinding
    val findIdViewModel: FindIdViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_id)
        binding.activity = this
        binding.viewModel = findIdViewModel
        binding.lifecycleOwner = this

        binding.backIcon.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 🔹 기존 액티비티를 스택에서 모두 제거
            startActivity(intent)
        }

        setObserve()
    }
    fun setObserve(){
        findIdViewModel.toastMessage.observe(this){
            if(!it.isEmpty()){
                Toast.makeText(this,it,Toast.LENGTH_LONG).show()
            }
        }
    }
}