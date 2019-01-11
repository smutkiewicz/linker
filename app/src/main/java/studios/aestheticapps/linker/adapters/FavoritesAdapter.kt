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
import studios.aestheticapps.linker.validation.LinkMetadataFormatter
import java.util.*

class FavoritesAdapter(private val callback: OnMyAdapterItemClickListener)
    : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>(), MyAdapter
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
        val model = elements[position]
        holder.apply {
            this.model = model
            id = model.id
            domainTv.text = model.domain
            titleTv.text = model.title

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
        var domainTv: TextView = itemView.findViewById(R.id.domainTv)
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val miniatureIv: ImageView = itemView.findViewById(R.id.miniatureIv)
        val copyIb: ImageButton = itemView.findViewById(R.id.copyIb)
        val shareIb: ImageButton = itemView.findViewById(R.id.shareIb)

        init
        {
            itemView.setOnClickListener{
                callback.onItemClicked(model)
            }

            itemView.setOnLongClickListener{
                callback.onItemLongClicked(model)
                true
            }

            shareIb.setOnClickListener{
                callback.onShare(model)
            }

            copyIb.setOnClickListener {
                callback.onCopy(model.url)
            }
        }
    }
}