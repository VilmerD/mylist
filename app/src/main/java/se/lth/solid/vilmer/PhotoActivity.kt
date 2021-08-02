package se.lth.solid.vilmer

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.exifinterface.media.ExifInterface
import se.lth.solid.vilmer.databinding.ActivityPhotoBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityPhotoBinding

    // Image things
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo)

        viewBinding.cameraCaptureButton.setOnClickListener {
            takePhoto()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        //Get storage location from intent
        val file = intent.getSerializableExtra(FILE) as File

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                postProcess(file)
                setResult(RESULT_OK)
                finish()
            }

            override fun onError(exc: ImageCaptureException) {
                setResult(RESULT_CANCELED)
                finish()
            }
        })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.preview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    fun postProcess(imageFile: File) {
        var bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        var width = bitmap.width
        var height = bitmap.height

        // Rotate
        var rotatedBitmap: Bitmap?
        val rotation = getExifRotation(imageFile)
        val frame = Matrix()
        if (rotation != 0 && bitmap != null) {
            frame.postRotate(rotation.toFloat())
            rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height,
                frame, true
            )
            bitmap = null
        } else {
            rotatedBitmap = bitmap
        }

        // Cropping
        width = rotatedBitmap.width
        height = rotatedBitmap.height
        val croppedWidth = if (width < height) width else 4*height/3
        val croppedHeight = if (width < height) 3*width/4 else height
        val x = (width - croppedWidth)/2
        val y = (height - croppedHeight)/2
        val croppedBitmap = Bitmap.createBitmap(rotatedBitmap, x, y, croppedWidth, croppedHeight)

        // Scaling bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true)
        rotatedBitmap = null

        // Save image as jpeg again
        val fops: FileOutputStream?
        try {
            fops = FileOutputStream(imageFile)
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fops)

            fops.flush()
            fops.close()
        } catch (e: FileNotFoundException) {

        } catch (e: Exception) {

        }

    }

    private fun getExifRotation(imageFile: File): Int {
        return try {
            val exif = ExifInterface(imageFile)
            when (exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: IOException) {
            0
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    companion object {
        private const val TAG = "AddCardFragment"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val FILE = "se.lth.solid.vilmer.Project3.FileExtra"

        const val IMAGE_WIDTH = 540
        const val IMAGE_HEIGHT = 540*3/4
    }
}