package com.example.salehub.screens.post_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.salehub.R
import com.example.salehub.Repositories
import com.example.salehub.databinding.FragmentPostDetailsBinding
import com.example.salehub.screens.posts.PostsViewModel

class PostDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPostDetailsBinding
    private val viewModel = PostsViewModel(Repositories.postsRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_post_details, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController: NavController

        val activity = activity as AppCompatActivity?
        activity?.let {
            activity.setSupportActionBar(binding.postDetailsToolbar)
            navController = Navigation.findNavController(activity, R.id.mainGraphContainer)
            NavigationUI.setupActionBarWithNavController(activity, navController)
        }

        binding.postDetailsToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

}