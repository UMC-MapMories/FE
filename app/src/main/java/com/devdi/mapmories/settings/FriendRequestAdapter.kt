package com.devdi.mapmories.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.ItemFriendRequestBinding

class FriendRequestAdapter(
    private var requests: List<Friend>,
    private val onAcceptClick: (Long) -> Unit,
    private val onRejectClick: (Long) -> Unit
) : RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder>() {

    inner class FriendRequestViewHolder(val binding: ItemFriendRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            binding.friend = friend
            binding.btnAccept.setOnClickListener { onAcceptClick(friend.id) }
            binding.btnReject.setOnClickListener { onRejectClick(friend.id) }

            // 이미지 로딩 (Glide)
            Glide.with(binding.ivProfile.context)
                .load(friend.profileImg)
                .fallback(R.drawable.ic_profile)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.ivProfile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val binding =
            ItemFriendRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendRequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size

    fun updateList(newList: List<Friend>) {
        requests = newList
        notifyDataSetChanged()
    }
}
