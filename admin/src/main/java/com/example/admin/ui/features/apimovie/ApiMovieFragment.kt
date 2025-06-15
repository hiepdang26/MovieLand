package com.example.admin.ui.features.apimovie

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.admin.MainActivity
import com.example.admin.R
import com.example.admin.databinding.FragmentApiMovieBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.apimovie.nowplaying.NowPlayingFragment
import com.example.admin.ui.features.apimovie.upcoming.UpComingFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApiMovieFragment : BaseFragment<FragmentApiMovieBinding>() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentApiMovieBinding {
        return FragmentApiMovieBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun setupInitialData() {
        val fragments = listOf(
            NowPlayingFragment(),
            UpComingFragment()
        )

        val titles = listOf("Now Playing", "Upcoming")

        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        binding.viewPagerMovies.adapter = adapter

        TabLayoutMediator(binding.tabLayoutMovies, binding.viewPagerMovies) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    override fun setupObserver() {
    }

    override fun setupClickView() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialData()
        setupClickView()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideNavigationBar()
    }
}