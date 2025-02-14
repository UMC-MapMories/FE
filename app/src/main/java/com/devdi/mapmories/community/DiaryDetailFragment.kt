package com.devdi.mapmories.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.devdi.mapmories.R
import com.devdi.mapmories.RetrofitInstance

class DiaryDetailFragment : Fragment() {

    private var diaryId: Long = -1L

    // activityViewModels를 사용하여 동일한 ViewModel 인스턴스를 공유합니다.
    private val diaryViewModel: DiaryViewModel by activityViewModels {
        DiaryViewModelFactory(DiaryRepository(RetrofitInstance.diaryApi))
    }

    companion object {
        fun newInstance(diaryId: Long): DiaryDetailFragment {
            val fragment = DiaryDetailFragment()
            val args = Bundle()
            args.putLong("diaryId", diaryId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diaryId = arguments?.getLong("diaryId") ?: -1L
        // ViewModel을 통해 상세 데이터를 로드
        diaryViewModel.loadDiaryDetail(diaryId)
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

        // ViewModel의 diaryDetail LiveData를 관찰하여 UI 업데이트
        diaryViewModel.diaryDetail.observe(viewLifecycleOwner) { diary ->
            view.findViewById<TextView>(R.id.diary_title).text = diary.title ?: "제목 없음"
            // imgUrl이 URL이라면 Glide 등으로 이미지 로딩 (아래는 Glide 예제)
            Glide.with(this)
                .load(diary.imgUrl)
                .fallback(R.drawable.ic_default_image)      // imgUrl이 null인 경우 기본 이미지 로드
                .placeholder(R.drawable.ic_default_image)  // 로딩 중 보여줄 이미지 (선택 사항)
                .error(R.drawable.ic_default_image)         // 로드 실패 시 기본 이미지
                .into(view.findViewById<ImageView>(R.id.diary_image))
            view.findViewById<TextView>(R.id.diary_date).text = "📅 날짜: ${diary.createdAt ?: "날짜 없음"}"
            view.findViewById<TextView>(R.id.diary_content).text = diary.content ?: "내용 없음"
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 물리적 Back 버튼도 뒤로 가기 처리
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        })
    }
}
