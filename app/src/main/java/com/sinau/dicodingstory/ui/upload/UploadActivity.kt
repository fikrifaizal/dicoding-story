package com.sinau.dicodingstory.ui.upload

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.sinau.dicodingstory.R
import com.sinau.dicodingstory.databinding.ActivityUploadBinding
import com.sinau.dicodingstory.utils.MediaUtils.createCustomTempFile
import com.sinau.dicodingstory.utils.MediaUtils.uriToFile
import com.sinau.dicodingstory.utils.animateLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val uploadViewModel: UploadViewModel by viewModels()

    private var token: String = ""
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val imageFile = File(currentPhotoPath)
                val result = BitmapFactory.decodeFile(imageFile.path)
                getFile = imageFile
                binding.ivPreview.setImageBitmap(result)
            }
        }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val selectedImg: Uri = it.data?.data as Uri
                val imageFile = uriToFile(selectedImg, this@UploadActivity)
                getFile = imageFile
                binding.ivPreview.setImageURI(selectedImg)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnCamera.setOnClickListener {
                startTakePhoto()
            }
            btnGallery.setOnClickListener {
                startGallery()
            }
            btnUpload.setOnClickListener {
                getToken()
                uploadStory()
            }
        }

        supportActionBar?.title = getString(R.string.upload_story)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun getToken() {
        lifecycleScope.launchWhenCreated {
            launch {
                uploadViewModel.getToken().collect { getToken ->
                    if (!getToken.isNullOrEmpty()) token = getToken
                }
            }
        }
    }

    // upload story
    private fun uploadStory() {
        showLoading(true)
        val description = binding.etDescription.text.toString()
        var isReadyForUpload = true

        // check file is empty or not
        if (getFile == null) {
            isReadyForUpload = false
            showLoading(false)
            Toast.makeText(
                this@UploadActivity,
                "Please select your picture first",
                Toast.LENGTH_SHORT
            ).show()
        }

        // check description is blank or not
        if (description.isBlank()) {
            isReadyForUpload = false
            showLoading(false)
            Toast.makeText(
                this@UploadActivity,
                "Please write your description",
                Toast.LENGTH_SHORT
            ).show()
        }

        // upload story
        if (isReadyForUpload) {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            lifecycleScope.launchWhenCreated {
                launch {
                    uploadViewModel.uploadStory(token, description, imageMultipart)
                        .collect { result ->
                            result.onSuccess {
                                showLoading(false)
                                Toast.makeText(
                                    this@UploadActivity,
                                    "Successfully uploaded your story",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }

                            result.onFailure {
                                showLoading(false)
                                Toast.makeText(
                                    this@UploadActivity,
                                    "Your story failed to upload",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        } else {
            showLoading(false)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@UploadActivity,
                "com.sinau.dicodingstory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            loadingLayout.animateLoading(isLoading)
            btnCamera.isEnabled = !isLoading
            btnGallery.isEnabled = !isLoading
            btnUpload.isEnabled = !isLoading
        }
    }
}