package com.andre.myruns

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

class StartFragment : Fragment() {
    private val TAG = "debug:"

    private var selectedInput = 0

    // inflate the layout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_start, container, false)!!

        initSpinner(view, R.id.spinner_input, R.array.input_type_array)
        initSpinner(view, R.id.spinner_activity, R.array.activity_array)

        val startButton = view.findViewById<Button>(R.id.button_start)
        startButton.setOnClickListener {
            when (selectedInput) {
                0 -> {
                    Log.d(TAG, "Opening manual entry activity")
                    val startManualEntryIntent = Intent(requireContext(), ManualEntryActivity::class.java)
                    startActivity(startManualEntryIntent)
                }
                1 -> {
                    Log.d(TAG, "Opening gps activity")
                    val startGpsIntent = Intent(requireContext(), GpsActivity::class.java)
                    startActivity(startGpsIntent)
                }
                2 -> {
                    Log.d(TAG, "Opening automatic activity")
                    val startAutomaticIntent = Intent(requireContext(), AutomaticActivity::class.java)
                    startActivity(startAutomaticIntent)
                }
            }
        }

        return view
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
                        selectedInput = position
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
            }
        }
    }
}