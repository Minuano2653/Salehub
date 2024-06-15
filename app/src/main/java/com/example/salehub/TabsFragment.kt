package com.example.salehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.salehub.databinding.FragmentTabsBinding

class TabsFragment : Fragment()  {
    private lateinit var binding: FragmentTabsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTabsBinding.inflate(layoutInflater)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.tabsGraphContainer) as NavHostFragment
        val navController = navHostFragment.navController

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment, R.id.createPostFragment, R.id.accountFragment
        ).build()
        setupActionBarWithNavController(activity, navController, appBarConfiguration)
        setupWithNavController(binding.bottomNavigationView, navController)

        return binding.root
    }
}