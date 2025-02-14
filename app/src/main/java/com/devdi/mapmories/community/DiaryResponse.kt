package com.devdi.mapmories.community

data class Diary(
    val diaryId: Long,
    val country: String? = "정보 없음",       // 기본값 "정보 없음"
    val title: String? = "제목 없음",
    val content: String? = "내용 없음",
    val imgUrl: String? = null,
    val isOpen: Boolean,
    val isCollaborative: Boolean,
    val latitude: Double,
    val longitude: Double,
    val createdAt: String,
    val modifiedAt: String
)

data class DiaryListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<Diary>
)

data class DiaryDetailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Diary
)
