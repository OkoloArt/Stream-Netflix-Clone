package com.example.watchhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchhub.database.Movie
import com.example.watchhub.databinding.MovieListDetailBinding
import com.example.watchhub.tvShowDatabase.TvShows
import com.squareup.picasso.Picasso

class TvDatabaseRecyclerAdapter(private val onItemClicked: (TvShows) -> Unit) :
    ListAdapter<TvShows, TvDatabaseRecyclerAdapter.TvShowViewHolder>(DiffCallback) {

    class TvShowViewHolder(private val binding: MovieListDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tvShows: TvShows) {
            binding.movieTitle.text = tvShows.tvShowName
            val image = "https://image.tmdb.org/t/p/w342${tvShows.tvShowImage}"
            Picasso.get().load(image).into(binding.movieImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        return TvShowViewHolder(
            MovieListDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context)))
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<TvShows>() {
            override fun areItemsTheSame(oldItem: TvShows, newItem: TvShows): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: TvShows, newItem: TvShows): Boolean {
                return oldItem.tvShowName == newItem.tvShowName
            }
        }
    }
}