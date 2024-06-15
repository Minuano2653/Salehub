package com.example.salehub.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.salehub.databinding.FragmentHomeBinding
import com.example.salehub.screens.posts.PostsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var pagerAdapter: PostsPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupHomePostsViewPager()

        return binding.root
    }

    private fun setupHomePostsViewPager() {
        pagerAdapter = PostsPagerAdapter(this)
        binding.homeViewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.accountTabLayout, binding.homeViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Новые"
                1 -> "Лучшие"
                else -> null
            }
        }.attach()
    }
}