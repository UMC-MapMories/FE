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
        imgUrl: String?,
        isOpen: Boolean,
        isCollaborative: Boolean,
        latitude: Double?,
        longitude: Double?,
        date: String?
    ) {
        viewModelScope.launch {
            try {
                // 필수 필드 확인
                if (title.isBlank() || content.isBlank() || date.isNullOrBlank()) {
                    _saveResult.postValue(Result.failure(Exception("필수 항목(title, content, date)이 누락되었습니다.")))
                    return@launch
                }

                if (latitude == null || longitude == null) {
                    _saveResult.postValue(Result.failure(Exception("위치 정보가 없습니다.")))
                    return@launch
                }

                // 국가 정보 얻기
                val country = geocodingRepository.getCountry(latitude, longitude) ?: "Unknown"

                // API 요청 객체 생성
                val request = DiaryRequest(
                    country = country,
                    title = title,
                    content = content,
                    imgUrl = imgUrl ?: "",
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
                    val errorBody = response.errorBody()?.string() ?: "서버 오류 발생"
                    _saveResult.postValue(Result.failure(Exception("Error ${response.code()}: $errorBody")))
                }
            } catch (e: Exception) {
                _saveResult.postValue(Result.failure(e))
            }
        }
    }

}