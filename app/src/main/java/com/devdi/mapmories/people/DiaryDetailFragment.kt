package com.devdi.mapmories.people

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.devdi.mapmories.R

class DiaryDetailFragment : Fragment() {

    private var diaryTitle: String? = null
    private var diaryImage: Int = -1
    private var diaryDate: String? = null  // 날짜 추가
    private var diaryContent: String? = null // 내용 추가

    companion object {
        fun newInstance(title: String, imageResId: Int, date: String, content: String): DiaryDetailFragment {
            val fragment = DiaryDetailFragment()
            val args = Bundle().apply {
                putString("diaryTitle", title)
                putInt("diaryImage", imageResId)
                putString("diaryDate", date)   // 날짜 추가
                putString("diaryContent", content)  // 내용 추가
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            diaryTitle = it.getString("diaryTitle")
            diaryImage = it.getInt("diaryImage")
            diaryDate = it.getString("diaryDate") // 날짜 받기
            diaryContent = it.getString("diaryContent") // 내용 받기
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary_detail, container, false)

        // 닫기 버튼 클릭 시 뒤로 가기
        view.findViewById<ImageButton>(R.id.btn_close).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 다이어리 데이터 표시
        view.findViewById<TextView>(R.id.diary_title).text = diaryTitle
        view.findViewById<ImageView>(R.id.diary_image).setImageResource(diaryImage)
        view.findViewById<TextView>(R.id.diary_date).text = "📅 날짜: $diaryDate" // 날짜 추가
        view.findViewById<TextView>(R.id.diary_content).text = diaryContent // 내용 추가

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 물리 Back 버튼도 뒤로 가기 처리
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        })
    }
}