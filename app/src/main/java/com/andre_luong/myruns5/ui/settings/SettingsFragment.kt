package com.andre_luong.myruns5.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.andre_luong.myruns5.util.DialogUtils
import com.andre_luong.myruns5.ProfileActivity
import com.andre_luong.myruns5.R
import com.andre_luong.myruns5.SettingsPreference
import com.andre_luong.myruns5.databinding.FragmentSettingsBinding
import com.andre_luong.myruns5.model.UnitsOfMeasure

class SettingsFragment : Fragment() {
    private val TAG = "debug:"

    private lateinit var settingsPreference: SettingsPreference
    private lateinit var comment: String

    private var privacy = false
    private var selectedUnitIndex = -1

    // Inflate the layout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Load settings from SharedPreferences
        settingsPreference = SettingsPreference(this.requireContext())
        loadSettingsPreference()

        // Initialize buttons
        initProfileButton(view)
        initPrivacyButton(view)
        initUnitPreferenceButton(view)
        initCommentButton(view)

        return view
    }

    private fun loadSettingsPreference() {
        val settings = settingsPreference.getSettings()
        privacy = settings.privacy
        selectedUnitIndex = settings.unit
        comment = settings.comment

        Log.d(TAG, "Loaded settings")
    }

    private fun initProfileButton(view: View) {
        val profileButton = view.findViewById<LinearLayout>(R.id.button_open_profile)
        profileButton.setOnClickListener {
            // Launch profile activity
            val profileIntent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(profileIntent)
        }
    }

    private fun initPrivacyButton(view: View) {
        val checkBox = view.findViewById<CheckBox>(R.id.check_box_privacy)
        checkBox.isChecked = privacy

        // Save privacy to preference
        fun savePrivacy(isChecked: Boolean) {
            privacy = isChecked
            settingsPreference.savePrivacy(privacy)
            Log.d(TAG, "Saved privacy: $privacy")
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            savePrivacy(isChecked)
        }

        val privacyButton = view.findViewById<RelativeLayout>(R.id.button_privacy)
        privacyButton.setOnClickListener {
            checkBox.isChecked = !checkBox.isChecked
        }
    }

    private fun initUnitPreferenceButton(view: View) {
        val unitPreferenceButton =
            view.findViewById<LinearLayout>(R.id.button_open_unit_preference)
        unitPreferenceButton.setOnClickListener {
            showUnitPreferenceDialog()
        }
    }

    private fun showUnitPreferenceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Unit Preference")
            .setSingleChoiceItems(
                UnitsOfMeasure.entries.map { it.value }.toTypedArray(),
                selectedUnitIndex
            ) { dialog, which ->
                selectedUnitIndex = which
                settingsPreference.saveUnit(selectedUnitIndex)
                Log.d(TAG, "Saved unit preference")
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Cancel
            }
            .create()
            .show()
    }

    private fun initCommentButton(view: View) {
        val commentButton = view.findViewById<LinearLayout>(R.id.button_open_comment)
        commentButton.setOnClickListener {
            DialogUtils.showCommentDialog(
                requireContext(),
                comment
            ) { commentEntered ->
                // On OK, save comment to preference
                if (commentEntered != null) {
                    comment = commentEntered
                    settingsPreference.saveComment(comment)
                    Log.d(TAG, "Saved comment: $comment")
                }
            }
        }
    }
}