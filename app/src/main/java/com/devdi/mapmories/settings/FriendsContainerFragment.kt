package com.devdi.mapmories.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.FragmentFriendsContainerBinding
import com.google.android.material.tabs.TabLayoutMediator

class FriendsContainerFragment : Fragment() {

    private var _binding: FragmentFriendsContainerBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: FriendsPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        pagerAdapter = FriendsPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "친구 목록"
                1 -> tab.text = "친구 신청 목록"
            }
        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
