package com.example.salehub.screens.sign_in

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.salehub.R
import com.example.salehub.Repositories
import com.example.salehub.databinding.FragmentSignInBinding
import com.example.salehub.screens.sign_up.AuthState

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel = SignInViewModel(Repositories.firebaseAuthRepository)

    override fun onStart() {
        super.onStart()

        if (viewModel.isSignedIn()) launchTabsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        observeAuthState()

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Не все поля заполнены!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.signInWithEmailAndPassword(email, password)
            }
        }

        binding.goToSignUpButton.setOnClickListener {
            launchSignUpFragment()
        }

        return binding.root
    }

    private fun observeAuthState() {
        viewModel.authState.observe(viewLifecycleOwner) {
            when (it) {
                AuthState.PENDING -> {
                    with(binding) {
                        signInButton.text = ""
                        signInProgressBar.visibility = View.VISIBLE
                    }
                }
                AuthState.SUCCESS -> {
                    with(binding) {
                        signInButton.text = getString(R.string.sign_in_button_text)
                        signInProgressBar.visibility = View.GONE
                        launchTabsFragment()
                    }
                }
                AuthState.ERROR -> {
                    with(binding) {
                        signInButton.text = getString(R.string.sign_in_button_text)
                        signInProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun launchTabsFragment() {
        findNavController().navigate(R.id.action_signInFragment_to_tabsFragment)
    }

    private fun launchSignUpFragment() {
        findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
    }
}