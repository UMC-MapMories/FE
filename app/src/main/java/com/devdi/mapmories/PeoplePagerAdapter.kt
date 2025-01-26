package com.devdi.mapmories

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PeoplePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // Tab 개수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DiaryListFragment.newInstance(isFriendList = false) // 전체 공개
            1 -> DiaryListFragment.newInstance(isFriendList = true)  // 친구 공개
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
