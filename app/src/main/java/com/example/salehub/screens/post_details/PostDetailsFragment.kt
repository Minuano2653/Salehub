package com.example.salehub.screens.post_details

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salehub.R
import com.example.salehub.Repositories
import com.example.salehub.databinding.FragmentPostDetailsBinding
import com.example.salehub.model.posts.Comment
import com.example.salehub.screens.account.OperationState
import com.example.salehub.screens.create_post.ImagePagerAdapter
import com.example.salehub.screens.posts.PostsViewModel

class PostDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPostDetailsBinding
    private val viewModel = PostDetailsViewModel(Repositories.postsRepository)
    private lateinit var imagePagerAdapter: ImagePagerAdapter
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_post_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.addToFavourite -> {
                viewModel.addToFavourite()
                true
            }

            else -> {false}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostDetailsBinding.inflate(inflater, container, false)

        PostDetailsFragmentArgs.fromBundle(requireArguments()).postItem?.let { viewModel.setPost(it) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        setupImagePagerAdapter()
        setupCommentsAdapter()

        observeComments()
        observePost()
        observeIncrementPostState()
        observeDecrementPostState()
        observeAddToFavouriteState()

        binding.plusImageView.setOnClickListener {
            viewModel.incrementPost()
        }

        binding.minusImageView.setOnClickListener {
            viewModel.decrementPost()
        }

        binding.sendCommentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString()
            if (commentText.isNotBlank()) {
                val comment = Comment(
                    id = "",
                    postId = viewModel.post.value?.id ?: "",
                    userId = "",
                    userName = "",
                    content = commentText
                )
                viewModel.addComment(comment)
                binding.commentEditText.text.clear()
            }
        }

        viewModel.fetchComments()

        binding.addressTextView.setOnClickListener {
            tryToOpenMap()
        }

    }

    private fun tryToOpenMap() {
        val address = binding.addressTextView.text.toString()
        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Address", address)
        clipboardManager.setPrimaryClip(clipData)

        val packageNames = listOf(
            "com.google.android.apps.maps",  // Google Maps
            "ru.yandex.yandexmaps"  // yandex maps
        )

        val intents = mutableListOf<Intent>()
        for (packageName in packageNames) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("geo:0,0?q=$address")
            intent.setPackage(packageName)
            intents.add(intent)
        }

        val chooserIntent = Intent.createChooser(intents.removeAt(0), "Выберите приложение для открытия карт")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())

        if (chooserIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(chooserIntent)
        } else {
            Toast.makeText(requireContext(), "Ни одно приложение для карт не установлено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupImagePagerAdapter() {
        val imageUrls = viewModel.post.value?.imageUrls ?: mutableListOf()
        val imageUris = imageUrls.map { Uri.parse(it) }
        imagePagerAdapter = ImagePagerAdapter(this, imageUris)
        binding.imageViewPager.adapter = imagePagerAdapter
    }

    private fun observeAddToFavouriteState() {
        viewModel.addToFavouriteState.observe(viewLifecycleOwner) {
            when(it) {
                OperationState.EMPTY -> {
                    Toast.makeText(requireContext(), "Пост уже в избранном!", Toast.LENGTH_SHORT).show()
                }
                OperationState.SUCCESS -> {
                    Toast.makeText(requireContext(), "Пост успешно добавлен в избранное!", Toast.LENGTH_SHORT).show()
                }
                OperationState.ERROR -> {
                    Toast.makeText(requireContext(), "Ошибка добавления в избранное!", Toast.LENGTH_SHORT).show()
                } else -> {}
            }
        }
    }

    private fun observeIncrementPostState() {
        viewModel.incrementPostState.observe(viewLifecycleOwner) {operationState ->
            when(operationState) {
                OperationState.EMPTY -> {
                    Toast.makeText(requireContext(), "Вы уже лайкали этот пост!", Toast.LENGTH_SHORT).show()
                }
                OperationState.SUCCESS -> {
                    viewModel.post.value?.let { postItem -> viewModel.setPost(postItem.copy(rating = postItem.rating + 1)) }
                    Toast.makeText(requireContext(), "Пост успешно лайкнут!", Toast.LENGTH_SHORT).show()
                }
                OperationState.ERROR -> {
                    Toast.makeText(requireContext(), "Ошибка!", Toast.LENGTH_SHORT).show()
                } else -> {}
            }
        }
    }

    private fun observeDecrementPostState() {
        viewModel.decrementPostState.observe(viewLifecycleOwner) {operationState ->
            when(operationState) {
                OperationState.EMPTY -> {
                    Toast.makeText(requireContext(), "Вы уже дизлайкали этот пост!", Toast.LENGTH_SHORT).show()
                }
                OperationState.SUCCESS -> {
                    viewModel.post.value?.let { postItem -> viewModel.setPost(postItem.copy(rating = postItem.rating - 1)) }
                    Toast.makeText(requireContext(), "Пост успешно дизлайкнут!", Toast.LENGTH_SHORT).show()
                }
                OperationState.ERROR -> {
                    Toast.makeText(requireContext(), "Ошибка!", Toast.LENGTH_SHORT).show()
                } else -> {}
            }

        }
    }

    private fun observePost() {
        viewModel.post.observe(viewLifecycleOwner) { postItem ->
            with(binding) {
                publicationDateTextView.text = postItem.date
                postRatingTextView.text = postItem.rating.toString()
                postNameTextView.text = postItem.name
                oldCostTextView.text = getString(R.string.old_cost_text_view, postItem.oldCost)
                newCostTextView.text = getString(R.string.new_cost_text_view, postItem.newCost)
                postAuthorTextView.text =
                    getString(R.string.post_author_text_view, postItem.author)
                postDescriptionTextView.text = postItem.description
                linkTextView.text = postItem.link
                addressTextView.text = postItem.address
            }
        }

    }

    private fun setupToolbar() {
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

    private fun setupCommentsAdapter() {
        commentsAdapter = CommentsAdapter()
        binding.commentsRecyclerView.adapter = commentsAdapter
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun observeComments() {
        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentsAdapter.updateComments(comments)
        }
    }
}