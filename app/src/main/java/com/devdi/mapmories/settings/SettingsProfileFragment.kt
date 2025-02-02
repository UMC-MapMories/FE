import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devdi.mapmories.databinding.FragmentSettingsProfileBinding

class SettingsProfileFragment : Fragment() {

    private var _binding: FragmentSettingsProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "저장하기" 버튼 클릭 시 이전 Fragment(설정 화면)로 돌아감
        binding.btnSave.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // 이전 Fragment로 돌아감
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
