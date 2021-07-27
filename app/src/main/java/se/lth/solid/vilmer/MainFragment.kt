package se.lth.solid.vilmer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.lth.solid.vilmer.databinding.FragmentMainBinding
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MainFragment : Fragment() {

    private val listViewModel: ListViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentMainBinding

    private lateinit var mAdapter: CardAdapter

    companion object {
        const val saveFileName = "myLists.sr1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lists = readLists()
        listViewModel.myLists = lists
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        val mRecyclerView: RecyclerView = dataBinding.cardRecycler
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        mAdapter = CardAdapter(listViewModel.myLists[0])
        mRecyclerView.adapter = mAdapter

        val addCardButton = dataBinding.addCardButton
        addCardButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addCardFragment)
        }

        return dataBinding.root
    }

    override fun onPause() {
        super.onPause()
        writeLists(listViewModel.myLists)
    }

    private fun readLists() : ArrayList<CardList> {
        return try {
            val ois = ObjectInputStream(context?.openFileInput(saveFileName))
            val list = ois.readObject() as ArrayList<CardList>
            ois.close()
            list
        } catch (e: FileNotFoundException) {
            val firstList = CardList("My List", arrayListOf())
            val lists = arrayListOf(firstList)
            writeLists(lists)
            lists
        }
    }

    private fun writeLists(list: ArrayList<CardList>) {
        try {
            val oos = ObjectOutputStream(context?.openFileOutput(saveFileName, Context.MODE_PRIVATE))
            oos.writeObject(list)
            oos.close()
        } catch (e: FileNotFoundException) {
            print(e.stackTrace)
        }
    }
}