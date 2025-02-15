package com.devdi.mapmories.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devdi.mapmories.databinding.FragmentFriendRequestBinding

class FriendRequestFragment : Fragment() {

    private var _binding: FragmentFriendRequestBinding? = null
    private val binding get() = _binding!!

    // activity 범위로 ViewModel을 공유하여 동일한 데이터를 사용
    private val friendViewModel: FriendViewModel by viewModels({ requireActivity() })
    private lateinit var friendRequestAdapter: FriendRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendRequestBinding.inflate(inflater, container, false)
        // ViewModel을 레이아웃에 바인딩 (필요하다면)
        binding.viewModel = friendViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendRequestAdapter = FriendRequestAdapter(emptyList(),
            onAcceptClick = { friendId ->
                friendViewModel.acceptFriendRequest(friendId) { success, message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        friendViewModel.loadFriendRequests()
                    }
                }
            },
            onRejectClick = { friendId ->
                friendViewModel.rejectFriendRequest(friendId) { success, message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        friendViewModel.loadFriendRequests()
                    }
                }
            }
        )

        binding.rvFriendRequests.apply {
            adapter = friendRequestAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        friendViewModel.friendRequests.observe(viewLifecycleOwner) { requests ->
            friendRequestAdapter.updateList(requests)
        }
        friendViewModel.loadFriendRequests()
    }

    override fun onResume() {
        super.onResume()
        friendViewModel.loadFriendRequests()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
