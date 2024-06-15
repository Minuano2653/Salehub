package com.example.salehub.screens.account

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salehub.model.account.Account
import com.example.salehub.model.account.FirebaseAccountRepository
import kotlinx.coroutines.launch

class AccountViewModel(private val accountRepository: FirebaseAccountRepository) : ViewModel() {

    private val _state: MutableLiveData<OperationState> = MutableLiveData()
    val state: LiveData<OperationState>
        get() = _state

    private val _account: MutableLiveData<Account?> = MutableLiveData()
    val account: LiveData<Account?>
        get() = _account

    fun signOut() {
        accountRepository.signOut()
    }

    fun fetchAccountInfo() {
        _state.value = OperationState.PENDING
        viewModelScope.launch {
            try {
                val account = accountRepository.fetchAccountInfo()
                if (account == null) {
                    _state.value = OperationState.EMPTY
                } else {
                    _state.value = OperationState.SUCCESS
                    _account.value = account
                }
            } catch (e: Exception) {
                _state.value = OperationState.ERROR
            }
        }
    }
}

enum class OperationState {
    PENDING,
    SUCCESS,
    EMPTY,
    ERROR
}