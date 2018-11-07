package studios.aestheticapps.linker.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import studios.aestheticapps.linker.R
import java.util.*

class TagAdapter : RecyclerView.Adapter<TagAdapter.ViewHolder>()
{
    var elements: MutableList<String> = LinkedList()

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
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val titleTextView: TextView = itemView.findViewById<TextView>(R.id.tagTitle)
    }
}