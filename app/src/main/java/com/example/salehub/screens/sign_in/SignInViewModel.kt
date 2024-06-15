package com.example.salehub.screens.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salehub.model.auth.FirebaseAuthRepository
import com.example.salehub.screens.sign_up.AuthState
import kotlinx.coroutines.launch

class SignInViewModel(private val authRepository: FirebaseAuthRepository) : ViewModel() {

    private val _authState: MutableLiveData<AuthState> = MutableLiveData()
    val authState: LiveData<AuthState>
        get() = _authState

    fun isSignedIn() = authRepository.isSingedIn()

    fun signInWithEmailAndPassword(email: String, password: String) {
        _authState.value = AuthState.PENDING
        viewModelScope.launch {
            try {
                authRepository.signInWithEmailAndPassword(email, password)
                _authState.postValue(AuthState.SUCCESS)
            } catch (e: Exception) {
                _authState.postValue(AuthState.ERROR)
            }
        }
    }

}