package com.example.salehub.screens.posts

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salehub.model.posts.FirebasePostsRepository
import com.example.salehub.model.posts.PostItem
import com.example.salehub.screens.account.OperationState
import kotlinx.coroutines.launch

class PostsViewModel(private val postsRepository: FirebasePostsRepository) : ViewModel() {

    private val _state = MutableLiveData<OperationState>()
    val state: LiveData<OperationState>
        get() = _state

    private val _postItems = MutableLiveData<List<PostItem>?>()
    val postItems: LiveData<List<PostItem>?>
        get() = _postItems

    private val _incrementPostState = MutableLiveData<OperationState>()
    val incrementPostState: LiveData<OperationState>
        get() = _incrementPostState

    private val _decrementPostState = MutableLiveData<OperationState>()
    val decrementPostState: LiveData<OperationState>
        get() = _decrementPostState

    private val _addToFavouriteState = MutableLiveData<OperationState>()
    val addToFavouriteState: LiveData<OperationState>
        get() = _addToFavouriteState

    fun fetchPosts(type: Int) {
        try {
            _state.value = OperationState.PENDING
            viewModelScope.launch {
                val result = postsRepository.fetchPostsByType(type)
                if (result != null) {
                    _state.value = OperationState.SUCCESS
                    _postItems.value = result
                } else {
                    _state.value = OperationState.EMPTY
                }
            }
        } catch (e: Exception) {
            Log.d("RRRR", e.message.toString())
            _state.value = OperationState.ERROR
        }
    }

    fun addToFavourite(postId: String) {
        _addToFavouriteState.value = OperationState.PENDING

        viewModelScope.launch {
            val result = postsRepository.addToFavourite(postId)
            if (result.isSuccess) {
                _addToFavouriteState.value = result.getOrNull()
            } else {
                _addToFavouriteState.value = OperationState.ERROR
            }
        }
    }

    fun incrementPost(postId: String) : Boolean {
        //EMPTY - пост уже увеличен
        //_incrementPostState.value = OperationState.PENDING
        viewModelScope.launch {
            val result = postsRepository.incrementPost(postId)
            if (result.isSuccess) {
                _incrementPostState.value = result.getOrNull()
            } else {
                _incrementPostState.value = OperationState.ERROR
            }
        }
        return _incrementPostState.value == OperationState.SUCCESS
    }

    fun decrementPost(postId: String) : Boolean {
        //EMPTY - пост уже уменьшен
        //_decrementPostState.value = OperationState.PENDING
        viewModelScope.launch {
            val result = postsRepository.decrementPost(postId)
            if (result.isSuccess) {
                _decrementPostState.value = result.getOrNull()
            } else {
                _decrementPostState.value = OperationState.ERROR
            }
        }
        return _decrementPostState.value == OperationState.SUCCESS
    }
}