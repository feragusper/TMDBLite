package com.feragusper.tmdblite.catalog.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.feragusper.tmdblite.catalog.adapter.TVShowPagingDataAdapter
import com.feragusper.tmdblite.catalog.state.Resource
import com.feragusper.tmdblite.catalog.viewmodel.SearchTVShowsViewModel
import com.feragusper.tmdblite.databinding.FragmentSearchTvShowsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchTVShowsFragment : Fragment() {

    private val viewModel: SearchTVShowsViewModel by viewModels()
    private val adapter = TVShowPagingDataAdapter { tvShow ->
        findNavController().navigate(PopularTVShowsFragmentDirections.actionGlobalToTvShowDetailFragmentDest(tvShow))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchTvShowsBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.tvShowList.adapter = adapter
        binding.tvShowList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        subscribeUi(adapter)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { queryNotNull ->
                    viewModel.searchTVShows(queryNotNull)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Returning true as this was handled here (no-op). We don't want to filter results on query text changes.
                return true
            }

        })

        return binding.root
    }

    private fun subscribeUi(adapter: TVShowPagingDataAdapter) {
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.Error -> Toast.makeText(context, (loadStates.refresh as LoadState.Error).error.message, Toast.LENGTH_LONG).show()
                    is LoadState.NotLoading -> {

                    }
                    LoadState.Loading -> {

                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.searchTVShowListFlow.collectLatest { fetchResource ->
                when (fetchResource.status) {
                    Resource.Status.SUCCESS -> {
                        fetchResource.data?.let { tvShowPagingData ->
                            adapter.submitData(tvShowPagingData)
                        }
                    }
                    Resource.Status.ERROR -> {
                        Toast.makeText(context, fetchResource.message, Toast.LENGTH_LONG).show()
                    }
                    Resource.Status.LOADING -> {

                    }
                    Resource.Status.FAILURE -> {
                        Toast.makeText(context, fetchResource.message, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }

}
