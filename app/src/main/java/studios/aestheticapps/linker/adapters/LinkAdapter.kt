package studios.aestheticapps.linker.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.LinkMetadataFormatter
import studios.aestheticapps.linker.utils.DateTimeHelper
import java.util.*

class LinkAdapter(private val callback: OnMyAdapterItemClickListener)
    : RecyclerView.Adapter<LinkAdapter.ViewHolder>(), MyAdapter
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
        val model = elements[position]
        holder.apply {
            this.model = model
            id = model.id
            titleTv.text = model.title
            categoryTv.text = model.category
            domainTv.text = model.domain
            createdTv.text = DateTimeHelper.getMonthAndDay(model.created)
            changeFavourite(model.isFavorite)

            if (LinkMetadataFormatter.hasCompatibleImageUrl(model.imageUrl))
            {
                Picasso.get()
                    .load(model.imageUrl)
                    .into(miniatureIv)
            }
        }
    }

    fun removeItem(position: Int)
    {
        elements.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        lateinit var model: Link

        var id: Int = 0
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val categoryTv: TextView = itemView.findViewById(R.id.categoryTv)
        val domainTv: TextView = itemView.findViewById(R.id.domainTv)
        val miniatureIv: ImageView = itemView.findViewById(R.id.miniatureIv)
        val isFavouriteIb: ImageButton = itemView.findViewById(R.id.favouriteIb)
        val shareIb: ImageButton = itemView.findViewById(R.id.shareIb)
        val createdTv: TextView = itemView.findViewById(R.id.createdTv)

        init
        {
            itemView.setOnClickListener{
                callback.onItemClicked(model)
            }

            itemView.setOnLongClickListener{
                callback.onItemLongClicked(model)
                true
            }

            isFavouriteIb.setOnClickListener{
                val newValue = !model.isFavorite
                changeFavourite(newValue)
                callback.onFavourite(model)
            }

            shareIb.setOnClickListener{
                callback.onShare(model)
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
}