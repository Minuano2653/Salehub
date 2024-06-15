package com.example.salehub.screens.sign_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.salehub.R
import com.example.salehub.Repositories
import com.example.salehub.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment()  {
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel = SignUpViewModel(Repositories.firebaseAuthRepository)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        observeAuthState()

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val passwordForRepeat = binding.passwordForRepeatEditText.text.toString()
            val nickname = binding.nicknameEditText.text.toString()

            if (email.isBlank() || password.isBlank() || passwordForRepeat.isBlank()) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
            } else if (password != passwordForRepeat) {
                Toast.makeText(requireContext(), "Пароли не совпадают!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.signUpWithEmailAndPassword(email, password, nickname)
            }

        }

        binding.toSignInButton.setOnClickListener {
            launchSignInFragment()
        }

        return binding.root
    }

    private fun observeAuthState() {
        viewModel.authState.observe(viewLifecycleOwner) {
            when(it) {
                AuthState.PENDING -> {
                    with(binding) {
                        signUpButton.text = ""
                        signUpProgressBar.visibility = View.VISIBLE
                    }
                }
                AuthState.SUCCESS -> {
                    with(binding) {
                        signUpButton.text = getString(R.string.sign_up_button_text)
                        signUpProgressBar.visibility = View.GONE
                        launchTabsFragment()
                    }
                }
                AuthState.ERROR -> {
                    with(binding) {
                        signUpButton.text = getString(R.string.sign_up_button_text)
                        signUpProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT)
                    }
                }
            }
        }
    }

    private fun launchTabsFragment() {
        findNavController().navigate(R.id.action_signUpFragment_to_tabsFragment)
    }

    private fun launchSignInFragment() {
        findNavController().navigateUp()
    }
}