package se.lth.solid.vilmer

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

class AddListFragment : Fragment() {

    private lateinit var viewBinding: FragmentAddListBinding
    private val lists: ListsViewModel by activityViewModels()
    private lateinit var list: CardList

    private val args : AddListFragmentArgs by navArgs()
    private var position = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_list, container, false)
        position = args.position

        if (position == -1) {
            list = CardList(DEFAULT_LIST_NAME)
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
                    list.name = viewBinding.listNameEditText.editText?.text.toString()
                    if (position == -1) lists.addList(list)
                    requireActivity().onBackPressed()
                    true
                }
                R.id.delete -> {
                    lists.safeDeleteList(position)
                    requireActivity().onBackPressed()
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

    companion object {
        const val DEFAULT_LIST_NAME = "_DEFAULT_LIST_NAME"
    }
}