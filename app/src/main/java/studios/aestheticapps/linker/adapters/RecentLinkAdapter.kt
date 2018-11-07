package studios.aestheticapps.linker.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.model.Link
import java.util.*

class RecentLinkAdapter : RecyclerView.Adapter<RecentLinkAdapter.ViewHolder>(), MyAdapter
{
    override var elements: MutableList<Link> = LinkedList()
        set(value)
        {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = elements.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recent_list_item,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val link = elements[position]
        holder.apply {
            id = link.id
            titleTextView.text = link.title
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var id: Int = 0
        val titleTextView: TextView = itemView.findViewById<TextView>(R.id.titleTv)
    }
}