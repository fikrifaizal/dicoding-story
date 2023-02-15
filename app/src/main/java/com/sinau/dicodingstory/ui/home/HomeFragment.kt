package com.sinau.dicodingstory.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinau.dicodingstory.adapter.LoadingStateAdapter
import com.sinau.dicodingstory.adapter.StoryAdapter
import com.sinau.dicodingstory.data.local.entity.StoryEntity
import com.sinau.dicodingstory.databinding.FragmentHomeBinding
import com.sinau.dicodingstory.ui.main.MainActivity
import com.sinau.dicodingstory.ui.upload.UploadActivity
import com.sinau.dicodingstory.utils.animateLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val homeViewModel: HomeViewModel by viewModels()

    private var storyAdapter = StoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = requireActivity().intent.getStringExtra(MainActivity.EXTRA_TOKEN).toString()

        showRecyclerView()
        getStories(token)

        binding?.fabUpload?.setOnClickListener {
            val intent = Intent(requireContext(), UploadActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getStories(token: String) {
        /**
        * remove lifecycleScope to avoid refreshing data on pagination after page 1
         * removed lifecycle : lifecycleScope and repeatOnLifecycle(Lifecycle.State.RESUMED)
        */
        homeViewModel.getStories(token).observe(viewLifecycleOwner) {
            updateRecyclerView(it)
        }
    }

    private fun showRecyclerView() {
        binding?.rvStories?.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
            setHasFixedSize(true)
        }
    }

    private fun updateRecyclerView(listStory: PagingData<StoryEntity>) {
        binding?.rvStories?.apply {
            val firstState = layoutManager?.onSaveInstanceState()
            storyAdapter.submitData(lifecycle, listStory)
            layoutManager?.onRestoreInstanceState(firstState)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            loadingLayout.animateLoading(isLoading)
            fabUpload.isEnabled = !isLoading
        }
    }

    private fun onErrorData(isError: Boolean) {
        binding?.apply {
            rvStories.alpha = if (isError) 0F else 1F
            tvError.animateLoading(isError)
        }
    }
}