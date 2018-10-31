package studios.aestheticapps.linker.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.R
import java.util.*

class RecentLinksAdapter(private val recentLinks: LinkedList<Link>) : RecyclerView.Adapter<RecentLinksAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private var rowID: Long = 0
        val titleTextView: TextView = itemView.findViewById(R.id.titleTv)

        private fun setRowID(rowID: Long)
        {
            this.rowID = rowID
        }
    }

    override fun getItemCount() = recentLinks.size

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
        val link = recentLinks[position]
        holder.titleTextView.text = link.title
    }
}