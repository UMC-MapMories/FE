package com.devdi.mapmories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.devdi.mapmories.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 앱 시작 시 HomeFragment로 시작
        replaceFragment(HomeFragment())

        binding.bottomNavigationView.selectedItemId = R.id.home

        // BottomNavigationView 메뉴 클릭 리스너
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.diary -> replaceFragment(DiaryFragment())
                R.id.people -> replaceFragment(PeopleFragment())
                R.id.shop -> replaceFragment(ShopFragment())
                R.id.settings -> replaceFragment(SettingsFragment())
                else -> {}
            }
            true
        }
    }

    // 프래그먼트 전환 메서드
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}