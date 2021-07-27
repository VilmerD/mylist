package se.lth.solid.vilmer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import se.lth.solid.vilmer.databinding.FragmentAddCardBinding


class AddCardFragment : Fragment() {

    private val listViewModel: ListViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentAddCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_card, container, false)

        dataBinding.pressMeButton.setOnClickListener {
            listViewModel.addCard(null, "Name", null)
            activity?.onBackPressed()
        }
        return dataBinding.root
    }
}