package com.feragusper.tmdblite.catalog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.feragusper.tmdblite.catalog.data.TVShowRepository
import com.feragusper.tmdblite.catalog.model.TVShow
import com.feragusper.tmdblite.catalog.state.Resource
import com.feragusper.tmdblite.catalog.ui.SearchTVShowsFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for [SearchTVShowsFragment].
 */
@HiltViewModel
class SearchTVShowsViewModel @Inject internal constructor(
    private val tvShowRepository: TVShowRepository
) : ViewModel() {

    private val _searchTVShowListFlow = Channel<Resource<PagingData<TVShow>>>(Channel.BUFFERED)
    val searchTVShowListFlow = _searchTVShowListFlow.receiveAsFlow()

    fun searchTVShows(query: String) {
        viewModelScope.launch {
            _searchTVShowListFlow.send(Resource.loading())
            tvShowRepository.searchTVShows(query)
                .cachedIn(viewModelScope)
                .catch { e ->
                    _searchTVShowListFlow.send(Resource.error(e.toString()))
                }
                .collect { tvShowPagingData ->
                    _searchTVShowListFlow.send(Resource.success(tvShowPagingData))
                }
        }
    }
}
