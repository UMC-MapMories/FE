package com.devdi.mapmories.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DiaryViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _diaryList = MutableLiveData<List<Diary>>()
    val diaryList: LiveData<List<Diary>> get() = _diaryList

    private val _diaryDetail = MutableLiveData<Diary>()
    val diaryDetail: LiveData<Diary> get() = _diaryDetail

    fun loadDiaryList(isFriendList: Boolean) {
        viewModelScope.launch {
            val diaries = if (isFriendList) {
                repository.fetchFriendDiaryList() // 🔥 친구 다이어리 API 호출
            } else {
                repository.fetchDiaryList() // 🔥 전체 다이어리 API 호출
            }
            diaries?.let {
                _diaryList.postValue(it)
            }
        }
    }

    fun loadDiaryDetail(diaryId: Long) {
        viewModelScope.launch {
            val diary = repository.fetchDiaryDetail(diaryId)
            diary?.let {
                _diaryDetail.postValue(it)
            }
        }
    }
}