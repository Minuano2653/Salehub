package com.example.salehub.screens.account

import android.app.AlertDialog
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
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.salehub.R
import com.example.salehub.Repositories
import com.example.salehub.TabsFragmentDirections
import com.example.salehub.databinding.FragmentAccountBinding
import com.example.salehub.screens.edit_account.EditAccountFragment
import com.example.salehub.screens.posts.PostsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var pagerAdapter: PostsPagerAdapter

    private val viewModel = AccountViewModel(Repositories.accountRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAccountInfo()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_account, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.edit -> {
                launchEditAccountFragment()
                true
            }
            R.id.signOut -> {
                showSignOutConfirmationDialog()
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
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        setupViewPagerWithTabs()

        observeState()
        observeAccount()

        return binding.root
    }

    private fun showSignOutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Подтверждение выхода")
            .setMessage("Вы уверены, что хотите выйти из аккаунта?")
            .setPositiveButton("Да") { _, _ ->

                val activity = activity as AppCompatActivity?
                activity?.let {
                    val navController =
                        Navigation.findNavController(activity, R.id.mainGraphContainer)
                    NavigationUI.setupActionBarWithNavController(activity, navController)
                    navController.navigate(R.id.action_tabsFragment_to_signInFragment)
                    viewModel.signOut()
                }

            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun setupViewPagerWithTabs() {
        pagerAdapter = PostsPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.accountTabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Мои публикации"
                1 -> "Избранное"
                else -> null
            }
        }.attach()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) {
            hideAccountInfo()
            when(it) {
                OperationState.PENDING -> {
                    binding.accountInfoProgressBar.visibility = View.VISIBLE
                }
                OperationState.SUCCESS -> {
                    with(binding) {
                        accountInfoProgressBar.visibility = View.GONE
                        accountNicknameTextView.visibility = View.VISIBLE
                        accountEmailTextView.visibility = View.VISIBLE
                        accountRegistrationDateTextView.visibility = View.VISIBLE
                    }
                }
                OperationState.EMPTY -> {
                    binding.accountInfoNotFoundTextView.visibility = View.VISIBLE
                    binding.accountInfoProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Данные не найдены!", Toast.LENGTH_SHORT).show()
                }
                OperationState.ERROR -> {
                    binding.accountInfoNotFoundTextView.visibility = View.VISIBLE
                    binding.accountInfoProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Произошла ошибка при получении данных!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeAccount() {
        viewModel.account.observe(viewLifecycleOwner) {
            it?.let {
                with(binding) {
                    accountNicknameTextView.text = it.nickname
                    accountEmailTextView.text = it.email
                    accountRegistrationDateTextView.text = getString(R.string.registration_date_text, it.registrationDate)

                    if (it.avatarUrl.isNotBlank()) {
                        Glide.with(this@AccountFragment)
                            .load(it.avatarUrl)
                            .circleCrop()
                            .into(accountAvatarImageView)
                    }
                }
            }
        }
    }

    private fun launchEditAccountFragment() {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.mainGraphContainer) as NavHostFragment?

        if (navHostFragment != null) {
            val navController = navHostFragment.navController
            val action: TabsFragmentDirections.ActionTabsFragmentToEditAccountFragment =
                TabsFragmentDirections.actionTabsFragmentToEditAccountFragment(viewModel.account.value)
            navController.navigate(action)
        }
    }

    private fun hideAccountInfo() {
        with(binding) {
            accountNicknameTextView.visibility = View.GONE
            accountEmailTextView.visibility = View.GONE
            accountRegistrationDateTextView.visibility = View.GONE
            accountInfoProgressBar.visibility = View.GONE
            accountInfoNotFoundTextView.visibility = View.GONE
        }
    }
}