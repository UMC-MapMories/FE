package com.devdi.mapmories.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel(){
    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    var id: MutableLiveData<String> = MutableLiveData("")
    var password: MutableLiveData<String> = MutableLiveData("")

    var showInputNumberActivity : MutableLiveData<Boolean> = MutableLiveData(false)
    var showFindIdActivity : MutableLiveData<Boolean> = MutableLiveData(false)
    var showMainActivity : MutableLiveData<Boolean> = MutableLiveData(false)

    fun loginWithSignupEmail(){
        print("Email")
        auth.createUserWithEmailAndPassword(id.value.toString(),password.value.toString()).addOnCompleteListener {
            if(it.isSuccessful){
                showInputNumberActivity.value=true
            }else{
                //아이디가 있을 경우
            }
        }
    }

    fun loginEmail(){
        showMainActivity.value=true
    }
}