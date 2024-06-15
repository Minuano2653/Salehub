package com.example.salehub.screens.create_post

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salehub.model.create_post.FirebaseCreatePostRepository
import com.example.salehub.model.create_post.Post
import com.example.salehub.screens.account.OperationState
import kotlinx.coroutines.launch

class CreatePostViewModel(private val createPostRepository: FirebaseCreatePostRepository) : ViewModel() {

    private val _state: MutableLiveData<OperationState> = MutableLiveData()
    val state: LiveData<OperationState>
        get() = _state

    private val _uris = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val uris: LiveData<MutableList<Uri>> get() = _uris

    private val _selectedCategory = MutableLiveData<Int>(-1)
    val selectedCategory: LiveData<Int>
        get() = _selectedCategory


    fun savePost(post: Post) {
        _state.value = OperationState.PENDING
        viewModelScope.launch {
            val result = createPostRepository.savePost(post)
            if (result.isSuccess) {
                _state.value = OperationState.SUCCESS
            } else {
                _state.value = OperationState.ERROR
            }
        }
    }


    fun setSelectedCategory(selected: Int) {
        _selectedCategory.value = selected
    }

    fun addUris(newUris: List<Uri>) {
        _uris.value?.addAll(newUris)
        _uris.value = _uris.value
    }

    fun clearUris() {
        _uris.value = mutableListOf()
        //_uris.value = _uris.value
    }
}