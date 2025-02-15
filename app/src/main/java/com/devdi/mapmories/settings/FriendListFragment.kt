package com.devdi.mapmories.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.FragmentFriendListBinding

class FriendListFragment : Fragment() {

    private var _binding: FragmentFriendListBinding? = null
    private val binding get() = _binding!!

    // ViewModel은 탭 컨테이너의 activity 범위로 공유합니다.
    private val friendViewModel: FriendViewModel by viewModels({ requireActivity() })
    private lateinit var friendListAdapter: FriendListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendListAdapter = FriendListAdapter(emptyList()) { friendId ->
            friendViewModel.deleteFriend(friendId) { success, message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvFriendList.apply {
            adapter = friendListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        friendViewModel.friendList.observe(viewLifecycleOwner) { list ->
            friendListAdapter.updateList(list)
        }
        friendViewModel.loadFriendList()
    }

    override fun onResume() {
        super.onResume()
        friendViewModel.loadFriendList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
