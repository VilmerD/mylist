package se.lth.solid.vilmer

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import se.lth.solid.vilmer.databinding.FragmentAddListBinding
import java.lang.IllegalStateException

class AddListFragment : Fragment() {

    private lateinit var viewBinding: FragmentAddListBinding
    private val lists: ListsViewModel by activityViewModels()
    private lateinit var list: CardList
    private lateinit var tags: ArrayList<String>

    private val args: AddListFragmentArgs by navArgs()
    private var position = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_list, container, false)
        position = args.position

        // Position of -1 indicates that the user wants to add a new cardlist
        if (position == -1) {
            list = CardList()
        } else {
            list = lists.myLists[position]
            viewBinding.listNameEditText.editText?.setText(list.name)
        }
        tags = list.tags.clone() as ArrayList<String>

        // Preparing buttons
        viewBinding.tagsEditText.setEndIconOnClickListener {
            val tagName = viewBinding.tagsEditText.editText?.text.toString()
            viewBinding.tagsEditText.editText?.text!!.clear()
            tags.add(tagName)
            addChip(tagName)
        }

        // Setting up the top app bar
        viewBinding.topAppBar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.done -> { addList(); true }
                R.id.delete -> { removeList(); true }
                else -> false
            }
        }
        viewBinding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        return viewBinding.root
    }

    override fun onStart() {
        super.onStart()
        tags.forEach { addChip(it) }
    }

    /***
     * Helper function for adding the list to the list of lists
     */
    private fun addList() {
        val name = viewBinding.listNameEditText.editText?.text.toString()
        list.tags = tags
        if (name != "") list.name = name
        if (position == -1) lists.myLists.add(list)
        requireActivity().onBackPressed()
    }

    /***
     * Helper function for removing the list from the list of lists
     */
    private fun removeList() {
        val dialog = alertBuilder()
        dialog.show()
    }

    /***
     * Helper function for adding chips to the chip group
     */
    private fun addChip(name: String) {
        val chip = Chip(requireContext())
        chip.text = name
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            viewBinding.tagChipGroup.removeView(chip as View)
            tags.remove(name)
        }

        chip.isClickable = false
        chip.isCheckable = false

        val chipGroup = viewBinding.tagChipGroup
        val index = chipGroup.childCount
        chipGroup.addView(chip as View, index)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("NAME", viewBinding.listNameEditText.editText?.text.toString())
        outState.putString("TAG", viewBinding.tagsEditText.editText?.text.toString())
        outState.putStringArrayList("TAGS", tags)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            viewBinding.listNameEditText.editText?.setText(savedInstanceState.getString("NAME"))
            viewBinding.tagsEditText.editText?.setText(savedInstanceState.getString("TAG"))
            tags = savedInstanceState.getStringArrayList("TAGS") ?: arrayListOf()
        }
    }

    /***
     * Builds the alert that warns the user from removing the list
     */
    private fun alertBuilder(): AlertDialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.dialog_delete_list)
                .setPositiveButton(R.string.delete_dialog_yes) { _, _ ->
                    lists.safeDeleteList(position)
                    requireActivity().onBackPressed()
                }
                .setNegativeButton(R.string.delete_dialog_no) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}