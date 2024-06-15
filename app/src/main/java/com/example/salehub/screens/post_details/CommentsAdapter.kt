package com.example.salehub.screens.post_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.salehub.databinding.ItemCommentBinding
import com.example.salehub.model.posts.Comment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UsersDiffCallback(
    private val oldList: List<Comment>,
    private val newList: List<Comment>,
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldComment = oldList[oldItemPosition]
        val newComment = newList[newItemPosition]
        return oldComment.id == newComment.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldComment = oldList[oldItemPosition]
        val newComment = newList[newItemPosition]
        return oldComment == newComment
    }
}

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    private var comments = mutableListOf<Comment>()

    fun updateComments(newComments: List<Comment>) {
        val diffCallback = UsersDiffCallback(comments, newComments)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        comments.clear()
        comments.addAll(newComments)
        diffResult.dispatchUpdatesTo(this)
    }

    class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.userNameTextView.text = comment.userName
            binding.commentContentTextView.text = comment.content
            binding.timestampTextView.text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(
                Date(comment.timestamp)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    class DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }
}
