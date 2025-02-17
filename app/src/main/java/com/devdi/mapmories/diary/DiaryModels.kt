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
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DiaryResult?
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