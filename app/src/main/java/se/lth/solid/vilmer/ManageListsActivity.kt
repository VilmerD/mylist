package se.lth.solid.vilmer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import se.lth.solid.vilmer.databinding.ActivityManageListsBinding
import java.lang.IndexOutOfBoundsException

class ManageListsActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityManageListsBinding
    private lateinit var mAdapter: MyListAdapter

    private lateinit var lists: Array<String>

    private var showMenu = false

    var launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val newListName = result.data?.getStringExtra(LIST_NAME_EXTRA)!!
                val position = result.data?.getIntExtra(LIST_SELECTED_EXTRA, -1)!!
                when (position) {
                    -1 -> throw IndexOutOfBoundsException()
                    lists.size -> {
                        lists = Array(lists.size + 1) {
                            if (it < lists.size) lists[it] else newListName
                        }
                    }
                    else -> lists[position] = newListName
                }
                mAdapter.lists = lists
                mAdapter.notifyDataSetChanged()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_manage_lists)

        val extra = intent!!
        lists = extra.getStringArrayExtra((LISTS_EXTRA)) as Array<String>

        val mRecyclerView = viewBinding.listRecycler
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        mRecyclerView.addItemDecoration(divider)

        mAdapter = MyListAdapter(lists, this)
        mRecyclerView.adapter = mAdapter
    }

    fun toggleMenu() {
        showMenu = !showMenu
        invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        if (showMenu) {
            menuInflater.inflate(R.menu.add_list_menu, menu)
        }
        return true
    }

    companion object {
        const val LIST_NAME_EXTRA = "se.lth.solid.vilmer.LIST_NAME_EXTRA"
        const val LIST_SELECTED_EXTRA = "se.lth.solid.vilmer.LIST_SELECTED_EXTRA"
        const val LISTS_EXTRA = "se.lth.solid.vilmer.LISTS_EXTRA"
    }
}