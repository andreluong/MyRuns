package com.andre.myruns

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner


class StartFragment : Fragment() {
    private val TAG = "debug:"

    // inflate the layout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_start, container, false)!!

        initSpinner(view, R.id.inputSpinner, R.array.input_type_array)
        initSpinner(view, R.id.activitySpinner, R.array.activity_array)

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
                        val selectedItem = parent?.getItemAtPosition(position).toString()
                        Log.d(TAG, "Selected Item: $selectedItem")
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }
            }
        }
    }
}