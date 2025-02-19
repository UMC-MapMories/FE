package com.devdi.mapmories.community

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devdi.mapmories.R

class DiaryAdapter(
    private var diaryList: List<Diary>,
    private val onItemClick: (Diary) -> Unit
) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.diary_item_title)
        private val imageView: ImageView = itemView.findViewById(R.id.diary_item_image)

        fun bind(diary: Diary) {
            // 제목이 null이면 "제목 없음" 표시
            titleTextView.text = diary.title ?: "제목 없음"
            // Glide를 사용하여 이미지 로딩, imgUrl이 null이면 fallback 이미지 사용
            Glide.with(itemView.context)
                .load(diary.imgUrl)
                .fallback(R.drawable.ic_default_image)    // imgUrl이 null인 경우 기본 이미지
                .placeholder(R.drawable.ic_default_image) // 로딩 중 표시할 이미지 (선택 사항)
                .error(R.drawable.ic_default_image)        // 로드 실패 시 기본 이미지
                .into(imageView)


            itemView.setOnClickListener {
                onItemClick(diary)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(diaryList[position])
    }

    override fun getItemCount(): Int = diaryList.size

    fun updateList(newList: List<Diary>) {
        diaryList = newList
        Log.d("DiaryAdapter", "어댑터 데이터 업데이트됨: ${diaryList.size} 개") // 🔥 로그 추가
        notifyDataSetChanged()
    }
}