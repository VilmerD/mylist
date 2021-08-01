package se.lth.solid.vilmer

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(var cardList: CardList) : RecyclerView.Adapter<CardAdapter.CardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val card = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)

        return CardHolder(card)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val card = cardList.cards[position]

        val rawBitmap = BitmapFactory.decodeFile(card.file?.absolutePath) ?: null
        holder.imageView.setImageBitmap(rawBitmap)
        holder.nameView.text = card.name
        val counts = card.counts

        holder.iterButton.text = "$counts"
        holder.iterButton.setOnClickListener {
            val counts = card.iterateCounts()
            holder.iterButton.text = "$counts"
        }
    }

    override fun getItemCount(): Int {
        return cardList.cards.size
    }

    class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var nameView: TextView = itemView.findViewById(R.id.nameTextView)
        var iterButton: Button = itemView.findViewById(R.id.iterButton)
    }
}