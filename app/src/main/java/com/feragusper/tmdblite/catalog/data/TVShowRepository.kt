package com.feragusper.tmdblite.catalog.data

import androidx.paging.*
import com.feragusper.tmdblite.catalog.data.api.CatalogService
import com.feragusper.tmdblite.catalog.data.api.TVShowListPageable
import com.feragusper.tmdblite.catalog.data.db.TVShowDBEntity
import com.feragusper.tmdblite.catalog.data.db.TVShowDao
import com.feragusper.tmdblite.catalog.model.Genre
import com.feragusper.tmdblite.catalog.model.TVShow
import com.feragusper.tmdblite.catalog.model.TVShowId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val STARTING_PAGE_INDEX = 1
private const val PAGE_SIZE = 10

/**
 * Repository module for handling data operations.
 */
@Singleton
class TVShowRepository @Inject constructor(
    private val catalogAPIDataSource: CatalogService,
    private val tvShowDao: TVShowDao,
) {

    private var genreListCache: List<Genre> = listOf()

    suspend fun fetchPopularTVShows(): Flow<PagingData<TVShow>> {

        fetchGenreListIfNeeded()

        return Pager(
            config = PagingConfig(
                enablePlaceholders = true,
                pageSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                PopularTVShowsPagingSource(
                    genreList = genreListCache,
                    dataSource = catalogAPIDataSource
                )
            }
        ).flow
    }


    suspend fun searchTVShows(query: String): Flow<PagingData<TVShow>> {

        fetchGenreListIfNeeded()

        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                SearchTVShowsPagingSource(
                    genreList = genreListCache,
                    dataSource = catalogAPIDataSource,
                    query = query
                )
            }
        ).flow
    }

    suspend fun subscribe(tvShow: TVShow) {
        return tvShowDao.insert(TVShowDBEntity.from(tvShow))
    }

    suspend fun unsubscribe(tvShow: TVShow) {
        tvShowDao.delete(tvShow.id)
    }

    fun fetchIsSubscribed(tvShowId: TVShowId): Flow<Boolean> {
        return tvShowDao.isSubscribed(tvShowId)
    }

    private suspend fun fetchGenreListIfNeeded() {
        if (genreListCache.isEmpty()) {
            genreListCache = catalogAPIDataSource.getTVShowGenres().toGenreList()
        }
    }

    fun fetchSubscribedTVShows(): Flow<List<TVShow>> {
        return tvShowDao.getTVShows().map { tvShowDBEntityList ->
            tvShowDBEntityList.map { tvShowDBEntity ->
                tvShowDBEntity.toTVShow()
            }
        }
    }

}

abstract class AbstractTVShowsPagingSource(private val genreList: List<Genre>) : PagingSource<Int, TVShow>() {

    final override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TVShow> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = loadResults(page = page)
            LoadResult.Page(
                data = response.toTVShows(genreList),
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (page == response.totalPages) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    abstract suspend fun loadResults(page: Int): TVShowListPageable

    override fun getRefreshKey(state: PagingState<Int, TVShow>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}

class PopularTVShowsPagingSource(
    genreList: List<Genre>,
    private val dataSource: CatalogService
) : AbstractTVShowsPagingSource(genreList) {

    override suspend fun loadResults(page: Int): TVShowListPageable {
        return dataSource.getPopularTVShows(
            page = page
        )
    }
}

class SearchTVShowsPagingSource(
    genreList: List<Genre>,
    private val dataSource: CatalogService,
    private val query: String
) : AbstractTVShowsPagingSource(genreList) {

    override suspend fun loadResults(page: Int): TVShowListPageable {
        return dataSource.searchTVShows(
            page = page,
            query = query
        )
    }
}
