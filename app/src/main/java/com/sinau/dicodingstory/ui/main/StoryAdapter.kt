package com.sinau.dicodingstory.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sinau.dicodingstory.data.remote.response.ListStoryItem
import com.sinau.dicodingstory.databinding.StoryItemBinding
import com.sinau.dicodingstory.ui.detail.DetailActivity
import com.sinau.dicodingstory.ui.detail.DetailActivity.Companion.EXTRA_ID

class StoryAdapter(private val listStory: List<ListStoryItem>) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: ListStoryItem) {
            binding.apply {
                Glide.with(context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(ivStory)
                tvName.text = story.name
                tvDescription.text = story.description
                tvDate.text = story.createdAt

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    itemView.context.startActivity(intent)
                    intent.putExtra(EXTRA_ID, story.id)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = listStory[position]
        holder.bind(holder.itemView.context, story)
    }

}