package se.lth.solid.vilmer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.lth.solid.vilmer.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var myLists: ArrayList<CardList>

    private lateinit var mAdapter: CardAdapter

    private var startAddCardActivity = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val card =
                result.data?.getSerializableExtra(AddCardActivity.CARD_EXTRA) as CardDataModel
            val defaultPosition = mAdapter.cardList.cards.size
            val position = result.data?.getIntExtra(
                AddCardActivity.POSITION_EXTRA,
                defaultPosition
            )!!
            if (position == mAdapter.cardList.cards.size) {
                mAdapter.cardList.cards.add(card)
            } else {
                mAdapter.cardList.cards[position] = card
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    private var startManageListsActivity = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            // Update names of lists
            val lists = result.data?.getStringArrayExtra(NEW_LISTS_EXTRA)!!
            val tags =
                result.data?.getSerializableExtra(NEW_TAGS_EXTRA)!! as ArrayList<ArrayList<String>>
            for (i in 0..myLists.lastIndex) {
                myLists[i].name = lists[i]
                myLists[i].tags = tags[i]
            }

            // If a new list was created, add it to the lists and set adapter to that list or chosen
            // list
            if (lists.size > myLists.size) {
                val newCardList = CardList(lists.last(), arrayListOf(), tags.last())
                myLists.add(newCardList)
                mAdapter.cardList = newCardList
            } else {
                val position = result.data?.getIntExtra(DISPLAY_LIST_EXTRA, -1)!!
                mAdapter.cardList = myLists[position]
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        readLists()

        val mRecyclerView: RecyclerView = viewBinding.cardRecycler
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)

        mAdapter = CardAdapter(myLists[0], startAddCardActivity, this)
        mRecyclerView.adapter = mAdapter

        val addCardButton = viewBinding.addCardButton
        addCardButton.setOnClickListener {
            val i = Intent(this, AddCardActivity::class.java)
            val newCard = CardDataModel(null, "", arrayListOf())
            i.putExtra(AddCardActivity.CARD_EXTRA, newCard)
            i.putExtra(AddCardActivity.POSITION_EXTRA, mAdapter.cardList.cards.size)
            i.putExtra(AddCardActivity.TAG_CHOICES_EXTRA, mAdapter.cardList.tags)

            startAddCardActivity.launch(i)
        }

        viewBinding.topAppBar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.search -> {
                    true
                }
                R.id.filter -> {
                    true
                }
                else -> false
            }
        }

        viewBinding.topAppBar.setNavigationOnClickListener { _ ->
            val data = Intent(this, ManageListsActivity::class.java)
            data.putExtra(ManageListsActivity.LISTS_EXTRA, Array(myLists.size) { myLists[it].name })
            data.putExtra(ManageListsActivity.TAGS_EXTRA, Array(myLists.size) { myLists[it].tags })

            startManageListsActivity.launch(data)
        }
    }

    override fun onPause() {
        super.onPause()
        writeLists()
    }

    private fun readLists() {
        try {
            val ois = ObjectInputStream(openFileInput(saveFileName))
            myLists = ois.readObject() as ArrayList<CardList>
            ois.close()
        } catch (e: FileNotFoundException) {
            val firstList = CardList("My List", arrayListOf(), arrayListOf())
            myLists = arrayListOf(firstList)
            writeLists()
        }
    }

    private fun writeLists() {
        try {
            val oos = ObjectOutputStream(openFileOutput(saveFileName, Context.MODE_PRIVATE))
            oos.reset()
            oos.writeObject(myLists)
            oos.flush()
            oos.close()
        } catch (e: FileNotFoundException) {
            print(e.stackTrace)
        }
    }

    companion object {
        const val NEW_LISTS_EXTRA = "se.lth.solid.vilmer.project3.NEW_LISTS_EXTRA"
        const val DISPLAY_LIST_EXTRA = "se.lth.solid.vilmer.project3.DISPLAY_LIST_EXTRA"
        const val NEW_TAGS_EXTRA = "se.lth.solid.vilmer.project3.NEW_TAGS_EXTRA"

        const val saveFileName = "myLists.sr1"
    }
}