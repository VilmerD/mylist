package se.lth.solid.vilmer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import se.lth.solid.vilmer.databinding.ActivityAddCardBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddCardActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddCardBinding

    private var name: String = ""
    private var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_card)

        viewBinding.imageView.setImageBitmap(null)

        viewBinding.photoButton.setOnClickListener {
            takePicture()
        }
    }

    private fun takePicture() {
        val i = Intent(this, PhotoActivity::class.java)
        file = File(filesDir,
        SimpleDateFormat(FILENAME_FORMAT, Locale.US
        ).format(System.currentTimeMillis()) + ".jpg")

        i.putExtra(PhotoActivity.FILE, file)

        startActivityForResult(i, CAPTURE_IMAGE_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.add_card_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
                true
            }
            R.id.done -> {
                name = viewBinding.nameEditText.editText?.text.toString()
                val data = Intent()
                data.putExtra(MainActivity.NAME_EXTRA, name)
                data.putExtra(MainActivity.PATH_EXTRA, file)
                setResult(RESULT_OK, data)
                finish()
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val img = BitmapFactory.decodeFile(file!!.absolutePath)
                val height = viewBinding.imageView.height
                val width = viewBinding.imageView.width
                val bm = Bitmap.createScaledBitmap(img, width, height, true)
                viewBinding.imageView.setImageBitmap(bm)
            }
        }
    }

    companion object {
        const val CAPTURE_IMAGE_REQUEST_CODE = 1
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}