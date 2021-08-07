package se.lth.solid.vilmer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.lth.solid.vilmer.databinding.FragmentMainBinding
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MainFragment : Fragment() {

    private lateinit var viewBinding: FragmentMainBinding
    private val lists: ListsViewModel by activityViewModels()

    private lateinit var mAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readLists()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        val mRecyclerView: RecyclerView = viewBinding.cardRecycler
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        mAdapter = CardAdapter(lists.getCardsFiltered())
        mRecyclerView.adapter = mAdapter

        val addCardButton = viewBinding.addCardButton
        addCardButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addCardFragment)
        }

        viewBinding.topAppBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_manageListsFragment)
        }

        viewBinding.topAppBar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.filter -> {
                    findNavController().navigate(R.id.action_mainFragment_to_filterFragment)
                    true
                }
                else -> false
            }
        }

        viewBinding.topAppBar.title = lists.getListName()
        return viewBinding.root
    }

    override fun onPause() {
        super.onPause()
        writeLists()
    }

    private fun readLists() {
        try {
            val ois = ObjectInputStream(requireContext().openFileInput(saveFileName))
            lists.myLists = ois.readObject() as ArrayList<CardList>
            ois.close()
        } catch (e: FileNotFoundException) {
            val firstList = CardList("My List")
            lists.myLists = arrayListOf(firstList)
            writeLists()
        }
    }

    private fun writeLists() {
        try {
            val oos = ObjectOutputStream(
                requireContext().openFileOutput(
                    saveFileName,
                    Context.MODE_PRIVATE
                )
            )
            oos.reset()
            oos.writeObject(lists.myLists)
            oos.flush()
            oos.close()
        } catch (e: FileNotFoundException) {
            print(e.stackTrace)
        }
    }

    companion object {
        const val saveFileName = "myLists.sr1"
    }
}