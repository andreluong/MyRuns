package com.andre_luong.myruns5

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.EXTRA_OUTPUT
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.andre_luong.myruns5.util.Utils
import java.io.File

class ProfileActivity: AppCompatActivity(), View.OnClickListener {
    private val TAG = "debug:"
    private val TEMP_IMG_FILENAME = "tempPhoto.jpg"
    private val SAVED_IMG_FILENAME = "savedPhoto.jpg"
    private val AUTHORITY = "com.andre_luong.myruns5"

    private lateinit var photoView: ImageView
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var editGender: RadioGroup
    private lateinit var editClass: EditText
    private lateinit var editMajor: EditText

    private lateinit var tempImgUri: Uri
    private lateinit var viewModel: PhotoViewModel
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var tempImgFile: File
    private lateinit var savedImgFile: File

    private lateinit var profilePreference: ProfilePreference

    private var photoTaken = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initButtons()
        initInputFields()

        // Load profile from SharedPreferences
        profilePreference = ProfilePreference(this)
        loadProfileFromPreference()

        // Finds the temp img file
        tempImgFile = File(
            getExternalFilesDir(DIRECTORY_PICTURES),
            TEMP_IMG_FILENAME
        )
        tempImgUri = FileProvider.getUriForFile(
            this,
            AUTHORITY,
            tempImgFile
        )

        // Finds the saved img file
        savedImgFile = File(
            getExternalFilesDir(DIRECTORY_PICTURES),
            SAVED_IMG_FILENAME
        )
        val savedImgUri = FileProvider.getUriForFile(
            this,
            AUTHORITY,
            savedImgFile
        )

        // Loads the saved image
        // If no image exists, load snoopy
        if (savedImgFile.exists()) {
            val bitmap = Utils.getBitmap(this, savedImgUri)
            photoView.setImageBitmap(bitmap)
            Log.d(TAG, "Loaded saved profile photo")
        } else {
            photoView.setImageResource(R.drawable.snoopy_peanuts)
        }

        // Use camera to take photo
        cameraLauncher = registerForActivityResult(StartActivityForResult()) {
            result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    changeProfilePhoto(tempImgUri)
                    Log.d(TAG, "Captured profile photo")
                }
        }

        // Use gallery to select a photo
        galleryLauncher = registerForActivityResult(StartActivityForResult()) {
            result ->
                if (result.resultCode == RESULT_OK) {
                    val selectedImageUri: Uri? = result.data?.data
                    if (selectedImageUri != null) {
                        tempImgFile = Utils.copyUriToFile(
                            this,
                            selectedImageUri,
                            tempImgFile
                        )
                        changeProfilePhoto(selectedImageUri)
                    }
                    Log.d(TAG, "Selected profile photo from gallery")
                }
        }

        // Change profile photo after capture
        viewModel = ViewModelProvider(this)[PhotoViewModel::class.java]
        viewModel.userImage.observe(this) {
            photoView.setImageBitmap(it)
        }

    }

    private fun initButtons() {
        findViewById<Button>(R.id.button_change_photo)
            .setOnClickListener(this)
        findViewById<Button>(R.id.button_cancel_profile)
            .setOnClickListener(this)
        findViewById<Button>(R.id.button_save_profile)
            .setOnClickListener(this)
    }

    private fun initInputFields() {
        photoView = findViewById(R.id.image_view_photo)
        editName = findViewById(R.id.edit_name)
        editEmail = findViewById(R.id.edit_email)
        editPhone = findViewById(R.id.edit_phone)
        editGender = findViewById(R.id.edit_gender)
        editClass = findViewById(R.id.edit_class)
        editMajor = findViewById(R.id.edit_major)
    }

    // Button click functionality
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_change_photo -> {
                Log.d(TAG, "Open photo dialog")
                showPhotoDialog()
            }
            R.id.button_cancel_profile -> {
                Log.d(TAG, "Leaving profile activity")
                finish()
            }
            R.id.button_save_profile -> {
                Log.d(TAG, "Saving profile...")
                saveProfile()
                finish()
            }
        }
    }

    private fun changeProfilePhoto(imgUri: Uri) {
        val bitmap = Utils.getBitmap(this, imgUri)
        viewModel.userImage.value = bitmap
        photoTaken = true
    }

    private fun showPhotoDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select Profile Photo")
            .setPositiveButton("Take from Camera") { _, _ ->
                Log.d(TAG, "Capturing new profile photo...")
                openCamera()
            }
            .setNegativeButton("Select from Gallery") { _, _ ->
                Log.d(TAG, "Selecting new profile photo...")
                openGallery()
            }
            .create()
            .show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(EXTRA_OUTPUT, tempImgUri)
        cameraLauncher.launch(cameraIntent)
    }

    private fun openGallery() {
        val galleryIntent = Intent(ACTION_PICK, EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    // Extracts data from a SharedPreference object and sets to fields
    private fun loadProfileFromPreference() {
        val profile = profilePreference.getProfile()
        editName.setText(profile.name)
        editEmail.setText(profile.email)
        editPhone.setText(profile.phone)
        editClass.setText(profile.classValue)
        editMajor.setText(profile.major)

        if (profile.gender.equals("Female", true)) {
            editGender.check(R.id.gender_female)
        } else if (profile.gender.equals("Male", true)) {
            editGender.check(R.id.gender_male)
        }

        Log.d(TAG, "Loaded user profile")
    }

    // Saves the user input data to a SharedPreference object
    private fun saveProfile() {
        // Extract gender from radio group
        val genderSelect = editGender.checkedRadioButtonId
        val gender: String = if (genderSelect != -1) {
            (findViewById<RadioButton>(genderSelect)!!).text.toString()
        } else {
            "" // No selection
        }

        // Overwrites the saved img file with temp if a photo was taken this session
        if (photoTaken) {

            tempImgFile.copyTo(savedImgFile, overwrite = true)
            Log.d(TAG, "Overwrite saved photo")
        }

        profilePreference.setProfile(
            editName.text.toString(),
            editEmail.text.toString(),
            editPhone.text.toString(),
            gender,
            editClass.text.toString(),
            editMajor.text.toString()
        )

        val msg = "Saved user profile"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        Log.d(TAG, msg)
    }
}