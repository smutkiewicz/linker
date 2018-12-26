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

class TagAdapter(private val isMenuEnabled: Boolean = false)
    : RecyclerView.Adapter<TagAdapter.ViewHolder>(), View.OnCreateContextMenuListener
{
    var onTagClickedListener: OnTagClickedListener? = null
    var position: Int = 0

    var elements: MutableList<String> = LinkedList()
        set(value)
        {
            field = value
            notifyDataSetChanged()
        }

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

        holder.itemView.setOnClickListener {
            onTagClickedListener?.onSearchTag(tag)
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

    fun removeItem(tag: String, position: Int)
    {
        elements.removeAt(position)
        notifyItemRemoved(position)
        onTagClickedListener?.onDeleteTag(tag)
    }

    fun addItem(tag: String)
    {
        elements.add(tag)
        notifyDataSetChanged()
        onTagClickedListener?.onAddTag(tag)
    }

    private val onContextMenu = MenuItem.OnMenuItemClickListener { item ->
        when (item.itemId)
        {
            DELETE_TAG -> removeItem(elements[position], position)
        }
        true
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val titleTextView: TextView = itemView.findViewById<TextView>(R.id.tagTitle)
    }

    interface OnTagClickedListener
    {
        fun onSearchTag(tag: String) {}
        fun onDeleteTag(tag: String) {}
        fun onAddTag(tag: String) {}
    }

    private companion object
    {
        const val DELETE_TAG = 0
    }
}