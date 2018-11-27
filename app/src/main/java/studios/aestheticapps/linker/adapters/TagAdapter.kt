package studios.aestheticapps.linker.adapters

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import studios.aestheticapps.linker.R
import java.util.*

class TagAdapter(val isMenuEnabled: Boolean = false)
    : RecyclerView.Adapter<TagAdapter.ViewHolder>(), View.OnCreateContextMenuListener
{
    var elements: MutableList<String> = LinkedList()
    var position: Int = 0

    override fun getItemCount() = elements.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.tag_item,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val tag = elements[position]
        holder.apply {
            titleTextView.text = "#$tag"
        }

        if (isMenuEnabled)
        {
            holder.itemView.setOnCreateContextMenuListener(this)
            holder.itemView.setOnLongClickListener {
                this.position = position
                false
            }
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, p1: View?, p2: ContextMenu.ContextMenuInfo?)
    {
        val delete = menu!!.add(Menu.NONE, DELETE_TAG, DELETE_TAG, "Delete")
        delete.setOnMenuItemClickListener(onContextMenu)
    }

    fun removeItem(position: Int)
    {
        elements.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(tag: String)
    {
        elements.add(tag)
        notifyDataSetChanged()
    }

    private val onContextMenu = MenuItem.OnMenuItemClickListener { item ->
        when (item.itemId)
        {
            DELETE_TAG -> removeItem(position)
        }
        true
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val titleTextView: TextView = itemView.findViewById<TextView>(R.id.tagTitle)
    }

    private companion object
    {
        const val DELETE_TAG = 0
    }
}