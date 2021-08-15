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

        if (position == -1) {
            list = CardList()
        } else {
            list = lists.myLists[position]
            viewBinding.listNameEditText.editText?.setText(list.name)
        }

        list.tags.forEach { addChip(it) }

        // Preparing buttons
        viewBinding.tagsEditText.setEndIconOnClickListener {
            val tagName = viewBinding.tagsEditText.editText?.text.toString()
            viewBinding.tagsEditText.editText?.text!!.clear()
            list.tags.add(tagName)
            addChip(tagName)
        }

        viewBinding.topAppBar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.done -> {
                    addList()
                    true
                }
                R.id.delete -> {
                    removeList()
                    true
                }
                else -> false
            }
        }

        viewBinding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        return viewBinding.root
    }

    private fun addList() {
        val name = viewBinding.listNameEditText.editText?.text.toString()
        if (name != "") list.name = name
        if (position == -1) lists.addList(list)
        requireActivity().onBackPressed()
    }

    private fun removeList() {
        val dialog = alertBuilder()
        dialog.show()
    }

    private fun addChip(name: String) {
        val chip = Chip(requireContext())
        chip.text = name
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            viewBinding.tagChipGroup.removeView(chip as View)
            list.tags.remove(name)
        }

        chip.isClickable = false
        chip.isCheckable = false

        val chipGroup = viewBinding.tagChipGroup
        val index = chipGroup.childCount
        chipGroup.addView(chip as View, index)
    }

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