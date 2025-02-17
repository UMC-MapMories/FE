package com.devdi.mapmories.diary

import DiaryRequest
import DiaryResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdi.mapmories.community.DiaryApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryApi: DiaryApi,
    private val geocodingRepository: GeocodingRepository
) : ViewModel() {

    // 저장 결과를 관찰할 LiveData (DiaryResponse로 변경)
    private val _saveResult = MutableLiveData<Result<DiaryResponse>>()
    val saveResult: LiveData<Result<DiaryResponse>> = _saveResult

    fun saveDiary(
        title: String,
        content: String,
        imgUrl: String,
        isOpen: Boolean,
        isCollaborative: Boolean,
        latitude: Double,
        longitude: Double,
        date: String
    ) {
        viewModelScope.launch {
            try {
                // 위도/경도로부터 국가 이름 얻기
                val country = geocodingRepository.getCountry(latitude, longitude)
                // API 요청에 보낼 객체 생성
                val request = DiaryRequest(
                    country = country,
                    title = title,
                    content = content,
                    imgUrl = imgUrl,
                    isOpen = isOpen,
                    isCollaborative = isCollaborative,
                    latitude = latitude,
                    longitude = longitude,
                    date = date
                )
                // API 호출
                val response = diaryApi.saveDiary(request)
                if (response.isSuccessful && response.body() != null) {
                    _saveResult.postValue(Result.success(response.body()!!))
                } else {
                    _saveResult.postValue(Result.failure(Exception("Error: ${response.code()}")))
                }
            } catch (e: Exception) {
                _saveResult.postValue(Result.failure(e))
            }
        }
    }
}