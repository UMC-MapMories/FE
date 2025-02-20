data class DiaryRequest(
    val country: String,
    val title: String,
    val content: String,
    val imgUrl: String,
    val isOpen: Boolean,
    val isCollaborative: Boolean,
    val latitude: Double,
    val longitude: Double,
    val date: String
)

data class DiaryResponse(
    val message: String,
    val status: Int,  // API 응답의 "status"를 반영
    val timestamp: String
)

data class DiaryResult(
    val diaryId: Int,
    val country: String,
    val title: String,
    val content: String,
    val imgUrl: String,
    val isOpen: Boolean,
    val isCollaborative: Boolean,
    val latitude: Double,
    val longitude: Double,
    val createdAt: String,
    val modifiedAt: String
)