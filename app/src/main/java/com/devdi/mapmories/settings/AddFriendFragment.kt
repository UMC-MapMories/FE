package com.devdi.mapmories.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devdi.mapmories.databinding.FragmentAddFriendBinding

class AddFriendFragment : Fragment() {

    private var _binding: FragmentAddFriendBinding? = null
    private val binding get() = _binding!!

    private val friendViewModel: FriendViewModel by viewModels()

    private lateinit var friendAdapter: FriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        binding.viewModel = friendViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendAdapter = FriendAdapter(emptyList()) { friend ->
            // 친구 신청 버튼 클릭 시, friend.id를 사용하여 요청 전송
            friendViewModel.sendFriendRequest(friend.id) { success, message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvFriendSearchResults.apply {
            adapter = friendAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // 검색 버튼 클릭 시 입력된 이름으로 친구 검색 호출
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearchName.text.toString().trim()
            if (query.isNotEmpty()) {
                friendViewModel.searchFriends(query)
            }
        }

        binding.btnBack.setOnClickListener {
            // FragmentManager의 popBackStack()을 호출하면 이전 Fragment로 돌아갑니다.
            requireActivity().supportFragmentManager.popBackStack()
        }

        friendViewModel.searchResults.observe(viewLifecycleOwner) { friends ->
            friendAdapter.updateList(friends)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}