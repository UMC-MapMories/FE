package com.devdi.mapmories.people

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.devdi.mapmories.R

class DiaryDetailFragment : Fragment() {

    private var diaryTitle: String? = null
    private var diaryImage: Int = -1

    companion object {
        fun newInstance(title: String, imageResId: Int): DiaryDetailFragment {
            val fragment = DiaryDetailFragment()
            val args = Bundle().apply {
                putString("diaryTitle", title)
                putInt("diaryImage", imageResId)
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
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary_detail, container, false)

        // 다이어리 데이터 표시
        view.findViewById<TextView>(R.id.diary_title).text = diaryTitle
        view.findViewById<ImageView>(R.id.diary_image).setImageResource(diaryImage)

        return view
    }
}