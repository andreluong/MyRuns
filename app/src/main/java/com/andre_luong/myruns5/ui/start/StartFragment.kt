package com.andre_luong.myruns5.ui.start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.andre_luong.myruns5.ManualEntryActivity
import com.andre_luong.myruns5.MapDisplayActivity
import com.andre_luong.myruns5.R
import com.andre_luong.myruns5.databinding.FragmentStartBinding
import com.andre_luong.myruns5.model.InputType

class StartFragment : Fragment() {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    private val TAG = "debug:"

    private var selectedInputType = 0
    private var selectedActivity = 0

    // Inflate the layout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentStartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initSpinner(root, R.id.spinner_input, R.array.input_type_array)
        initSpinner(root, R.id.spinner_activity, R.array.activity_array)

        val startButton = root.findViewById<Button>(R.id.button_start)
        startButton.setOnClickListener {
            val activityClass = when (selectedInputType) {
                InputType.MANUAL.id -> {
                    Log.d(TAG, "Opening manual entry activity")
                    ManualEntryActivity::class.java
                }
                InputType.GPS.id -> {
                    Log.d(TAG, "Opening GPS activity")
                    MapDisplayActivity::class.java
                }
                InputType.AUTOMATIC.id -> {
                    Log.d(TAG, "Opening automatic activity")
                    MapDisplayActivity::class.java
                }
                else -> {
                    Log.d(TAG, "Invalid input type, defaulting to manual entry activity")
                    ManualEntryActivity::class.java
                }
            }

            val intent = Intent(requireContext(), activityClass).apply {
                putExtra("inputType", selectedInputType)
                putExtra("activityType", selectedActivity)
            }
            startActivity(intent)
        }
        return root
    }

    private fun initSpinner(v: View, spinnerId: Int, arrayId: Int) {
        val spinner = v.findViewById<Spinner>(spinnerId)

        // Create an ArrayAdapter using the string array and a default spinner layout
        activity?.applicationContext?.let {
            ArrayAdapter.createFromResource(
                it,
                arrayId,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (arrayId == R.array.input_type_array) {
                            selectedInputType = position
                        } else {
                            selectedActivity = position
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
            }
        }
    }
}