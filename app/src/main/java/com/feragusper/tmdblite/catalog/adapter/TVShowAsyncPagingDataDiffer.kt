package com.feragusper.tmdblite.catalog.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.feragusper.tmdblite.R
import com.feragusper.tmdblite.catalog.model.TVShow
import com.feragusper.tmdblite.catalog.ui.PopularTVShowsFragment
import com.feragusper.tmdblite.databinding.ListItemPopularTvShowBinding
import com.feragusper.tmdblite.databinding.ListItemSubscribedTvShowListBinding
import com.feragusper.tmdblite.databinding.ListItemTvShowListLabelBinding


/**
 * Adapter for the [RecyclerView] in [PopularTVShowsFragment].
 */

class TVShowAsyncPagingDataDiffer(private val onItemClick: (TVShow) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val adapterCallback = AdapterListUpdateCallback(this)

    private val differ = AsyncPagingDataDiffer(
        TVShowAsyncPagingDataDiffCallback(),
        object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                adapterCallback.onInserted(position + 1, count);
            }

            override fun onRemoved(position: Int, count: Int) {
                adapterCallback.onRemoved(position + 1, count);
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                adapterCallback.onMoved(fromPosition + 1, toPosition + 1);
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                adapterCallback.onChanged(position + 1, count, payload);
            }

        }
    )

    companion object {
        const val TYPE_SUBSCRIBED_LABEL = 0
        const val TYPE_SUBSCRIBED = 1
        const val TYPE_POPULAR_LABEL = 2
        const val TYPE_POPULAR = 3
        const val EXTRA_ROWS = 3
    }

    private var subscribedTVShowList: List<TVShow>? = null
    private var subscribedTVShowListAdapter: SubscribedTVShowListAdapter? = null

    override fun getItemCount(): Int {
        return differ.itemCount + EXTRA_ROWS
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_SUBSCRIBED_LABEL
            1 -> TYPE_SUBSCRIBED
            2 -> TYPE_POPULAR_LABEL
            else -> TYPE_POPULAR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SUBSCRIBED_LABEL -> SubscribedTvShowsLabelViewHolder(
                ListItemTvShowListLabelBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
            )
            TYPE_POPULAR_LABEL -> PopularLabelViewHolder(
                ListItemTvShowListLabelBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
            )
            TYPE_POPULAR -> TVShowViewHolder(
                ListItemPopularTvShowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemClick
            )
            TYPE_SUBSCRIBED -> {
                subscribedTVShowListAdapter = SubscribedTVShowListAdapter(onItemClick)
                subscribedTVShowListAdapter?.submitList(subscribedTVShowList)
                SubscribedTVShowViewHolder(
                    ListItemSubscribedTvShowListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    subscribedTVShowListAdapter
                )
            }
            else -> throw Exception("Type Not Supported")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TVShowViewHolder -> {
                val tvShow = differ.getItem(position - EXTRA_ROWS)
                if (tvShow != null) {
                    holder.bind(tvShow)
                }
            }
            is SubscribedTVShowViewHolder -> {
                holder.bind(subscribedTVShowList)
            }
            is PopularLabelViewHolder -> holder.bind(holder.itemView.context.getString(R.string.popular))
            is SubscribedTvShowsLabelViewHolder -> holder.bind(holder.itemView.context.getString(R.string.followed_tv_shows), subscribedTVShowList.isNullOrEmpty())
        }
    }

    fun submitData(subscribedTVShowList: List<TVShow>) {
        this.subscribedTVShowList = subscribedTVShowList
        subscribedTVShowListAdapter?.submitList(subscribedTVShowList)
    }

    suspend fun submitData(tvShowPagingData: PagingData<TVShow>) {
        differ.submitData(tvShowPagingData)
    }

    class TVShowViewHolder(
        private val binding: ListItemPopularTvShowBinding,
        private val onItemClick: (TVShow) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.tvShow?.let { tvShow ->
                    onItemClick(tvShow)
                }
            }
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(colorMatrix)
            binding.tvShowImage.colorFilter = filter
        }

        fun bind(item: TVShow) {
            binding.apply {
                tvShow = item
                executePendingBindings()
            }
        }
    }

    class SubscribedTVShowViewHolder(
        private val binding: ListItemSubscribedTvShowListBinding,
        private val adapter: SubscribedTVShowListAdapter?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                tvShowList.adapter = adapter
            }
        }

        fun bind(items: List<TVShow>?) {
            binding.apply {
                tvShowList.isVisible = items.isNullOrEmpty().not()
                items?.let { itemsNotNull ->
                    adapter?.submitList(itemsNotNull)
                }
            }
        }
    }

    abstract class LabelViewHolder(
        private val binding: ListItemTvShowListLabelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String, hide: Boolean = false) {
            binding.apply {
                label.isVisible = hide.not()
                label.text = title
            }
        }
    }

    class PopularLabelViewHolder(
        binding: ListItemTvShowListLabelBinding
    ) : LabelViewHolder(binding)

    class SubscribedTvShowsLabelViewHolder(
        binding: ListItemTvShowListLabelBinding
    ) : LabelViewHolder(binding)

}

private class TVShowAsyncPagingDataDiffCallback : DiffUtil.ItemCallback<TVShow>() {
    override fun areItemsTheSame(oldItem: TVShow, newItem: TVShow): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TVShow, newItem: TVShow): Boolean {
        return oldItem == newItem
    }
}
