package com.devdi.mapmories.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


data class FindIdModel(var id : String? = null, var phoneNumber: String?=null)

class InputNumberViewModel : ViewModel() {
    var auth = FirebaseAuth.getInstance()
    var firestore = FirebaseFirestore.getInstance()
    var nextPage = MutableLiveData(false)
    var id = MutableLiveData("") // 이메일 입력 필드
    var password = MutableLiveData("") // 비밀번호 입력 필드
    var inputNumber = MutableLiveData("") //전화번호 입력 필드

    fun savePhoneNumber() {
        val findIdModel = FindIdModel(id.value, inputNumber.value)
        firestore.collection("findIds").document().set(findIdModel).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                nextPage.postValue(true)  // postValue 사용
                auth.currentUser?.let {
                    it.sendEmailVerification()
                } ?: Log.e("InputNumberViewModel", "currentUser is null")
            }
        }
    }

    fun signup() {
        val email = id.value ?: ""
        val pass = password.value ?: ""

        if (email.isEmpty() || pass.isEmpty()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                savePhoneNumber()
            } else {
                Log.e("InputNumberViewModel", "회원가입 실패", it.exception)
            }
        }
    }
}