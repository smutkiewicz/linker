package studios.aestheticapps.linker.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.model.Link
import java.util.*

class RecentLinkAdapter(private val callback: OnMyAdapterItemClickListener)
    : RecyclerView.Adapter<RecentLinkAdapter.ViewHolder>(), MyAdapter
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
            this.link = link
            id = link.id
            titleTextView.text = link.title
            domainTextView.text = link.domain
        }
    }

    private fun bringItemToFront(oldIndex: Int) = notifyItemMoved(oldIndex, 0)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        lateinit var link: Link
        var id: Int = 0
        val titleTextView: TextView = itemView.findViewById<TextView>(R.id.titleTv)
        val domainTextView: TextView = itemView.findViewById<TextView>(R.id.domainTv)

        init
        {
            itemView.setOnClickListener{
                callback.onShare(link)
                bringItemToFront(this.adapterPosition)
            }

            itemView.setOnLongClickListener{
                callback.onItemLongClicked(link)
                true
            }
        }
    }
}