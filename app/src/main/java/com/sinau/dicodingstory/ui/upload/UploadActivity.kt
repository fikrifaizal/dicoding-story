package com.sinau.dicodingstory.ui.upload

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sinau.dicodingstory.R
import com.sinau.dicodingstory.databinding.ActivityUploadBinding
import com.sinau.dicodingstory.utils.MediaUtils.createCustomTempFile
import com.sinau.dicodingstory.utils.MediaUtils.uriToFile
import com.sinau.dicodingstory.utils.animateLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
@ExperimentalPagingApi
class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val uploadViewModel: UploadViewModel by viewModels()

    private var token: String = ""
    private var getFile: File? = null
    private var location: Location? = null
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
            switchLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    switchLocation.isChecked = true
                    getMyLastLocation()
                } else {
                    switchLocation.isChecked = false
                    location = null
                }
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
            lifecycleScope.launchWhenCreated {
                launch {
                    val convertDescription = description.toRequestBody("text/plain".toMediaType())
                    val file = reduceFileImage(getFile as File)
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )

                    var lat: RequestBody? = null
                    var lon: RequestBody? = null

                    if (location != null) {
                        lat =
                            location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                        lon =
                            location?.longitude.toString().toRequestBody("text/plain".toMediaType())
                    }

                    uploadViewModel.uploadStory(token, convertDescription, imageMultipart, lat, lon)
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

    /*
    * Intent Camera
    */
    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val imageFile = File(currentPhotoPath)
                val result = BitmapFactory.decodeFile(imageFile.path)
                getFile = imageFile
                binding.ivPreview.setImageBitmap(result)
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

    /*
    * Intent Gallery
    */
    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val selectedImg: Uri = it.data?.data as Uri
                val imageFile = uriToFile(selectedImg, this@UploadActivity)
                getFile = imageFile
                binding.ivPreview.setImageURI(selectedImg)
            }
        }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    /*
    * Reduce file image
    */
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

    /*
    * Get Coarse Location
    */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                    binding.switchLocation.isChecked = false
                }
            }
        }

    private fun getMyLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.location = location
                } else {
                    Toast.makeText(
                        this@UploadActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            loadingLayout.animateLoading(isLoading)
            btnCamera.isEnabled = !isLoading
            btnGallery.isEnabled = !isLoading
            btnUpload.isEnabled = !isLoading
        }
    }

    /*
    * first time in this year, i am confuse with my own code :)
    * but this activity will help me in the future :>
    * as long as the code works, it works
    */
}