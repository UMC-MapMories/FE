
package com.devdi.mapmories

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.devdi.mapmories.databinding.FragmentDiaryBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var photoUri: Uri
    private lateinit var photoFile: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 날짜 선택 다이얼로그 실행
        binding.txtSelectedDate.setOnClickListener {
            showDatePickerDialog()
        }

        // 화면이 실행되면 즉시 캘린더 표시
        view.post {
            showDatePickerDialog()
        }

        // 카메라 버튼 클릭 리스너
        binding.icCamera.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openCamera()
                }
                else -> {
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }
    }

    // 날짜 선택 다이얼로그
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            ContextThemeWrapper(requireContext(), R.style.CustomDatePickerDialog),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                binding.txtSelectedDate.text = selectedDate
                binding.edtTitle.visibility = View.VISIBLE
                binding.edtDiary.visibility = View.VISIBLE
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // 카메라 권한 요청
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // 카메라 촬영 결과 처리
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.imgCamera.setImageURI(photoUri)
                binding.imgCamera.visibility = View.VISIBLE
                binding.icCamera.visibility = View.GONE
            }
        }

    // 카메라 실행 함수
    private fun openCamera() {
        val context = requireContext()

        try {
            // 파일 생성
            photoFile = createImageFile()

            // FileProvider를 사용하여 URI 가져오기
            photoUri = FileProvider.getUriForFile(context, "com.devdi.mapmories.provider", photoFile)

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION) // 권한 부여
            }

            if (takePictureIntent.resolveActivity(context.packageManager) != null) {
                cameraLauncher.launch(takePictureIntent)
            } else {
                Toast.makeText(context, "카메라를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(context, "파일 생성 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 이미지 파일 생성 함수
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}