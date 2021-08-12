package com.feragusper.tmdblite.catalog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.feragusper.tmdblite.catalog.data.TVShowRepository
import com.feragusper.tmdblite.catalog.model.TVShow
import com.feragusper.tmdblite.catalog.state.Resource
import com.feragusper.tmdblite.catalog.ui.PopularTVShowsFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for [PopularTVShowsFragment].
 */
@HiltViewModel
class PopularTVShowListViewModel @Inject internal constructor(
    private val tvShowRepository: TVShowRepository
) : ViewModel() {

    private val _fetchSubscribedTVShowsFlow = Channel<Resource<List<TVShow>>>(Channel.BUFFERED)
    val fetchSubscribedTVShowsFlow = _fetchSubscribedTVShowsFlow.receiveAsFlow()

    private val _fetchPopularTVShowsFlow = Channel<Resource<PagingData<TVShow>>>(Channel.BUFFERED)
    val fetchPopularTVShowsFlow = _fetchPopularTVShowsFlow.receiveAsFlow()

    fun fetchPopularTVShows() {
        viewModelScope.launch {
            _fetchPopularTVShowsFlow.send(Resource.loading())
            tvShowRepository.fetchPopularTVShows()
                .cachedIn(viewModelScope)
                .catch { e ->
                    _fetchPopularTVShowsFlow.send(Resource.error(e.toString()))
                }
                .collect { tvShowPagingData ->
                    _fetchPopularTVShowsFlow.send(Resource.success(tvShowPagingData))
                }
        }
    }

    fun fetchSubscribedTVShows() {
        viewModelScope.launch {
            _fetchSubscribedTVShowsFlow.send(Resource.loading())
            tvShowRepository.fetchSubscribedTVShows()
                .catch { e ->
                    _fetchSubscribedTVShowsFlow.send(Resource.error(e.toString()))
                }
                .collect { tvShowList ->
                    _fetchSubscribedTVShowsFlow.send(Resource.success(tvShowList))
                }
        }
    }
}
