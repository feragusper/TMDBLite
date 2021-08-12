package com.feragusper.tmdblite.catalog.di

import android.content.Context
import com.feragusper.tmdblite.catalog.data.db.TVShowDao
import com.feragusper.tmdblite.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideTVShowDao(appDatabase: AppDatabase): TVShowDao {
        return appDatabase.tvShowDao()
    }

}
