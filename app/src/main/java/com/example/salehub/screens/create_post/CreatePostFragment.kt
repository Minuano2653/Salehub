package com.example.salehub.screens.create_post

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.salehub.R
import com.example.salehub.Repositories
import com.example.salehub.databinding.FragmentCreatePostBinding
import com.example.salehub.getCurrentDate
import com.example.salehub.model.create_post.Post
import com.example.salehub.screens.account.OperationState

class CreatePostFragment : Fragment() {
    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var imagePagerAdapter: ImagePagerAdapter

    private val viewModel = CreatePostViewModel(Repositories.createPostRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_create_post, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.addPhoto -> {
                openPhotoPicker()
                true
            }
            R.id.deletePostInfo -> {
                showDeletingInfoDialog()
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
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)

        observeUris()
        observeState()

        setupImagePagerAdapter()

        binding.pickPhotosContainer.setOnClickListener {
            openPhotoPicker()
        }

        binding.categoryTextView.setOnClickListener {
            val categories = resources.getStringArray(R.array.categories)
            showCategoryAlertDialog(categories, viewModel.selectedCategory.value ?: -1) { selected ->
                binding.categoryTextView.text = categories[selected]
                viewModel.setSelectedCategory(selected)
            }
        }

        binding.publishButton.setOnClickListener {
            with(binding) {
                val postName = postNameEditText.text.toString()
                val oldCost = oldCostEditText.text.toString()
                val newCost = newCostEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val category: String = categoryTextView.text.toString()
                val link = linkEditText.text.toString()
                val address = addressEditText.text.toString()
                val uris = viewModel.uris.value

                if (postName.isBlank() || oldCost.isBlank() || newCost.isBlank() || description.isBlank() || category.isBlank()) {
                    Toast.makeText(requireContext(), "Не все обязательные поля заполнены!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.savePost(Post(
                        name = postName,
                        oldCost = oldCost,
                        newCost = newCost,
                        description = description,
                        category = category,
                        link = link,
                        address = address,
                        imageUrls = uris ?: emptyList(),
                        date = getCurrentDate()
                    ))
                }
            }

        }

        return binding.root
    }

    private fun observeUris() {
        viewModel.uris.observe(viewLifecycleOwner) {
            with(binding) {
                if (it.isEmpty()) {
                    pickPhotosContainer.visibility = View.VISIBLE
                    imageViewPager.visibility = View.GONE
                } else {
                    pickPhotosContainer.visibility = View.GONE
                    imageViewPager.visibility = View.VISIBLE
                    imagePagerAdapter.notifyDataSetChanged()
                }
            }

        }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) {
            when(it) {
                OperationState.PENDING -> {
                    binding.publishProgressBar.visibility = View.VISIBLE
                    shutAll()
                }
                OperationState.SUCCESS -> {
                    binding.publishProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Скидка опубликована!", Toast.LENGTH_SHORT).show()
                    enableAll()
                }
                OperationState.ERROR -> {
                    binding.publishProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ошибка добавления скидки!", Toast.LENGTH_SHORT).show()
                    enableAllWithoutWipingData()
                }
                else -> {}

            }
        }
    }

    private fun showDeletingInfoDialog() {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Подтверждение удаления")
            .setMessage("Вы уверены, что хотите стереть все введённые данные?")
            .setPositiveButton("Да") { _, _ ->
                clearAll()
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showCategoryAlertDialog(categories: Array<String>, selectedOption: Int, onOptionSelected: (Int) -> Unit) {
        var selected = selectedOption

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
        builder.setTitle("Выберите категорию")
        builder.setSingleChoiceItems(categories, selectedOption) { _, which ->
            selected = which
        }

        builder.setPositiveButton("OK") { dialog, _ ->
            onOptionSelected(selected)
            dialog.dismiss()
        }

        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun openPhotoPicker() {
        photoPickerLauncher.launch("image/*")
    }

    private fun setupImagePagerAdapter() {
        imagePagerAdapter = ImagePagerAdapter(this, viewModel.uris.value ?: mutableListOf())
        binding.imageViewPager.adapter = imagePagerAdapter
    }

    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { selectedUris ->
        if (selectedUris.isNotEmpty()) {
            viewModel.addUris(selectedUris)
        } else {
            viewModel.clearUris()
        }
    }

    private fun shutAll() {
        with(binding) {
            postNameEditText.isEnabled = false
            oldCostEditText.isEnabled = false
            newCostEditText.isEnabled = false
            descriptionEditText.isEnabled = false
            linkEditText.isEnabled = false
            addressEditText.isEnabled = false
            categoryTextView.isEnabled = false
            categoryTextView.text = ""
        }
    }

    private fun clearAll() {
        with(binding) {
            postNameEditText.text?.clear()
            oldCostEditText.text?.clear()
            newCostEditText.text?.clear()
            descriptionEditText.text?.clear()
            linkEditText.text?.clear()
            addressEditText.text?.clear()
            viewModel.setSelectedCategory(-1)
            viewModel.clearUris()
        }
    }

    private fun enableAll() {
        with(binding) {
            postNameEditText.isEnabled = true
            oldCostEditText.isEnabled = true
            newCostEditText.isEnabled = true
            descriptionEditText.isEnabled = true
            linkEditText.isEnabled = true
            addressEditText.isEnabled = true
            categoryTextView.isEnabled = true
            categoryTextView.text = getString(R.string.publish_button_text)

            postNameEditText.text?.clear()
            oldCostEditText.text?.clear()
            newCostEditText.text?.clear()
            descriptionEditText.text?.clear()
            linkEditText.text?.clear()
            addressEditText.text?.clear()
            viewModel.setSelectedCategory(-1)
            viewModel.clearUris()
        }
    }

    private fun enableAllWithoutWipingData() {
        with(binding) {
            postNameEditText.isEnabled = true
            oldCostEditText.isEnabled = true
            newCostEditText.isEnabled = true
            descriptionEditText.isEnabled = true
            linkEditText.isEnabled = true
            addressEditText.isEnabled = true
            categoryTextView.isEnabled = true
            categoryTextView.text = getString(R.string.publish_button_text)
        }
    }

}