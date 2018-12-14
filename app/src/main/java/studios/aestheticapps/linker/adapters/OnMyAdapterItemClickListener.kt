package studios.aestheticapps.linker.adapters

import studios.aestheticapps.linker.model.Link

interface OnMyAdapterItemClickListener
{
    fun onItemLongClicked(model: Link)
    fun onItemClicked(model: Link)
    fun onShare(model: Link)
    fun onFavourite(model: Link)

    fun onEdit(model: Link) {} // Not necessary for some Views
    fun onCopy(content: String) {} // Not necessary for some Views
}