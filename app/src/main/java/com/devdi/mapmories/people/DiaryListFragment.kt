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
                diaryItem.imageResId
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
            DiaryItem("Canada", R.drawable.dummy_canada),
            DiaryItem("Chicago", R.drawable.dummy_chicago),
            DiaryItem("Seoul", R.drawable.dummy_seoul),
            DiaryItem("Japan", R.drawable.dummy_japan)
        )
    }

    private fun getFriendDiaryList(): List<DiaryItem> {
        return listOf(
            DiaryItem("Seoul", R.drawable.dummy_seoul),
            DiaryItem("Chicago", R.drawable.dummy_chicago)
        )
    }
}