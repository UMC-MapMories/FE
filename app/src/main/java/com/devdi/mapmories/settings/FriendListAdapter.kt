package com.devdi.mapmories.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.ItemFriendListBinding

class FriendListAdapter(
    private var friends: List<Friend>,
    private val onDeleteClick: (Long) -> Unit
) : RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(val binding: ItemFriendListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            binding.friend = friend
            binding.btnDelete.setOnClickListener {
                onDeleteClick(friend.id)
            }
            // 이미지 로딩 (Glide 사용)
            Glide.with(binding.ivProfile.context)
                .load(friend.profileImg)
                .fallback(R.drawable.ic_profile)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.ivProfile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friends[position])
    }

    override fun getItemCount(): Int = friends.size

    fun updateList(newList: List<Friend>) {
        friends = newList
        notifyDataSetChanged()
    }
}
