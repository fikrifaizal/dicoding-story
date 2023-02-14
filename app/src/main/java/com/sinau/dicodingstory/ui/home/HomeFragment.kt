package com.sinau.dicodingstory.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinau.dicodingstory.data.remote.response.ListStoryItem
import com.sinau.dicodingstory.databinding.FragmentHomeBinding
import com.sinau.dicodingstory.ui.main.MainActivity
import com.sinau.dicodingstory.ui.upload.UploadActivity
import com.sinau.dicodingstory.utils.animateLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val homeViewModel: HomeViewModel by viewModels()

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
        showLoading(true)
        onErrorData(false)

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                homeViewModel.getStories(token).collect { result ->
                    result.onSuccess {
                        showLoading(false)
                        showRecyclerView()
                        updateRecyclerView(it.listStory)
                    }

                    result.onFailure {
                        showLoading(false)
                        onErrorData(true)
                    }
                }
            }
        }
    }

    private fun showRecyclerView() {
        binding?.rvStories?.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
    }

    private fun updateRecyclerView(listStory: List<ListStoryItem>) {
        binding?.rvStories?.apply {
            val firstState = layoutManager?.onSaveInstanceState()
            adapter = StoryAdapter(listStory)
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