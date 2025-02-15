package com.devdi.mapmories.settings

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FriendsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FriendListFragment()     // 전체 친구 목록
            1 -> FriendRequestFragment()  // 친구 요청 목록
            else -> throw IllegalStateException("Invalid position")
        }
    }
}