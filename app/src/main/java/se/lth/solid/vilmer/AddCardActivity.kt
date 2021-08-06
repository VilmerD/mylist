package se.lth.solid.vilmer

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import com.google.android.material.chip.Chip
import se.lth.solid.vilmer.databinding.ActivityAddCardBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddCardActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddCardBinding

    private lateinit var card: CardDataModel
    var position: Int = 0

    private lateinit var tagChoices: ArrayList<String>

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

        card = intent!!.getSerializableExtra(CARD_EXTRA) as CardDataModel
        position = intent!!.getIntExtra(POSITION_EXTRA, 0)
        tagChoices = intent!!.getSerializableExtra(TAG_CHOICES_EXTRA) as ArrayList<String>

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_card)
        val img = BitmapFactory.decodeFile(card.file?.absolutePath) ?: null
        viewBinding.imageView.setImageBitmap(img)

        viewBinding.photoButton.setOnClickListener {
            takePicture()
        }

        val name = card.name
        if (name != "") viewBinding.cardNameEditText.editText?.setText(name)

        tagChoices.forEach { addChip(it) }
        viewBinding.tagChipGroup.forEach { view ->
            val chip = view as Chip
            card.tags.forEach { s ->
                if (chip.text == s) chip.isChecked = true
            }
        }

        viewBinding.topAppBar.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        viewBinding.topAppBar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                android.R.id.home -> {
                    setResult(RESULT_CANCELED)
                    finish()
                    true
                }
                R.id.done -> {
                    card.name = viewBinding.cardNameEditText.editText?.text.toString()
                    card.tags = getTags()
                    val data = Intent()
                        .putExtra(CARD_EXTRA, card)
                        .putExtra(POSITION_EXTRA, position)
                    setResult(RESULT_OK, data)
                    finish()
                    true
                }
                else -> false
            }
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

    private fun addChip(name: String) {
        val chip = Chip(this)
        chip.text = name

        chip.isClickable = true
        chip.isCheckable = true

        val index = viewBinding.tagChipGroup.childCount - 1
        viewBinding.tagChipGroup.addView(chip as View, index)
    }

    private fun getTags() : ArrayList<String> {
        val chipGroup = viewBinding.tagChipGroup
        val selectedTags : ArrayList<String> = arrayListOf()
        chipGroup.children
            .toList()
            .filter { (it as Chip).isChecked }
            .forEach { selectedTags.add((it as Chip).text.toString()) }
        return selectedTags
    }

    companion object {
        const val CARD_EXTRA = "se.lth.solid.vilmer.project3.CARD_EXTRA"
        const val POSITION_EXTRA = "se.lth.solid.vilmer.project3.POSITION_EXTRA"
        const val TAG_CHOICES_EXTRA = "se.lth.solid.vilmer.project3.TAGS_EXTRA"

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}