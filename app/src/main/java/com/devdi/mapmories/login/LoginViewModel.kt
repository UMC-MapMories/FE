package com.devdi.mapmories.login
import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.viewModelScope
import com.devdi.mapmories.R

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val id: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    val showInputNumberActivity = SingleLiveEvent<Unit>()
    val showFindIdActivity = SingleLiveEvent<Unit>()
    val showMainActivity = SingleLiveEvent<Unit>()

    private val context = getApplication<Application>().applicationContext
    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun loginEmail() {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(id.value.orEmpty(), password.value.orEmpty()).await()
                val user = result.user

                if (user != null) {
                    // ID Token 가져오기
                    val idToken = user.getIdToken(true).await().token
                    Log.d("LoginViewModel", "ID Token: $idToken")

                    showMainActivity.call()
                } else {
                    Log.e("LoginViewModel", "User is null after login")
                }
            } catch (e: FirebaseAuthException) {
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidUserException -> "존재하지 않는 이메일입니다."
                    is FirebaseAuthInvalidCredentialsException -> "비밀번호가 틀렸습니다."
                    else -> "로그인 실패: ${e.localizedMessage}"
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
                Log.e("LoginViewModel", "Login failed", e)
            }
        }
    }

    fun loginGoogle(view: View) {
        val intent = googleSignInClient.signInIntent
        (view.context as? LoginActivity)?.googleLoginResult?.launch(intent)
    }

    fun firebaseAuthWithGoogle(idToken: String?) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val user = result.user

                user?.let {
                    // ID Token 가져오기
                    val idTokenResult = user.getIdToken(true).await()
                    val token = idTokenResult.token
                    Log.d("LoginViewModel", "Google ID Token: $token")

                    val userDoc = withContext(Dispatchers.IO) {
                        firestore.collection("users").document(user.uid).get().await()
                    }

                    if (!userDoc.exists()) {
                        val userData = hashMapOf(
                            "uid" to user.uid,
                            "email" to user.email,
                            "displayName" to user.displayName,
                            "phoneNumber" to user.phoneNumber
                        )
                        withContext(Dispatchers.IO) {
                            firestore.collection("users").document(user.uid).set(userData).await()
                        }
                    }

                    showMainActivity.call()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Google 로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("LoginViewModel", "Google login failed", e)
            }
        }
    }

    fun onFindIdClick() {
        showFindIdActivity.call()
    }
}
