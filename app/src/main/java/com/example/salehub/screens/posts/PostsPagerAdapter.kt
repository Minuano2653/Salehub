package com.example.salehub.screens.posts

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.salehub.screens.home.HomeFragment

class PostsPagerAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        if (fragment is HomeFragment) {
            return PostsFragment.newInstance(position + 2)
        }
        return PostsFragment.newInstance(position)
    }

    override fun getItemCount() = 2
}