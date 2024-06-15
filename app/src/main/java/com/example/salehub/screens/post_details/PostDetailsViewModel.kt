package com.example.salehub.screens.post_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salehub.model.posts.FirebasePostsRepository
import com.example.salehub.model.posts.PostItem
import com.example.salehub.screens.account.OperationState
import kotlinx.coroutines.launch

class PostDetailsViewModel(private val postsRepository: FirebasePostsRepository) : ViewModel() {
    private val _post = MutableLiveData<PostItem>()
    val post: LiveData<PostItem>
        get() = _post

    private val _incrementPostState = MutableLiveData<OperationState>()
    val incrementPostState: LiveData<OperationState>
        get() = _incrementPostState

    private val _decrementPostState = MutableLiveData<OperationState>()
    val decrementPostState: LiveData<OperationState>
        get() = _decrementPostState

    private val _addToFavouriteState = MutableLiveData<OperationState>()
    val addToFavouriteState: LiveData<OperationState>
        get() = _addToFavouriteState

    fun setPost(postItem: PostItem) {
        _post.value = postItem
    }

    fun addToFavourite() {
        _addToFavouriteState.value = OperationState.PENDING

        viewModelScope.launch {
            val result = _post.value?.id?.let { postsRepository.addToFavourite(it) }
            if (result != null) {
                if (result.isSuccess) {
                    _addToFavouriteState.value = result.getOrNull()
                } else {
                    _addToFavouriteState.value = OperationState.ERROR
                }
            }
        }
    }

    fun incrementPost() : Boolean {
        //EMPTY - пост уже увеличен
        //_incrementPostState.value = OperationState.PENDING
        viewModelScope.launch {
            val result = _post.value?.let { postsRepository.incrementPost(it.id) }
            if (result != null) {
                if (result.isSuccess) {
                    _incrementPostState.value = result.getOrNull()
                } else {
                    _incrementPostState.value = OperationState.ERROR
                }
            }
        }
        return _incrementPostState.value == OperationState.SUCCESS
    }

    fun decrementPost() : Boolean {
        //EMPTY - пост уже уменьшен
        //_decrementPostState.value = OperationState.PENDING
        viewModelScope.launch {
            val result = _post.value?.let { postsRepository.decrementPost(it.id) }
            if (result != null) {
                if (result.isSuccess) {
                    _decrementPostState.value = result.getOrNull()
                } else {
                    _decrementPostState.value = OperationState.ERROR
                }
            }
        }
        return _decrementPostState.value == OperationState.SUCCESS
    }
}