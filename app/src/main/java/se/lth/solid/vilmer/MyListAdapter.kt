package se.lth.solid.vilmer

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.lang.IllegalStateException

class MyListAdapter(
    var lists: ListsViewModel
) : RecyclerView.Adapter<MyListAdapter.ListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        val item =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false)

        return ListHolder(item)
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        val context = holder.itemView.context
        holder.nameView.isClickable = true
        holder.nameView.text = lists.myLists[position].name
        holder.tags = lists.myLists[position].tags

        // onClick is used to select the current list as displaying list
        holder.nameView.setOnClickListener {
            val pos = holder.adapterPosition
            val action =
                ManageListsFragmentDirections.actionManageListsFragmentToAddListFragment(pos)
            it.findNavController().navigate(action)
        }

        holder.editButton.setOnClickListener {
            lists.displaying = holder.adapterPosition
            (context as Activity).onBackPressed()
        }

        holder.tagChipGroup.removeAllViews()
        holder.tags.forEach {
            val chip = Chip(holder.itemView.context)
            chip.text = it
            chip.isClickable = false
            chip.isCheckable = false

            holder.tagChipGroup.addView(chip as View)
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    class ListHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var nameView: TextView = itemView.findViewById(R.id.list_name)
        var editButton: ImageButton = itemView.findViewById(R.id.editListButton)
        var tagChipGroup: ChipGroup = itemView.findViewById(R.id.tagChipGroup)
        lateinit var tags: ArrayList<String>
    }
}