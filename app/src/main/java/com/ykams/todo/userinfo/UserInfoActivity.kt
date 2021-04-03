package com.ykams.todo.userinfo

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.ykams.todo.R
import com.ykams.todo.network.Api.userService
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import android.Manifest
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import coil.load
import com.ykams.todo.BuildConfig
import com.ykams.todo.network.Api

class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val takePictureButton = findViewById<Button>(R.id.take_picture_button)
        takePictureButton.setOnClickListener {
            askCameraPermissionAndOpenCamera()
        }

        val uploadImageButton = findViewById<Button>(R.id.upload_image_button)
        uploadImageButton.setOnClickListener {
            pickInGallery.launch("image/*")
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val userInfo = Api.userService.getInfo().body()!!
            val imageView = findViewById<ImageView>(R.id.image_view2)
            imageView.load(userInfo.avatar)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) openCamera()
            else showExplanationDialog()
        }

    private fun requestCameraPermission() =
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

    private fun askCameraPermissionAndOpenCamera() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> openCamera()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> showExplanationDialog()
            else -> requestCameraPermission()
        }
    }

    private fun showExplanationDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("On a besoin de la caméra sivouplé ! 🥺")
            setPositiveButton("Bon, ok") { _, _ ->
                requestCameraPermission()
            }
            setCancelable(true)
            show()
        }
    }

    private suspend fun handleImage(toUri: Uri) {
        val multipartImage = convert(toUri)
        userService.updateAvatar(multipartImage)
    }

    // create a temp file and get a uri for it
    private val photoUri by lazy {
        FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID +".fileprovider",
            File.createTempFile("avatar", ".jpeg", externalCacheDir)

        )
    }

    // register
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success){
            lifecycleScope.launch {
                handleImage(photoUri)
            }
        }
        else Toast.makeText(this, "Erreur ! 😢", Toast.LENGTH_LONG).show()

    }

    // register
    private val pickInGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { photoUri ->
            lifecycleScope.launch {
                handleImage(photoUri)
            }
        }

    // use
    private fun openCamera() = takePicture.launch(photoUri)

    // convert
    private fun convert(uri: Uri) =
        MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = contentResolver.openInputStream(uri)!!.readBytes().toRequestBody()
        )


}