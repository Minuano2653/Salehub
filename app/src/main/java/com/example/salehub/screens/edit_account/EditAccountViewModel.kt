package com.example.salehub.screens.edit_account

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salehub.model.account.Account
import com.example.salehub.model.edit_account.FirebaseEditAccountRepository
import com.example.salehub.screens.account.OperationState
import kotlinx.coroutines.launch

class EditAccountViewModel(private val editAccountRepository: FirebaseEditAccountRepository) : ViewModel() {

    private val _account: MutableLiveData<Account?> = MutableLiveData()
    val account: LiveData<Account?>
        get() = _account

    private val _saveResult: MutableLiveData<OperationState> = MutableLiveData()
    val saveResult: LiveData<OperationState>
        get() = _saveResult

    fun setAccount(account: Account?) {
        _account.value = account
    }

    fun updateAccountInfo(nickname: String, imageUri: Uri?) {
        _saveResult.value = OperationState.PENDING
        viewModelScope.launch {
            val result = editAccountRepository.updateAccountInfo(nickname, imageUri)
            if (result.isSuccess) {
                _saveResult.value = OperationState.SUCCESS
            } else {
                _saveResult.value = OperationState.ERROR
            }
        }
    }
}