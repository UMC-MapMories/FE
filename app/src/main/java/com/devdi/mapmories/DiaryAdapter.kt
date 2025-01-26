package com.devdi.mapmories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiaryAdapter(private val diaryList: List<DiaryItem>) :
    RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityImage: ImageView = itemView.findViewById(R.id.city_image)
        val cityName: TextView = itemView.findViewById(R.id.city_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val diaryItem = diaryList[position]
        holder.cityName.text = diaryItem.cityName
        holder.cityImage.setImageResource(diaryItem.imageResId)
    }

    override fun getItemCount(): Int = diaryList.size
}
