package se.lth.solid.vilmer

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import se.lth.solid.vilmer.databinding.ActivityAddListBinding

class AddListActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddListBinding
    private var position = -1
    private var tags: ArrayList<String> = arrayListOf()
    private lateinit var chipGroup : ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Getting information from intent
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_list)
        chipGroup = viewBinding.tagChipGroup
        val name = intent.getStringExtra(NAME_EXTRA)
        if (name != resources.getString(R.string.new_list_text)) {
            viewBinding.listNameEditText.editText?.setText(name)
        }
        position = intent.getIntExtra(LIST_SELECTED_EXTRA, -1)

        // Setting up the chipGroup
        tags = intent.getStringArrayListExtra(TAGS_EXTRA)!!
        tags.forEach { addChip(it) }

        // Preparing buttons
        viewBinding.tagsEditText.setEndIconOnClickListener {
            val tagName = viewBinding.tagsEditText.editText?.text.toString()
            viewBinding.tagsEditText.editText?.text!!.clear()
            tags.add(tagName)

            addChip(tagName)
        }
    }

    private fun addChip(name: String) {
        val chip = Chip(this)
        chip.text = name
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            viewBinding.tagChipGroup.removeView(chip as View)
            tags.remove(name)
        }

        chip.isClickable = false
        chip.isCheckable = false

        val index = viewBinding.tagChipGroup.childCount - 1
        viewBinding.tagChipGroup.addView(chip as View, index)
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
                val listName = viewBinding.listNameEditText.editText?.text.toString()
                val data = Intent()
                    .putExtra(ManageListsActivity.LIST_NAME_EXTRA, listName)
                    .putExtra(ManageListsActivity.LIST_SELECTED_EXTRA, position)
                    .putExtra(ManageListsActivity.TAGS_EXTRA, tags)
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
        const val NAME_EXTRA = "se.lth.solid.vilmer.NAME_EXTRA"
        const val LIST_SELECTED_EXTRA = "se.lth.solid.vilmer.LIST_SELECTED_EXTRA"
        const val TAGS_EXTRA = "se.lth.solid.vilmer.TAGS_EXTRA"
    }
}