package com.feragusper.tmdblite.catalog.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.feragusper.tmdblite.R
import com.feragusper.tmdblite.catalog.adapter.TVShowAsyncPagingDataDiffer
import com.feragusper.tmdblite.catalog.state.Resource
import com.feragusper.tmdblite.catalog.viewmodel.PopularTVShowListViewModel
import com.feragusper.tmdblite.databinding.FragmentPopularTvShowsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PopularTVShowsFragment : Fragment() {

    private val viewModel: PopularTVShowListViewModel by viewModels()
    private val adapter = TVShowAsyncPagingDataDiffer { tvShow ->
        findNavController().navigate(PopularTVShowsFragmentDirections.actionGlobalToTvShowDetailFragmentDest(tvShow))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPopularTvShowsBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.tvShowList.adapter = adapter
        subscribeUi(adapter)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_search)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.action_popularTVShowsFragmentDest_to_searchTVShowsFragmentDest)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchSubscribedTVShows()
        viewModel.fetchPopularTVShows()
    }

    private fun subscribeUi(adapter: TVShowAsyncPagingDataDiffer) {
        lifecycleScope.launchWhenStarted {
            viewModel.fetchSubscribedTVShowsFlow.collectLatest { fetchResource ->
                when (fetchResource.status) {
                    Resource.Status.LOADING -> {
                        // TODO Display a loading
                    }
                    Resource.Status.SUCCESS -> {
                        fetchResource.data?.let { subscribedTVShowList ->
                            adapter.submitData(subscribedTVShowList)
                        }
                    }
                    Resource.Status.FAILURE, Resource.Status.ERROR -> {
                        Toast.makeText(context, fetchResource.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.fetchPopularTVShowsFlow.collectLatest { fetchResource ->
                when (fetchResource.status) {
                    Resource.Status.LOADING -> {
                        // TODO Display a loading
                    }
                    Resource.Status.SUCCESS -> {
                        fetchResource.data?.let { tvShowPagingData ->
                            adapter.submitData(tvShowPagingData)
                        }
                    }
                    Resource.Status.FAILURE, Resource.Status.ERROR -> {
                        Toast.makeText(context, fetchResource.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

}
