package com.devdi.mapmories.people

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devdi.mapmories.R

class DiaryListFragment : Fragment() {

    private var isFriendList: Boolean = false
    private lateinit var diaryAdapter: DiaryAdapter

    companion object {
        fun newInstance(isFriendList: Boolean): DiaryListFragment {
            val fragment = DiaryListFragment()
            val bundle = Bundle()
            bundle.putBoolean("isFriendList", isFriendList)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFriendList = arguments?.getBoolean("isFriendList") ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        // LayoutManager: 2열 Grid
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Adapter 설정
        val diaryList = if (isFriendList) {
            getFriendDiaryList() // 친구 목록 데이터
        } else {
            getAllDiaryList() // 전체 목록 데이터
        }

        diaryAdapter = DiaryAdapter(diaryList) { diaryItem ->
            Log.d("DiaryListFragment", "Item clicked: ${diaryItem.cityName}") // 디버깅 로그 추가

            val detailFragment = DiaryDetailFragment.newInstance(
                diaryItem.cityName,
                diaryItem.imageResId,
                diaryItem.date,
                diaryItem.content

            )

            // 컨테이너를 VISIBLE로 변경
            val detailContainer = requireActivity().findViewById<View>(R.id.detail_fragment_container)
            detailContainer.visibility = View.VISIBLE

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.detail_fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = diaryAdapter

        return view
    }

    // 더미 데이터 예제 (추후 실제 데이터 소스로 변경 가능)
    private fun getAllDiaryList(): List<DiaryItem> {
        return listOf(
            DiaryItem("Canada", R.drawable.dummy_canada, "2024-02-10", "캐나다에서 스키를 타고 즐거운 하루를 보냈다."),
            DiaryItem("Chicago", R.drawable.dummy_chicago, "2024-01-28", "시카고의 야경은 정말 아름다웠다."),
            DiaryItem("Seoul", R.drawable.dummy_seoul, "2024-02-01", "서울에서 맛있는 떡볶이를 먹었다."),
            DiaryItem("Japan", R.drawable.dummy_japan, "2024-01-15", "일본의 벚꽃은 정말 예뻤다.")
        )
    }

    private fun getFriendDiaryList(): List<DiaryItem> {
        return listOf(
            DiaryItem("Chicago", R.drawable.dummy_chicago, "2024-01-28", "시카고의 야경은 정말 아름다웠다."),
            DiaryItem("Seoul", R.drawable.dummy_seoul, "2024-02-01", "서울에서 맛있는 떡볶이를 먹었다.")
        )
    }
}