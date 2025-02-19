package com.devdi.mapmories.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.FragmentSettingsBinding
import com.devdi.mapmories.login.LoginActivity
import com.devdi.mapmories.login.ProfileActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // ViewModel 주입
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        // 만약 XML에 viewModel 변수가 정의되어 있다면 바인딩에 할당
        binding.viewModel = settingsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 프로필 수정 버튼 클릭 시 ProfileActivity 실행
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        // 친구 목록 버튼 클릭 -> FriendListFragment로 전환
        binding.btnFriendList.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, FriendsContainerFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.btnAddFriend.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, AddFriendFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // 로그아웃 버튼(my_button5) 클릭 처리
        binding.myButton5.setOnClickListener {
            settingsViewModel.logout()
        }

        // ViewModel의 로그아웃 결과 관찰
        settingsViewModel.logoutResult.observe(viewLifecycleOwner) { result ->
            val (success, message) = result
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            if (success) {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        // 뷰모델의 프로필 정보를 관찰하여 UI 업데이트
        settingsViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let {
                binding.tvUserName.text = it.name
                Glide.with(requireContext())
                    .load(it.profileImg)
                    .fallback(R.drawable.ic_profile)       // profileImg가 null인 경우 기본 이미지
                    .placeholder(R.drawable.ic_profile)    // 로딩 중 표시할 이미지
                    .error(R.drawable.ic_profile)          // 로드 실패 시 기본 이미지
                    .into(binding.profileImage)
            }
        }

        // Fragment가 보여질 때마다 최신 프로필 정보를 불러옴
        settingsViewModel.loadUserProfile()
    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.loadUserProfile()  // 서버에 GET 요청을 보내 최신 프로필 정보를 불러옴
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


