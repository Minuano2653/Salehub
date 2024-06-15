package com.example.salehub.screens.create_post

import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ImagePagerAdapter(fragment: Fragment, private val uris: List<Uri>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = uris.size

    override fun createFragment(position: Int): Fragment {
        Log.d("RRRR", "createFragment")
        return ImageFragment.newInstance(uris[position])
    }
}