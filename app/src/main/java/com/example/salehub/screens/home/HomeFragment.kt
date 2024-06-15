package com.example.salehub.screens.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.salehub.R
import com.example.salehub.databinding.FragmentHomeBinding
import com.example.salehub.screens.posts.PostsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var pagerAdapter: PostsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.chooseCategory -> {
                true
            }
            else -> false
        }
    }

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

    private fun showCategoryAlertDialog(categories: Array<String>, selectedOption: Int) {
        var selected = selectedOption

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
        builder.setTitle("Выберите категорию")
        builder.setSingleChoiceItems(categories, selectedOption) { _, which ->
            selected = which
        }

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
}