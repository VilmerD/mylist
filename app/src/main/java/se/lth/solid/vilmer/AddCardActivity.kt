package se.lth.solid.vilmer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import se.lth.solid.vilmer.databinding.ActivityAddCardBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddCardActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddCardBinding

    private lateinit var card: CardDataModel
    var position: Int = 0

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val img = BitmapFactory.decodeFile(card.file!!.absolutePath)
            viewBinding.imageView.setImageBitmap(img)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        card = intent!!.getSerializableExtra(CARD_EXTRA) as CardDataModel
        position = intent!!.getIntExtra(POSITION_EXTRA, 0)

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_card)

        val img = BitmapFactory.decodeFile(card.file?.absolutePath) ?: null
        viewBinding.imageView.setImageBitmap(img)

        val name = card.name
        if (name != "") viewBinding.cardNameEditText.editText?.setText(name)

        viewBinding.photoButton.setOnClickListener {
            takePicture()
        }
    }

    private fun takePicture() {
        val i = Intent(this, PhotoActivity::class.java)
        if (card.file == null) {
            card.file = File(
                filesDir,
                SimpleDateFormat(
                    FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg"
            )
        }

        i.putExtra(PhotoActivity.FILE, card.file)

        startForResult.launch(i)
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
                card.name = viewBinding.cardNameEditText.editText?.text.toString()
                val data = Intent()
                    .putExtra(CARD_EXTRA, card)
                    .putExtra(POSITION_EXTRA, position)
                setResult(RESULT_OK, data)
                finish()
                true
            }
            else -> {
                false
            }
        }
    }

    companion object {
        const val CARD_EXTRA = "se.lth.solid.vilmer.project3.CARD_EXTRA"
        const val POSITION_EXTRA = "se.lth.solid.vilmer.project3.POSITION_EXTRA"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}