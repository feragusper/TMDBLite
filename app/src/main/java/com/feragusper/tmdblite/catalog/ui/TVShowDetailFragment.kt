package com.feragusper.tmdblite.catalog.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.feragusper.tmdblite.R
import com.feragusper.tmdblite.catalog.state.Resource
import com.feragusper.tmdblite.catalog.viewmodel.TVShowDetailViewModel
import com.feragusper.tmdblite.databinding.FragmentTvShowDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class TVShowDetailFragment : Fragment() {

    private val viewModel: TVShowDetailViewModel by viewModels()

    private val args: TVShowDetailFragmentArgs by navArgs()

    private val mTVShow by lazy { args.tvShow }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentTvShowDetailBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.apply {
            tvShow = mTVShow
        }

        context?.let { contextNotNull ->
            Glide.with(contextNotNull)
                .asBitmap()
                .load(mTVShow.backDropUrl)
                .transition(BitmapTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                        resource?.let { bitmapNotNull ->
                            Palette.from(bitmapNotNull).generate { palette ->
                                palette?.vibrantSwatch?.let { swatch ->
                                    binding.tvShowImageOverlay.setBackgroundColor(swatch.rgb)
                                    binding.tvShowName.setTextColor(swatch.bodyTextColor)
                                    binding.tvShowYear.setTextColor(swatch.bodyTextColor)
                                    binding.overview.setTextColor(swatch.bodyTextColor)
                                    binding.overviewLabel.setTextColor(swatch.titleTextColor)
                                }
                            }
                        }
                        return false
                    }


                })
                .into(binding.tvShowBackdrop)
        }

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        subscribeUi(binding.subscribe)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchIsSubscribed(tvShowId = mTVShow.id)
    }

    private fun subscribeUi(button: AppCompatButton) {
        lifecycleScope.launchWhenStarted {
            viewModel.fetchIsSubscribedFlow.collectLatest { fetchResource ->
                when (fetchResource.status) {
                    Resource.Status.SUCCESS -> {
                        if (fetchResource.data != true) {
                            button.text = getString(R.string.subscribe)
                            button.setOnClickListener {
                                viewModel.subscribe(mTVShow)
                                button.text = getString(R.string.subscribed)
                            }
                        } else {
                            button.text = getString(R.string.subscribed)
                            button.setOnClickListener {
                                viewModel.unsubscribe(mTVShow)
                                button.text = getString(R.string.subscribe)
                            }
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
