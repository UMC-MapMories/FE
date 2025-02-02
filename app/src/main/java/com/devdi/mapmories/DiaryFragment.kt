
package com.devdi.mapmories

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.Calendar

class DiaryFragment : Fragment() {
    private lateinit var txtSelectedDate: TextView
    private lateinit var imgCamera: ImageView
    private lateinit var edtTitle: EditText
    private lateinit var edtDiary: EditText
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 100
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary, container, false)

        edtTitle = view.findViewById(R.id.edtTitle)
        edtDiary = view.findViewById(R.id.edtDiary)
        val btnCamera: ImageButton = view.findViewById(R.id.btn_camera)
        imgCamera = view.findViewById(R.id.imgCamera)

        txtSelectedDate= view.findViewById(R.id.txtSelectedDate)


        // 화면이 실행되면 즉시 캘린더 표시
        view.post {
            showDatePickerDialog()
        }
        btnCamera.setOnClickListener{
            checkCameraPermission()
        }

        return view
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // 선택한 날짜를 TextView에 표시
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                txtSelectedDate.text = selectedDate
                edtTitle.isEnabled = true
                edtDiary.isEnabled = true
            },
            year, month, day
        )

        // 다이얼로그 띄우기
        datePickerDialog.show()
    }
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없는 경우 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            // 권한이 이미 있는 경우 카메라 실행
            openCamera()
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera() // 권한 허용 시 카메라 실행
            } else {
                // 권한이 거부된 경우 처리 (예: 토스트 메시지)
                txtSelectedDate.text = "Camera permission denied!"
            }
        }
    }

    // 카메라 실행 함수
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    // 촬영된 사진을 ImageView에 표시
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imgCamera.setImageBitmap(imageBitmap)
            imgCamera.visibility = View.VISIBLE
            // 카메라 버튼 숨기기
            val btnCamera: ImageButton = requireView().findViewById(R.id.btn_camera)
            btnCamera.visibility = View.GONE
        }
    }

}
