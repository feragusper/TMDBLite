package com.feragusper.tmdblite.catalog.di

import com.feragusper.tmdblite.catalog.data.api.CatalogService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideCatalogService(): CatalogService {
        return CatalogService.create()
    }
}
