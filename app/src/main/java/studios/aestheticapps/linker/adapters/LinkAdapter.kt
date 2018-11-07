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

class LinkAdapter(private val listener: OnLibraryItemClickListener) : RecyclerView.Adapter<LinkAdapter.ViewHolder>(), MyAdapter
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
            R.layout.list_item,
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
            domainTv.text = link.domain
            changeFavourite(link.isFavorite)
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
        val domainTv: TextView = itemView.findViewById(R.id.domainTv)
        val isFavouriteIb: ImageButton = itemView.findViewById(R.id.favouriteIb)
        val shareIb: ImageButton = itemView.findViewById(R.id.shareIb)

        init
        {
            itemView.setOnClickListener{
                listener.onItemClicked(link)
            }

            isFavouriteIb.setOnClickListener{
                val newValue = !link.isFavorite
                changeFavourite(newValue)
                listener.onFavourite(link)
            }

            shareIb.setOnClickListener{
                listener.onShare(link)
            }
        }

        fun changeFavourite(newValue: Boolean)
        {
            val view = isFavouriteIb.rootView
            val imageDrawable = when
            {
                newValue -> view.resources.getDrawable(R.drawable.ic_starred)
                else -> view.resources.getDrawable(R.drawable.ic_unstarred)
            }

            isFavouriteIb.setImageDrawable(imageDrawable)
        }
    }

    interface OnLibraryItemClickListener
    {
        fun onItemClicked(link: Link)
        fun onFavourite(link: Link)
        fun onShare(link: Link)
    }
}