package com.devdi.mapmories.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.devdi.mapmories.R

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    Glide.with(view.context)
        .load(url)
        .fallback(R.drawable.ic_profile)    // url이 null일 경우 기본 이미지
        .placeholder(R.drawable.ic_profile) // 로딩 중 표시할 이미지
        .error(R.drawable.ic_profile)       // 오류 발생 시 기본 이미지
        .into(view)
}