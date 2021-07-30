package se.lth.solid.vilmer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.lth.solid.vilmer.databinding.ActivityMainBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var myLists: ArrayList<CardList>

    private lateinit var mAdapter: CardAdapter

    companion object {
        const val saveFileName = "myLists.sr1"
        const val NAME_EXTRA = "se.lth.solid.vilmer.project3.NAME_EXTRA"
        const val PATH_EXTRA = "se.lth.solid.vilmer.project3.PATH_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readLists()
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val mRecyclerView: RecyclerView = viewBinding.cardRecycler
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mAdapter = CardAdapter(myLists[0])
        mRecyclerView.adapter = mAdapter

        val addCardButton = viewBinding.addCardButton
        addCardButton.setOnClickListener {
            val i = Intent(this, AddCardActivity::class.java)
            startActivityForResult(i, 0)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                val name = data?.getStringExtra(NAME_EXTRA)
                val path = data?.getSerializableExtra(PATH_EXTRA) as File
                val newCard = CardDataModel(path, name!!, null)
                myLists[0].cards.add(newCard)
                mAdapter.notifyDataSetChanged()
            }
        }
    }
}