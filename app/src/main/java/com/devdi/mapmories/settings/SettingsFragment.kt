package com.devdi.mapmories.settings

import SettingsProfileFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devdi.mapmories.R
import com.devdi.mapmories.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "프로필 수정" 버튼 클릭 시 SettingsProfileFragment로 이동
        binding.btnEditProfile.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, SettingsProfileFragment()) // fragment_container는 현재 Fragment를 담고 있는 레이아웃 ID
            transaction.addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 Fragment로 돌아가도록 설정
            transaction.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


