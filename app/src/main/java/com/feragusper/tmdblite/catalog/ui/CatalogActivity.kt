package com.feragusper.tmdblite.catalog.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.feragusper.tmdblite.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CatalogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

    }

}