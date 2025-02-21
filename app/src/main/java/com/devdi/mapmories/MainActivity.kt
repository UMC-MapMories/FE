package com.devdi.mapmories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.devdi.mapmories.databinding.ActivityMainBinding
import com.devdi.mapmories.community.PeopleFragment
import com.devdi.mapmories.diary.DiaryFragment
import com.devdi.mapmories.settings.SettingsFragment
import com.devdi.mapmories.shop.ShopFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // 프래그먼트 미리 생성
    private val homeFragment = HomeFragment()
    private val diaryFragment = DiaryFragment()
    private val peopleFragment = PeopleFragment()
    private val shopFragment = ShopFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 모든 프래그먼트를 add() 하여 프래그먼트 매니저에 등록하고,
        // homeFragment를 먼저 보이게 하고 나머지는 hide() 처리합니다.
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frame_layout, homeFragment, "home")
            add(R.id.frame_layout, diaryFragment, "diary")
            add(R.id.frame_layout, peopleFragment, "people")
            add(R.id.frame_layout, shopFragment, "shop")
            add(R.id.frame_layout, settingsFragment, "settings")
        }.commit()

        supportFragmentManager.beginTransaction().apply {
            show(homeFragment)
            hide(diaryFragment)
            hide(peopleFragment)
            hide(shopFragment)
            hide(settingsFragment)
        }.commit()

        binding.bottomNavigationView.selectedItemId = R.id.home

        // BottomNavigationView 메뉴 클릭 리스너
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> showFragment(homeFragment)
                R.id.diary -> showFragment(diaryFragment)
                R.id.people -> showFragment(peopleFragment)
                R.id.shop -> showFragment(shopFragment)
                R.id.settings -> showFragment(settingsFragment)
            }
            true
        }
    }

    // 선택된 프래그먼트를 보이고 나머지는 숨기는 메서드
    private fun showFragment(fragmentToShow: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            // 모든 프래그먼트를 숨김 처리
            listOf(homeFragment, diaryFragment, peopleFragment, shopFragment, settingsFragment)
                .forEach { hide(it) }
            // 선택된 프래그먼트만 보임 처리
            show(fragmentToShow)
        }.commit()
    }
}
