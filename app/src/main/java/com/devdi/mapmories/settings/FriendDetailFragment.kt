package com.devdi.mapmories.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devdi.mapmories.R

class FriendDetailFragment : Fragment() {

    private var friendId: Long = -1L

    companion object {
        fun newInstance(friendId: Long): FriendDetailFragment {
            val fragment = FriendDetailFragment()
            val args = Bundle()
            args.putLong("friendId", friendId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        friendId = arguments?.getLong("friendId") ?: -1L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 작은 크기로 디자인된 레이아웃 사용 (예: fragment_friend_detail.xml)
        return inflater.inflate(R.layout.fragment_friend_detail, container, false)
    }
}
