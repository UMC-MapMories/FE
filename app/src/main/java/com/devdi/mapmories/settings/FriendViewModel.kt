package com.devdi.mapmories.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdi.mapmories.RetrofitInstance
import kotlinx.coroutines.launch

class FriendViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<Friend>>()
    val searchResults: LiveData<List<Friend>> get() = _searchResults

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

    fun sendFriendRequest(toUserId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val requestBody = FriendRequestBody(toUserId)
                val response = RetrofitInstance.friendApi.sendFriendRequest(requestBody)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    onResult(true, response.body()?.message ?: "요청 성공")
                } else {
                    onResult(false, response.body()?.message ?: "요청 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, e.message ?: "오류 발생")
            }
        }
    }
}
