package com.devdi.mapmories.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdi.mapmories.RetrofitInstance
import kotlinx.coroutines.launch
import org.json.JSONObject

class FriendViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<Friend>>()
    val searchResults: LiveData<List<Friend>> get() = _searchResults

    private val _friendRequests = MutableLiveData<List<Friend>>()
    val friendRequests: LiveData<List<Friend>> get() = _friendRequests

    // 🔹 친구 검색
    fun searchFriends(name: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.friendApi.searchFriends(name)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _searchResults.postValue(response.body()?.result)
                } else {
                    _searchResults.postValue(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _searchResults.postValue(emptyList())
            }
        }
    }

    // 🔹 친구 요청 보내기
    fun sendFriendRequest(toUserId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.friendApi.sendFriendRequest(toUserId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    onResult(true, response.body()?.message ?: "요청 성공")
                } else {
                    // errorBody에서 에러 메시지 파싱
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorJson).getString("message")
                    } catch (e: Exception) {
                        "요청 실패"
                    }
                    onResult(false, errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, e.message ?: "오류 발생")
            }
        }
    }

    // 🔹 받은 친구 요청 목록 불러오기
    fun loadFriendRequests() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.friendApi.getFriendRequests()
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _friendRequests.postValue(response.body()?.result ?: emptyList())
                } else {
                    _friendRequests.postValue(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _friendRequests.postValue(emptyList())
            }
        }
    }

    // 🔹 친구 요청 수락
    fun acceptFriendRequest(fromUserId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.friendApi.acceptFriendRequest(fromUserId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    onResult(true, response.body()?.message ?: "친구 요청 수락됨")
                    loadFriendRequests() // 업데이트
                } else {
                    onResult(false, response.body()?.message ?: "수락 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, e.message ?: "오류 발생")
            }
        }
    }

    // 🔹 친구 요청 거절
    fun rejectFriendRequest(fromUserId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.friendApi.rejectFriendRequest(fromUserId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    onResult(true, response.body()?.message ?: "친구 요청 거절됨")
                    loadFriendRequests() // 업데이트
                } else {
                    onResult(false, response.body()?.message ?: "거절 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, e.message ?: "오류 발생")
            }
        }
    }
}
