package com.feragusper.tmdblite.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feragusper.tmdblite.catalog.model.TVShow
import com.feragusper.tmdblite.catalog.ui.PopularTVShowsFragment
import com.feragusper.tmdblite.databinding.ListItemSubscribedTvShowBinding

/**
 * Adapter for the [RecyclerView] in [PopularTVShowsFragment].
 */
class SubscribedTVShowListAdapter(private val onItemClick: (TVShow) -> Unit) : ListAdapter<TVShow, SubscribedTVShowListAdapter.TVShowViewHolder>(SubscribedTVShowDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVShowViewHolder {
        return TVShowViewHolder(
            ListItemSubscribedTvShowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: TVShowViewHolder, position: Int) {
        val tvShow = getItem(position)
        if (tvShow != null) {
            holder.bind(tvShow)
        }
    }

    class TVShowViewHolder(
        private val binding: ListItemSubscribedTvShowBinding,
        private val onItemClick: (TVShow) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.tvShow?.let { tvShow ->
                    onItemClick(tvShow)
                }
            }
        }

        fun bind(item: TVShow) {
            binding.apply {
                tvShow = item
                executePendingBindings()
            }
        }
    }

}

object SubscribedTVShowDiffCallback : DiffUtil.ItemCallback<TVShow>() {
    override fun areItemsTheSame(oldItem: TVShow, newItem: TVShow): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TVShow, newItem: TVShow): Boolean {
        return oldItem.id == newItem.id
    }
}
