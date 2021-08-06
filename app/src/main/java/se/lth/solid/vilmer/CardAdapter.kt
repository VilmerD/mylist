package se.lth.solid.vilmer

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CardAdapter(
    var cardList: CardList,
    var launcher: ActivityResultLauncher<Intent>,
    val context: Context
) : RecyclerView.Adapter<CardAdapter.CardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val card = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)

        return CardHolder(card)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val card = cardList.cards[position]

        val rawBitmap = BitmapFactory.decodeFile(card.file?.absolutePath) ?: null
        holder.imageView.setImageBitmap(rawBitmap)
        holder.nameView.text = card.name

        holder.itemView.isClickable = true

        holder.itemView.setOnClickListener {
            val data = Intent(it.context, AddCardActivity::class.java)
                .putExtra(AddCardActivity.CARD_EXTRA, card)
                .putExtra(AddCardActivity.POSITION_EXTRA, holder.adapterPosition)
                .putExtra(AddCardActivity.TAG_CHOICES_EXTRA, cardList.tags)
            launcher.launch(data)
        }

        val chipGroup = holder.chipGroup
        chipGroup.removeAllViews()
        card.tags.forEach { s ->
            val chip = Chip(context)
            chip.text = s

            chip.isCheckable = false
            chip.isClickable = false
            chipGroup.addView(chip as View)
        }
    }

    override fun getItemCount(): Int {
        return cardList.cards.size
    }

    class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var nameView: TextView = itemView.findViewById(R.id.nameTextView)
        var chipGroup: ChipGroup = itemView.findViewById(R.id.tagChipGroup)
    }
}