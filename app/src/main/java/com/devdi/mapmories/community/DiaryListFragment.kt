package com.devdi.mapmories.community

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devdi.mapmories.R
import com.devdi.mapmories.network.RetrofitInstance


class DiaryListFragment : Fragment() {

    private var isFriendList: Boolean = false
    private lateinit var diaryAdapter: DiaryAdapter

    // 필요에 따라 activityViewModels를 사용해 전체 액티비티와 공유하거나 viewModels로 개별 인스턴스를 사용합니다.
    private val diaryViewModel: DiaryViewModel by viewModels {
        DiaryViewModelFactory(DiaryRepository(RetrofitInstance.diaryApi))
    }

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

        // 2열 GridLayoutManager 설정
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        diaryAdapter = DiaryAdapter(emptyList()) { diaryItem ->
            Log.d("DiaryListFragment", "Item clicked: ${diaryItem.title}")
            // 아이템 클릭 시 diaryId를 전달하여 상세 Fragment 생성
            val detailFragment = DiaryDetailFragment.newInstance(diaryItem.diaryId)

            // 상세화면 컨테이너가 있다면 보이도록 처리
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel의 diaryList LiveData를 관찰하여 어댑터 업데이트
        diaryViewModel.diaryList.observe(viewLifecycleOwner) { diaries ->
            Log.d("DiaryListFragment", "받은 다이어리 개수: ${diaries.size}")

            diaries.forEach { diary ->
                Log.d("DiaryListFragment", "Diary ID: ${diary.diaryId}, isOpen: ${diary.isOpen}") // 🔥 개별 데이터 로그 추가
            }

            val list = if (isFriendList) {
                diaries.filter { it.isOpen } // 🔥 필터링 로직 수정
            } else {
                diaries
            }
            Log.d("DiaryListFragment", "필터링된 다이어리 개수: ${list.size}")

            diaryAdapter.updateList(list)
        }



        // API를 통해 다이어리 목록 로드
        diaryViewModel.loadDiaryList(isFriendList)
    }
}
