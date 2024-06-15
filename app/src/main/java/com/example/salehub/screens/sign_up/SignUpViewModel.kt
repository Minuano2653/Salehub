package com.example.salehub.screens.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salehub.model.auth.FirebaseAuthRepository
import kotlinx.coroutines.launch


class SignUpViewModel(private val authRepository: FirebaseAuthRepository) : ViewModel() {

    private val _authState: MutableLiveData<AuthState> = MutableLiveData()
    val authState: LiveData<AuthState>
        get() = _authState


    fun signUpWithEmailAndPassword(email: String, password: String, nickname: String) {
        _authState.value = AuthState.PENDING
        viewModelScope.launch {
            try {
                authRepository.signUpWithEmailAndPassword(email, password, nickname)
                _authState.postValue(AuthState.SUCCESS)
            } catch (e: Exception) {
                _authState.postValue(AuthState.ERROR)
            }
        }
    }
}

enum class AuthState {
    PENDING,
    SUCCESS,
    ERROR
}