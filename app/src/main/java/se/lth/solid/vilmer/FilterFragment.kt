package se.lth.solid.vilmer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import se.lth.solid.vilmer.databinding.FragmentFilterBinding

class FilterFragment : Fragment() {

    private lateinit var viewBinding: FragmentFilterBinding
    private val lists: ListsViewModel by activityViewModels()

    private var checkedTags: ArrayList<String>
        get() {
            // Getting the chips that are selected by the user
            val filters: ArrayList<String> = arrayListOf()
            viewBinding.filterChipGroup.children
                .toList()
                .filter { (it as Chip).isChecked }
                .forEach { filters.add((it as Chip).text.toString()) }
            return filters
        }
        set(newLists) {
            viewBinding.filterChipGroup.children.forEach {
                if (newLists.contains((it as Chip).text)) it.isChecked = true
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)

        // Setting up the chip group
        val chipGroup = viewBinding.filterChipGroup
        val tags = lists.list.tags
        chipGroup.removeAllViews()
        tags.forEach { s: String ->
            val chip = Chip(context)
            chip.text = s
            chip.isClickable = true
            chip.isCheckable = true

            chipGroup.addView(chip as View)
        }
        checkedTags = lists.tagFilters
        val filerGradeButton = viewBinding.filterRatingButton
        filerGradeButton.isChecked = lists.filterGrade < 0

        // Setting up the top app bar
        viewBinding.topAppBar.setNavigationOnClickListener {
            lists.tagFilters = arrayListOf()
            requireActivity().onBackPressed()
        }
        viewBinding.topAppBar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.done -> {
                    lists.tagFilters = checkedTags
                    lists.filterGrade = if(filerGradeButton.isChecked) -1 else 1
                    requireActivity().onBackPressed()
                    true
                }
                else -> false
            }
        }

        return viewBinding.root
    }

    /***
     * Saving the state for rotation
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("SORTING_STATE", viewBinding.filterRatingButton.isChecked)
        outState.putStringArrayList("TAGS", checkedTags)
    }

    /***
     * Restoring the state after rotation
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            viewBinding.filterRatingButton.isChecked = savedInstanceState.getBoolean("SORTING_STATE")
            checkedTags = savedInstanceState.getStringArrayList("TAGS") ?: arrayListOf()
        }
    }
}