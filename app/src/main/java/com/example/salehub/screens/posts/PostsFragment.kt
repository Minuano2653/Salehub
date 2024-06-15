package com.example.salehub.screens.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salehub.R
import com.example.salehub.Repositories
import com.example.salehub.TabsFragmentDirections
import com.example.salehub.databinding.FragmentPostsBinding
import com.example.salehub.model.posts.PostItem
import com.example.salehub.screens.account.OperationState

class PostsFragment : Fragment() {
    private lateinit var binding: FragmentPostsBinding
    private val viewModel = PostsViewModel(Repositories.postsRepository)
    private lateinit var postsAdapter: PostsAdapter


    private val type: Int by lazy {
        arguments?.getInt(ARG_TYPE) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostsBinding.inflate(layoutInflater)

        postsAdapter = PostsAdapter(object : OnPostClickListener {
            override fun onPostClick(postItem: PostItem) {
                launchPostDetailsFragment(postItem)
            }

            override fun onAddToFavouriteClick(postId: String) {
                viewModel.addToFavourite(postId)
            }

            override fun onIncrementClick(postId: String) : Boolean {
                return viewModel.incrementPost(postId)
            }

            override fun onDecrementClick(postId: String) : Boolean {
                return viewModel.decrementPost(postId)
            }
        })

        setupRecyclerView()
        observePosts()
        observeState()
        observeAddToFavouriteState()
        observeIncrementState()
        observeDecrementState()

        viewModel.fetchPosts(type)

        return binding.root
    }

    private fun observeIncrementState() {
        viewModel.incrementPostState.observe(viewLifecycleOwner) {
            when(it) {

                OperationState.SUCCESS -> {
                    viewModel.fetchPosts(type)
                }
                OperationState.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }
    }

    private fun observeDecrementState() {
        viewModel.decrementPostState.observe(viewLifecycleOwner) {
            when(it) {
                OperationState.SUCCESS -> {
                    viewModel.fetchPosts(type)
                }
                OperationState.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

    private fun observeAddToFavouriteState() {
        viewModel.addToFavouriteState.observe(viewLifecycleOwner) {
            Log.d("AAAA", it.name)
            when(it) {
                OperationState.EMPTY -> {
                    Toast.makeText(requireContext(), "Пост уже в избранном!", Toast.LENGTH_SHORT).show()
                }
                OperationState.SUCCESS -> {
                    Toast.makeText(requireContext(), "Пост добавлен в избранное!", Toast.LENGTH_SHORT).show()
                }
                OperationState.ERROR -> {
                    Toast.makeText(requireContext(), "Ошибка добавления в избранное!", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun observePosts() {
        viewModel.postItems.observe(viewLifecycleOwner) {
            it?.let {
                postsAdapter.setPosts(it)
            }
        }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) {
            when(it) {
                OperationState.PENDING -> {
                    binding.postsProgressBar.visibility = View.VISIBLE
                    binding.postsRecyclerView.visibility = View.GONE
                }
                OperationState.SUCCESS -> {
                    binding.postsProgressBar.visibility = View.GONE
                    binding.postsRecyclerView.visibility = View.VISIBLE
                }
                OperationState.EMPTY -> {
                    binding.postsProgressBar.visibility = View.GONE
                    binding.postsNotFoundTextView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Данные не найдены.", Toast.LENGTH_SHORT).show()
                }
                OperationState.ERROR -> {
                    binding.postsProgressBar.visibility = View.GONE
                    binding.postsNotFoundTextView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Ошибка при получении данных!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postsRecyclerView.adapter = postsAdapter
    }

    private fun launchPostDetailsFragment(postItem: PostItem) {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.mainGraphContainer) as NavHostFragment?

        if (navHostFragment != null) {
            val navController = navHostFragment.navController

            val action: TabsFragmentDirections.ActionTabsFragmentToPostDetailsFragment =
                TabsFragmentDirections.actionTabsFragmentToPostDetailsFragment(postItem)
            navController.navigate(action)
        }
    }

    companion object {

        const val TYPE_MY_POSTS = 0
        const val TYPE_FAVOURITE_POSTS = 1
        const val TYPE_NEW_POSTS = 2
        const val TYPE_BEST_POSTS = 3

        private const val ARG_TYPE = "ARG_TYPE"

        fun newInstance(postsType: Int) : PostsFragment {
            val fragment = PostsFragment()
            val args = Bundle()
            args.putInt(ARG_TYPE, postsType)
            fragment.arguments = args
            return fragment
        }
    }
}