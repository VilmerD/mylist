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

    private val lists: ListsViewModel by activityViewModels()
    private val args: AddCardFragmentArgs by navArgs()
    private var position = -1

    private lateinit var card: CardDataModel
    private var grade: Int
        get() {
            return when (viewBinding.gradeRadioGroup.checkedRadioButtonId) {
                R.id.badGrade -> 2
                R.id.okGrade -> 3
                R.id.goodGrade -> 4
                R.id.bestGrade -> 5
                else -> 3
            }
        }
        set(newGrade) {
            when (newGrade) {
                2 -> viewBinding.badGrade.isChecked = true
                3 -> viewBinding.okGrade.isChecked = true
                4 -> viewBinding.goodGrade.isChecked = true
                5 -> viewBinding.bestGrade.isChecked = true
                else -> viewBinding.okGrade.isChecked = true
            }
        }

    private var selectedTags: ArrayList<String>
        get() {
            val selectedTags: ArrayList<String> = arrayListOf()
            viewBinding.tagChipGroup.children
                .toList()
                .filter { (it as Chip).isChecked }
                .forEach { selectedTags.add((it as Chip).text.toString()) }
            return selectedTags
        }
        set(newTags) {
            viewBinding.tagChipGroup.children.forEach { chip ->
                val name = (chip as Chip).text
                if (newTags.contains(name)) chip.isChecked = true
            }
        }

    /***
     * A launcher for the photo activity.
     */
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            card.file = data?.getSerializableExtra(PhotoActivity.FILE_EXTRA) as File
            val img = BitmapFactory.decodeFile(card.file!!.absolutePath)
            viewBinding.imageView.setImageBitmap(img)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = args.position
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_card, container, false)

        // Setting up the card
        if (position == -1) {
            card = CardDataModel(null, DEFAULT_CARD_NAME, arrayListOf())
        } else {
            card = lists.getCardsFiltered()[position]
            viewBinding.cardNameEditText.editText?.setText(card.name)
        }
        viewBinding.imageView.setImageBitmap(
            BitmapFactory.decodeFile(card.file?.absolutePath) ?: null
        )
        grade = card.grade
        viewBinding.photoButton.setOnClickListener { takePicture() }

        lists.list.tags.forEach { addChip(it) }
        selectedTags = card.tags

        // Setting up the top app bar
        viewBinding.topAppBar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        viewBinding.topAppBar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.done -> { addCard(); true }
                R.id.delete -> { deleteCard(); true }
                else -> false
            }
        }
        return viewBinding.root
    }

    /***
     * Adds the card to the list and returns
     */
    private fun addCard() {
        card.name = viewBinding.cardNameEditText.editText?.text.toString()
        card.tags = selectedTags
        card.grade = grade
        if (position == -1) lists.list.cards.add(card)
        requireActivity().onBackPressed()
    }

    /***
     * Deletes the card and returns
     */
    private fun deleteCard() {
        lists.safeDeleteCard(position)
        requireActivity().onBackPressed()
    }

    /***
     * Helper function for taking a picture.
     */
    private fun takePicture() {
        val i = Intent(requireContext(), PhotoActivity::class.java)
        if (card.file == null) {
            val format = SimpleDateFormat(FILENAME_FORMAT, Locale.ENGLISH)
                .format(System.currentTimeMillis()) + ".jpg"
            card.file = File(requireContext().filesDir, format)
        }
        i.putExtra(PhotoActivity.FILE_EXTRA, card.file)
        startForResult.launch(i)
    }

    /***
     * Helper function for adding chips to the view
     */
    private fun addChip(name: String): Chip {
        val chip = Chip(requireContext())
        chip.text = name

        chip.isClickable = true
        chip.isCheckable = true

        val index = viewBinding.tagChipGroup.childCount
        viewBinding.tagChipGroup.addView(chip as View, index)
        return chip
    }

    /***
     * Saves the state
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("NAME", viewBinding.cardNameEditText.editText?.text.toString())
        outState.putStringArrayList("TAGS", selectedTags)
        outState.putInt("GRADE", grade)
    }

    /***
     * Restores the state
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            viewBinding.cardNameEditText.editText?.setText(savedInstanceState.getString("NAME"))
            selectedTags = savedInstanceState.getStringArrayList("TAGS") ?: arrayListOf()
            grade = savedInstanceState.getInt("GRADE")
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val DEFAULT_CARD_NAME = "_DEFAULT_CARD_NAME"
    }
}