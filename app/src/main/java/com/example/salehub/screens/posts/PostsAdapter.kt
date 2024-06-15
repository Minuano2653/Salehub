package com.example.salehub.screens.posts

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.salehub.R
import com.example.salehub.databinding.ItemPostBinding
import com.example.salehub.model.create_post.Post
import com.example.salehub.model.posts.PostItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface OnPostClickListener {
    fun onPostClick(postItem: PostItem)
    fun onAddToFavouriteClick(postId: String)
    fun onIncrementClick(postId: String) : Boolean
    fun onDecrementClick(postId: String) : Boolean
}

class PostsAdapter(private val listener: OnPostClickListener) : RecyclerView.Adapter<PostsAdapter.PostHolder>() {

    private var posts = mutableListOf<PostItem>()

    private var postsCopy = mutableListOf<PostItem>()


    fun setPosts(posts: List<PostItem>) {
        this.posts = posts.toMutableList()
        postsCopy.clear()
        postsCopy.addAll(posts)
        notifyDataSetChanged()
    }

    inner class PostHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(inflater, parent, false)

        return PostHolder(binding)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val postItem = posts[position]

        val context = holder.itemView.context

        with(holder.binding) {
            publicationDateTextView.text = postItem.date
            postRatingTextView.text = postItem.rating.toString()
            postNameTextView.text = postItem.name
            oldCostTextView.text = context.getString(R.string.old_cost_text_view, postItem.oldCost)
            newCostTextView.text = context.getString(R.string.new_cost_text_view, postItem.newCost)
            authorTextView.text = postItem.author

            val cardBackgroundColor = when {
                postItem.rating > 0 -> ContextCompat.getColor(context, R.color.pastel_orange)
                postItem.rating < 0 -> ContextCompat.getColor(context, R.color.pastel_blue)
                else -> Color.WHITE
            }
            root.setCardBackgroundColor(cardBackgroundColor)

            if (postItem.imageUrls.isNotEmpty()) {
                Glide
                    .with(holder.itemView)
                    .load(postItem.imageUrls[0])
                    .into(postImageView)
            }

            if (postItem.authorAvatar.isNotBlank()) {
                Glide
                    .with(holder.itemView)
                    .load(postItem.authorAvatar)
                    .circleCrop()
                    .into(authorImageView)
            }
        }

        holder.binding.root.setOnClickListener {
            listener.onPostClick(postItem)
        }

        holder.binding.addToFavouriteImageView.setOnClickListener {
            listener.onAddToFavouriteClick(postItem.id)
        }

        holder.binding.plusImageView.setOnClickListener {
            if (listener.onIncrementClick(postItem.id)) {
                postItem.rating += 1
                posts[position] = postItem
                //posts[position] = postItem.copy(rating = postItem.rating + 1)
                notifyItemChanged(position)
            }
        }

        holder.binding.minusImageView.setOnClickListener {
            if (listener.onDecrementClick(postItem.id)) {
                postItem.rating -= 1
                posts[position] = postItem
                //posts[position] = postItem.copy(rating = postItem.rating - 1)
                notifyItemChanged(position)
            }
        }
    }
}