package se.lth.solid.vilmer

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import se.lth.solid.vilmer.databinding.ActivityAddListBinding

class AddListActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddListBinding
    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_list)
        val name = intent.getStringExtra(NAME_EXTRA)
        if (name != resources.getString(R.string.new_list_text)) {
            viewBinding.listNameEditText.editText?.setText(name)
        }
        position = intent.getIntExtra(LIST_SELECTED_EXTRA, -1)
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
    }
}