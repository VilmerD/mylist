package se.lth.solid.vilmer

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup

class CardAdapter(
    var cardList: ArrayList<CardDataModel>
) : RecyclerView.Adapter<CardAdapter.CardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val card = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)

        return CardHolder(card)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val card = cardList[position]

        val rawBitmap = BitmapFactory.decodeFile(card.file?.absolutePath) ?: null
        holder.imageView.setImageBitmap(rawBitmap)
        holder.nameView.text = card.name
        holder.itemView.isClickable = true

        holder.itemView.setOnClickListener { view: View ->
            val action =
                MainFragmentDirections.actionMainFragmentToAddCardFragment(holder.adapterPosition)
            view.findNavController().navigate(action)
        }

        holder.gradeView.setImageResource(
            when (card.grade) {
                2 -> R.drawable.ic_bad
                3 -> R.drawable.ic_ok
                4 -> R.drawable.ic_good
                5 -> R.drawable.ic_best
                else -> R.drawable.ic_ok
            }
        )
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var nameView: TextView = itemView.findViewById(R.id.nameTextView)
        var gradeView: ImageView = itemView.findViewById(R.id.gradeView)
    }
}