package studios.aestheticapps.linker.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.model.Link
import java.util.*

class FavoritesAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>(), MyAdapter
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
            R.layout.fav_list_item,
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
            titleTv.text = link.title
        }
    }

    fun removeItem(position: Int)
    {
        elements.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        lateinit var link: Link

        var id: Int = 0
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val shareIb: ImageButton = itemView.findViewById(R.id.shareIb)

        init
        {
            itemView.setOnClickListener{
                listener.onItemClicked(link)
            }

            itemView.setOnLongClickListener{
                listener.onItemLongClicked(link)
                true
            }

            shareIb.setOnClickListener{
                listener.onShare(link)
            }
        }
    }

    //TODO one interface for all adapters
    interface OnItemClickListener
    {
        fun onItemLongClicked(link: Link)
        fun onItemClicked(link: Link)
        fun onShare(link: Link)
    }
}