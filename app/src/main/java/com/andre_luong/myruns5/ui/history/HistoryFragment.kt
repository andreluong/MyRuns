package com.andre_luong.myruns5.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andre_luong.myruns5.R
import com.andre_luong.myruns5.SettingsPreference
import com.andre_luong.myruns5.adapter.HistoryListAdapter
import com.andre_luong.myruns5.databinding.FragmentHistoryBinding
import com.andre_luong.myruns5.util.ExerciseUtils.buildExerciseViewModel

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsPreference: SettingsPreference

    // Inflate the layout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val exerciseViewModel = buildExerciseViewModel(requireContext(), this)

        settingsPreference = SettingsPreference(requireContext())

        // Set up ArrayAdapter
        val historyListAdapter = HistoryListAdapter(requireContext(), emptyList())
        binding.historyList.adapter = historyListAdapter

        // Observe LiveData from the ViewModel
        exerciseViewModel.allExercisesLiveData.observe(viewLifecycleOwner) { exercises ->
            historyListAdapter.setList(exercises)
            historyListAdapter.notifyDataSetChanged()
        }

        // Listen for changes to the unit preference
        settingsPreference.preference.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == requireContext().getString(R.string.unit_key)) {
                exerciseViewModel.allExercisesLiveData.value?.let { exercises ->
                    historyListAdapter.setList(exercises)
                    historyListAdapter.notifyDataSetChanged()
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}