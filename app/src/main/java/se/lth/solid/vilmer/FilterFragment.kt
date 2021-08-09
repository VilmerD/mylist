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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)

        val chipGroup = viewBinding.filterChipGroup
        val tags = lists.getTags()
        chipGroup.removeAllViews()
        tags.forEach { s: String ->
            val chip = Chip(context)
            chip.text = s
            chip.isClickable = true
            chip.isCheckable = true

            if (lists.filters != null && lists.filters!!.contains(s)) chip.isChecked = true

            chipGroup.addView(chip as View)
        }

        viewBinding.topAppBar.setNavigationOnClickListener {
            lists.filters = null
            requireActivity().onBackPressed()
        }

        viewBinding.topAppBar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.done -> {
                    val filters: ArrayList<String> = arrayListOf()
                    chipGroup.children
                        .toList()
                        .filter { (it as Chip).isChecked }
                        .forEach { filters.add((it as Chip).text.toString()) }
                    lists.filters = filters
                    requireActivity().onBackPressed()
                    true
                }
                else -> false
            }
        }

        return viewBinding.root
    }
}