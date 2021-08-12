package com.feragusper.tmdblite.catalog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feragusper.tmdblite.catalog.data.TVShowRepository
import com.feragusper.tmdblite.catalog.model.TVShow
import com.feragusper.tmdblite.catalog.model.TVShowId
import com.feragusper.tmdblite.catalog.state.Resource
import com.feragusper.tmdblite.catalog.ui.TVShowDetailFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for [TVShowDetailFragment].
 */
@HiltViewModel
class TVShowDetailViewModel @Inject internal constructor(
    private val tvShowRepository: TVShowRepository
) : ViewModel() {

    private val _fetchIsSubscribedFlow = Channel<Resource<Boolean>>(Channel.BUFFERED)
    val fetchIsSubscribedFlow = _fetchIsSubscribedFlow.receiveAsFlow()

    fun subscribe(tvShow: TVShow) {
        viewModelScope.launch {
            tvShowRepository.subscribe(tvShow)
        }
    }

    fun fetchIsSubscribed(tvShowId: TVShowId) {
        viewModelScope.launch {
            tvShowRepository.fetchIsSubscribed(tvShowId)
                .catch { e ->
                    _fetchIsSubscribedFlow.send(Resource.error(e.toString()))
                }
                .collect { isSubscribed ->
                    _fetchIsSubscribedFlow.send(Resource.success(isSubscribed))
                }
        }
    }

    fun unsubscribe(tvShow: TVShow) {
        viewModelScope.launch {
            tvShowRepository.unsubscribe(tvShow)
        }
    }

}
