package se.lth.solid.vilmer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.Window
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import se.lth.solid.vilmer.databinding.ActivityMainBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var myLists: ArrayList<CardList>

    private lateinit var mAdapter: CardAdapter

    private var startAddCardActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val card = result.data?.getSerializableExtra(AddCardActivity.CARD_EXTRA) as CardDataModel
            val defaultPosition = mAdapter.cardList.cards.size
            val position = result.data?.getIntExtra(
                AddCardActivity.POSITION_EXTRA,
                defaultPosition
            )!!
            mAdapter.cardList.cards.add(position, card)
            mAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        const val saveFileName = "myLists.sr1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readLists()
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val mRecyclerView: RecyclerView = viewBinding.cardRecycler
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)

        mAdapter = CardAdapter(myLists[0])
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
        viewBinding.bottomAppBar.setNavigationOnClickListener {

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
}