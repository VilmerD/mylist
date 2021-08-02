package se.lth.solid.vilmer

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView

class MyListAdapter(
    var lists: Array<String>,
    var context: Context
) :
    RecyclerView.Adapter<MyListAdapter.ListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        val item =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false)

        return ListHolder(item, (context as ManageListsActivity).launcher)
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        if (position == lists.size) {
            holder.nameView.text = context.resources.getString(R.string.new_list_text)
        } else {
            holder.nameView.isClickable = true
            holder.nameView.text = lists[position]
            holder.nameView.setOnClickListener {
                val data = Intent()
                    .putExtra(MainActivity.DISPLAY_LIST_EXTRA, holder.adapterPosition)
                    .putExtra(MainActivity.NEW_LISTS_EXTRA, lists)
                (context as Activity).setResult(RESULT_OK, data)
                (context as Activity).finish()
            }
        }

        holder.nameView.setOnLongClickListener {
            (context as ManageListsActivity).toggleMenu()
            true
        }
        holder.editButton.setOnClickListener(holder)
    }

    override fun getItemCount(): Int {
        return lists.size + 1
    }

    class ListHolder(itemView: View, var launcher: ActivityResultLauncher<Intent>) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var nameView: TextView = itemView.findViewById(R.id.list_name)
        var editButton: Button = itemView.findViewById(R.id.editListButton)

        override fun onClick(v: View) {
            val data = Intent(v.context, AddListActivity::class.java)
                .putExtra(AddListActivity.NAME_EXTRA, nameView.text)
                .putExtra(AddListActivity.LIST_SELECTED_EXTRA, adapterPosition)
            launcher.launch(data)
        }
    }
}


