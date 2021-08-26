package se.lth.solid.vilmer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import se.lth.solid.vilmer.databinding.FragmentManageListsBinding
import androidx.core.content.res.ResourcesCompat


class ManageListsFragment : Fragment() {

    private lateinit var viewBinding: FragmentManageListsBinding
    private lateinit var mAdapter: MyListAdapter

    private val lists: ListsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_manage_lists, container, false)

        // Setting up the list recycler
        val mRecyclerView = viewBinding.listRecycler
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = MyListAdapter(lists)
        mRecyclerView.adapter = mAdapter

        // Setting up the divider
        val insetDrawable = ResourcesCompat.getDrawable(resources, R.drawable.divider, null)!!
        val itemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        itemDecoration.setDrawable(insetDrawable)
        mRecyclerView.addItemDecoration(itemDecoration)

        // Setting up the top app bar
        viewBinding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        viewBinding.addListButton.setOnClickListener {
            findNavController().navigate(R.id.action_manageListsFragment_to_addListFragment)
        }

        return viewBinding.root
    }
}