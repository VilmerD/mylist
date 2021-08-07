package se.lth.solid.vilmer

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import se.lth.solid.vilmer.databinding.FragmentAddCardBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddCardFragment : Fragment() {

    private lateinit var viewBinding: FragmentAddCardBinding

    private val lists : ListsViewModel by activityViewModels()
    private val args: AddCardFragmentArgs by navArgs()
    private var position = -1

    private lateinit var card: CardDataModel

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val img = BitmapFactory.decodeFile(card.file!!.absolutePath)
            viewBinding.imageView.setImageBitmap(img)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_card, container, false)
        position = args.position

        if (position == -1) {
            card = CardDataModel(null, DEFAULT_CARD_NAME, arrayListOf())
        } else {
            card = lists.getCardsFiltered()[position]
            viewBinding.cardNameEditText.editText?.setText(card.name)
        }
        val img = BitmapFactory.decodeFile(card.file?.absolutePath) ?: null
        viewBinding.imageView.setImageBitmap(img)

        viewBinding.photoButton.setOnClickListener {
            takePicture()
        }

        lists.getTags().forEach { addChip(it) }
        viewBinding.tagChipGroup.children.forEach { view ->
            val chip = view as Chip
            card.tags.forEach { s ->
                if (chip.text == s) chip.isChecked = true
            }
        }

        viewBinding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewBinding.topAppBar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.done -> {
                    card.name = viewBinding.cardNameEditText.editText?.text.toString()
                    card.tags = getTags()
                    if (position == -1) {
                        lists.addCard(card)
                    }
                    requireActivity().onBackPressed()
                    true
                }
                R.id.delete -> {
                    lists.safeDeleteCard(position)
                    requireActivity().onBackPressed()
                    true
                }
                else -> false
            }
        }
        return viewBinding.root
    }

    private fun takePicture() {
        val i = Intent(requireContext(), PhotoActivity::class.java)
        if (card.file == null) {
            card.file = File(
                requireContext().filesDir,
                SimpleDateFormat(
                    FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg"
            )
        }

        i.putExtra(PhotoActivity.FILE_EXTRA, card.file)

        startForResult.launch(i)
    }

    private fun addChip(name: String) {
        val chip = Chip(requireContext())
        chip.text = name

        chip.isClickable = true
        chip.isCheckable = true

        val index = viewBinding.tagChipGroup.childCount - 1
        viewBinding.tagChipGroup.addView(chip as View, index)
    }

    private fun getTags() : ArrayList<String> {
        val chipGroup = viewBinding.tagChipGroup
        val selectedTags : ArrayList<String> = arrayListOf()
        chipGroup.children
            .toList()
            .filter { (it as Chip).isChecked }
            .forEach { selectedTags.add((it as Chip).text.toString()) }
        return selectedTags
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val DEFAULT_CARD_NAME = "_DEFAULT_CARD_NAME"
    }
}