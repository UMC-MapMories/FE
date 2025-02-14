package com.devdi.mapmories.community

class DiaryRepository(private val diaryApi: DiaryApi) {

    suspend fun fetchDiaryList(): List<Diary>? {
        return try {
            val response = diaryApi.getDiaries()
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                response.body()?.result
            } else {
                // 필요에 따라 에러 처리
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchDiaryDetail(diaryId: Long): Diary? {
        return try {
            val response = diaryApi.getDiaryDetail(diaryId)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                response.body()?.result
            } else {
                // 필요에 따라 에러 처리
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
