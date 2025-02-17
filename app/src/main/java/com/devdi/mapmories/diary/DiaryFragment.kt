package com.devdi.mapmories.diary

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.devdi.mapmories.R

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DiaryFragment : Fragment(R.layout.fragment_diary) {

    private val diaryViewModel: DiaryViewModel by viewModels()

    // UI 참조
    private lateinit var edtTitle: EditText
    private lateinit var edtDiary: EditText
    private lateinit var edtLatitude: EditText
    private lateinit var edtLongitude: EditText
    private lateinit var btnSave: Button
    private lateinit var txtSelectedDate: TextView
    private lateinit var imgCamera: ImageView
    private lateinit var imgFlag: ImageView

    // 캡처된 이미지 파일 관련 변수
    private var photoFile: File? = null
    private var photoUri: Uri? = null
    // 업로드 완료된 이미지의 URL (Diary 저장 시 사용)
    private var uploadedImageUrl: String? = null

    @Inject lateinit var geocodingRepository: GeocodingRepository
    @Inject lateinit var imageApi: ImageApi  // ImageApi는 NetworkModule 등에서 제공

    // 카메라 권한 요청 (ActivityResultContracts.RequestPermission 사용)
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // 카메라 촬영 결과 처리 (FileProvider 방식을 사용)
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // FileProvider 방식은 extras에 썸네일이 없으므로, 미리 생성한 photoUri를 이용해 이미지를 표시
                photoUri?.let { uri ->
                    imgCamera.setImageURI(uri)
                    imgCamera.visibility = View.VISIBLE
                    // 촬영한 이미지 파일을 서버에 업로드
                    photoFile?.let { file ->
                        lifecycleScope.launch {
                            val url = uploadImageFile(file)
                            if (url != null) {
                                uploadedImageUrl = url
                                Toast.makeText(requireContext(), "이미지 업로드 성공", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtTitle = view.findViewById(R.id.edtTitle)
        edtDiary = view.findViewById(R.id.edtDiary)
        edtLatitude = view.findViewById(R.id.edtLatitude)
        edtLongitude = view.findViewById(R.id.edtLongitude)
        btnSave = view.findViewById(R.id.btnSave)
        txtSelectedDate = view.findViewById(R.id.txtSelectedDate)
        imgCamera = view.findViewById(R.id.imgCamera)
        imgFlag = view.findViewById(R.id.imgFlag)

        // imgCamera 클릭 시 카메라 권한 체크 후 실행
        imgCamera.setOnClickListener {
            checkCameraPermissionAndLaunchCamera()
        }

        // 날짜 TextView 클릭 시 달력 다이얼로그 재출력 (날짜 수정 가능)
        txtSelectedDate.setOnClickListener {
            showDatePicker()
        }
        // 프래그먼트 진입 시 자동 달력 출력
        showDatePicker()

        // 위도/경도 입력 시 국기 업데이트
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updateCountryFlag() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        edtLatitude.addTextChangedListener(textWatcher)
        edtLongitude.addTextChangedListener(textWatcher)

        btnSave.setOnClickListener {
            val title = edtTitle.text.toString()
            val content = edtDiary.text.toString()
            // 업로드된 이미지 URL 사용 (업로드가 안되었으면 빈 문자열)
            val imgUrl = uploadedImageUrl ?: ""
            val isOpen = true
            val isCollaborative = false
            val latitude = edtLatitude.text.toString().toDoubleOrNull() ?: 0.0
            val longitude = edtLongitude.text.toString().toDoubleOrNull() ?: 0.0
            val date = txtSelectedDate.text.toString()

            diaryViewModel.saveDiary(title, content, imgUrl, isOpen, isCollaborative, latitude, longitude, date)
        }

        diaryViewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Diary saved successfully!", Toast.LENGTH_SHORT).show()
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCameraPermissionAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    // FileProvider 방식을 이용한 카메라 실행
    private fun openCamera() {
        val context = requireContext()
        try {
            photoFile = createImageFile()
            photoFile?.let { file ->
                photoUri = FileProvider.getUriForFile(context, "com.devdi.mapmories.provider", file)
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                if (takePictureIntent.resolveActivity(context.packageManager) != null) {
                    cameraLauncher.launch(takePictureIntent)
                } else {
                    Toast.makeText(context, "카메라를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            Toast.makeText(context, "파일 생성 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    // 서버에 이미지 파일 업로드: 먼저 업로드 URL 발급 API를 호출한 후, OkHttp를 사용하여 PUT 요청
    private suspend fun uploadImageFile(file: File): String? {
        val fileName = file.name
        val contentType = "image/jpeg" // 파일 확장자에 맞게 설정 (예: image/png)
        try {
            val response = imageApi.getUploadUrl(fileName, contentType)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                // 업로드 URL은 result 객체 내의 key에 따라 다릅니다.
                // 예시로 additionalProp1에 업로드 URL이 있다고 가정합니다.
                val uploadUrl = response.body()?.result?.get("additionalProp1")
                if (!uploadUrl.isNullOrEmpty()) {
                    // 파일을 업로드 (PUT 요청)
                    val client = OkHttpClient()
                    val mediaType = contentType.toMediaTypeOrNull()
                    val requestBody = file.asRequestBody(mediaType)
                    val request = Request.Builder()
                        .url(uploadUrl)
                        .put(requestBody)
                        .addHeader("Content-Type", contentType)
                        .build()
                    val uploadResponse = withContext(Dispatchers.IO) { client.newCall(request).execute() }
                    if (uploadResponse.isSuccessful) {
                        // 성공 시, 업로드 URL 또는 서버에서 제공하는 공개 URL을 반환
                        return uploadUrl
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                txtSelectedDate.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateCountryFlag() {
        val lat = edtLatitude.text.toString().toDoubleOrNull()
        val lng = edtLongitude.text.toString().toDoubleOrNull()
        if (lat != null && lng != null) {
            lifecycleScope.launch {
                val country = geocodingRepository.getCountry(lat, lng)
                Log.d("DiaryFragment", "Geocoder returned country: $country")
                val flagRes = getFlagDrawable(country)
                Log.d("DiaryFragment", "Mapped flag resource ID: $flagRes")
                imgFlag.setImageResource(flagRes)
            }
        } else {
            imgFlag.setImageDrawable(null)
        }
    }

    private fun getFlagDrawable(country: String): Int {
        val lowerCountry = country.lowercase(Locale.getDefault())
        return when {
            lowerCountry.contains("algeria") -> R.drawable.flag_algeria
            lowerCountry.contains("united states") || lowerCountry.contains("usa") || lowerCountry == "us" -> R.drawable.flag_us
            // "south korea"를 포함하거나 "korea"가 단독으로 나오는 경우 처리 (주의: "north korea"까지 포함될 수 있음)
            lowerCountry.contains("south korea") || (lowerCountry.contains("korea") && lowerCountry.contains("republic")) -> R.drawable.flag_kr
            lowerCountry.contains("japan") -> R.drawable.flag_japan
            else -> R.drawable.flag_default
        }
    }
}
