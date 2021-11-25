package com.baharudin.camerax_playground

import android.app.Instrumentation
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.baharudin.camerax_playground.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var cameraProviderFeature : ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector : CameraSelector
    private var imageCapture : ImageCapture? = null
    private lateinit var imageCaptureExecutor : ExecutorService

    private val cameraProviderResult = registerForActivityResult(
        ActivityResultContracts
            .RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            startCamera()
        }else {
            Snackbar.make(binding.root,"Camera permission is requerment", Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFeature = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imageCaptureExecutor = Executors.newSingleThreadExecutor()

        cameraProviderResult.launch(android.Manifest.permission.CAMERA)

        startCamera()
        binding.imgCaptureBtn.setOnClickListener {
            takePhoto()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                animateFlash()
            }
        }
        binding.switchBtn.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            //restart kamera
            startCamera()
        }
        binding.galleryBtn.setOnClickListener {
            val intent = Intent(this, GaleryActivity::class.java)
            startActivity(intent)
        }


    }
    private fun startCamera() {
        //listening data dari kamera
        cameraProviderFeature.addListener({
            val cameraProvider = cameraProviderFeature.get()
            imageCapture = ImageCapture.Builder().build()
            //menghubungkan case preview ke preview dalam file xml.
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
            }catch (e : Exception) {
                Log.e(TAG,"Use case binding failed")
            }
        },ContextCompat.getMainExecutor(this))
    }
    private fun takePhoto() {
        imageCapture?.let {capture ->
            //Buat lokasi penyimpanan yang nama filenya diberi stempel waktu dalam milidetik
            val fileName = "JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)
            //Simpan gambar di file di atas
            val outputFileOptions = ImageCapture
                .OutputFileOptions.Builder(file)
                .build()
            capture.takePicture(
                outputFileOptions,
                imageCaptureExecutor,
                object  : ImageCapture.OnImageSavedCallback{
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i(TAG,"Image was captured in ${file.toUri()}")
                    }

                    override fun onError(exception: ImageCaptureException) {
                     Toast.makeText(
                         binding.root.context,
                         "Error taking image",
                         Toast.LENGTH_LONG
                     ).show()
                        Log.d(TAG, "Error taking photo : $exception")
                    }
                }
            )

        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun animateFlash() {
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        },100)
    }
    companion object {
        val TAG = "MainActivity"
    }
}