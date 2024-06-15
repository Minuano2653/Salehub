package com.example.salehub.screens.edit_account

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.example.salehub.R
import com.example.salehub.Repositories
import com.example.salehub.databinding.FragmentEditAccountBinding
import com.example.salehub.screens.account.AccountFragment
import com.example.salehub.screens.account.OperationState

class EditAccountFragment : Fragment() {
    private lateinit var binding: FragmentEditAccountBinding
    private val viewModel = EditAccountViewModel(Repositories.editAccountRepository)

    private var initialUri: Uri? = null
    private var uri: Uri? = null
    private lateinit var initialNickname: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false)

        viewModel.setAccount(EditAccountFragmentArgs.fromBundle(requireArguments()).account)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        observeSaveResult()
        observeAccount()

        binding.saveAccountInfoButton.setOnClickListener {
            val currentNickname = binding.nicknameEditText.text.toString()
            val currentUri = uri

            if (initialNickname != currentNickname || initialUri != currentUri) {
                viewModel.updateAccountInfo(currentNickname, currentUri)
            } else {
                Toast.makeText(requireContext(), "Изменений не обнаружено", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editAvatarImageView.setOnClickListener{
            openPhotoPicker()
        }

    }



    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(binding.editAvatarImageView)
            this.uri = uri
        }
    }

    private fun observeSaveResult() {
        viewModel.saveResult.observe(viewLifecycleOwner) {
            when (it) {
                OperationState.PENDING -> {
                    binding.saveAccountInfoButton.text = ""
                    binding.saveAccountInfoButton.isClickable = false
                    binding.saveProgressBar.visibility = View.VISIBLE
                }
                OperationState.SUCCESS -> {
                    binding.saveAccountInfoButton.text = getString(R.string.save_button_text)
                    binding.saveAccountInfoButton.isClickable = true
                    binding.saveProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Информация успешно обновлена!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                OperationState.ERROR -> {
                    binding.saveAccountInfoButton.text = getString(R.string.save_button_text)
                    binding.saveAccountInfoButton.isClickable = true
                    binding.saveProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ошибка при обновлении информации!", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun observeAccount() {
        viewModel.account.observe(viewLifecycleOwner) {
            it?.let {
                initialNickname = it.nickname
                initialUri = if (it.avatarUrl.isNotBlank()) Uri.parse(it.avatarUrl) else null
                binding.nicknameEditText.setText(it.nickname)
                if (it.avatarUrl.isNotBlank()) {
                    Glide.with(this@EditAccountFragment)
                        .load(it.avatarUrl)
                        .circleCrop()
                        .into(binding.editAvatarImageView)
                }
            }
        }
    }

    private fun openPhotoPicker() {
        photoPickerLauncher.launch("image/*")
    }

    private fun setupToolbar() {
        val navController: NavController

        val activity = activity as AppCompatActivity?
        activity?.let {
            it.setSupportActionBar(binding.editAccountToolbar)
            navController = findNavController(it, R.id.mainGraphContainer)
            setupActionBarWithNavController(it, navController)

        }

        binding.editAccountToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    companion object {
        const val EDIT_ACCOUNT_FRAGMENT_REQUEST_KEY = "EDIT_ACCOUNT_FRAGMENT_REQUEST_KEY"
    }
}