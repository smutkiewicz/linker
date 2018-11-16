package studios.aestheticapps.linker.adapters

import studios.aestheticapps.linker.model.Link

interface OnItemClickListener
{
    fun onItemLongClicked(link: Link)
    fun onItemClicked(link: Link)
    fun onShare(link: Link)
    fun onFavourite(link: Link)
}