package com.andre.myruns

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout

class SettingsFragment : Fragment() {
    private var selectedUnitIndex = -1

    // inflate the layout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)!!

        val profileButton = view.findViewById<LinearLayout>(R.id.profileButton)
        profileButton.setOnClickListener {
            val profileIntent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(profileIntent)
        }

        val privacyButton = view.findViewById<RelativeLayout>(R.id.privacyButton)
        privacyButton.setOnClickListener {
            val checkBox = view.findViewById<CheckBox>(R.id.privacyCheckBox)
            checkBox.isChecked = !checkBox.isChecked
        }

        val unitPreferenceButton = view.findViewById<LinearLayout>(R.id.unitPreferenceButton)
        unitPreferenceButton.setOnClickListener {
            showUnitPreferenceDialog()
        }

        val commentButton = view.findViewById<LinearLayout>(R.id.commentButton)
        commentButton.setOnClickListener {
            showCommentDialog()
        }

        return view
    }

    private fun showUnitPreferenceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Unit Preference")
            .setSingleChoiceItems(
                arrayOf("Metric (km)", "Imperial (mi)"), selectedUnitIndex
            ) { dialog, which ->
                selectedUnitIndex = which
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Cancel
            }
            .create()
            .show()
    }

    private fun showCommentDialog() {
        val container = FrameLayout(requireContext())
        val params = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)

        // Convert 20dp to pixels programmatically
        val marginInDp = TypedValue.applyDimension(
            COMPLEX_UNIT_DIP,
            20f,
            resources.displayMetrics
        ).toInt()
        params.setMargins(marginInDp, marginInDp / 2, marginInDp, marginInDp / 2)

        // Text input
        val input = EditText(requireContext())
        input.layoutParams = params
        container.addView(input)

        AlertDialog.Builder(requireContext())
            .setTitle("Comment")
            .setView(container)
            .setPositiveButton("Ok") { _, _ ->
            }
            .setNegativeButton("Cancel") { _, _ ->
            }
            .create()
            .show()
    }
}