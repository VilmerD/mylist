package se.lth.solid.vilmer

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
            val card = result.data?.getSerializableExtra(AddCardActivity.CARD_EXTRA) as CardDataModel
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
            for (i in 1..myLists.lastIndex) {
                myLists[i].name = lists[i]
            }

            // If a new list was created, add it to the lists and set adapter to that list or chosen
            // position given by intent
            if (lists.size > myLists.size) {
                val newCardList = CardList(lists.last(), arrayListOf())
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
        readLists()
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val mRecyclerView: RecyclerView = viewBinding.cardRecycler
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)

        mAdapter = CardAdapter(myLists[0], startAddCardActivity)
        mRecyclerView.adapter = mAdapter

        val addCardButton = viewBinding.addCardButton
        addCardButton.setOnClickListener {
            val i = Intent(this, AddCardActivity::class.java)
            val newCard = CardDataModel(null, "", null)
            i.putExtra(AddCardActivity.CARD_EXTRA, newCard)
            i.putExtra(AddCardActivity.POSITION_EXTRA, mAdapter.cardList.cards.size)
            startAddCardActivity.launch(i)
        }

        viewBinding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    true
                }
                R.id.filter -> {
                    true
                }
                else -> false
            }
        }
        viewBinding.bottomAppBar.setNavigationOnClickListener { _: View ->
            val data = Intent(this, ManageListsActivity::class.java)
            data.putExtra(ManageListsActivity.LISTS_EXTRA, Array(myLists.size) { myLists[it].name })

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
            val firstList = CardList("My List", arrayListOf())
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
        const val saveFileName = "myLists.sr1"
        const val NEW_LISTS_EXTRA = "se.lth.solid.vilmer.NEW_LISTS_EXTRA"
        const val DISPLAY_LIST_EXTRA = "se.lth.solid.vilmer.DISPLAY_LIST_EXTRA"
    }
}