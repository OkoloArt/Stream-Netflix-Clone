package com.example.watchhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchhub.databinding.MovieListDetailBinding
import com.example.watchhub.database.Movie
import com.squareup.picasso.Picasso

class MovieDatabaseRecyclerAdapter(private val onItemClicked: (Movie) -> Unit) :
    ListAdapter<Movie, MovieDatabaseRecyclerAdapter.MovieViewHolder>(DiffCallback) {

    class MovieViewHolder(private val binding: MovieListDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.movieName
            val image = "https://image.tmdb.org/t/p/w342${movie.movieImage}"
            Picasso.get().load(image).into(binding.movieImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            MovieListDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context)))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.movieName == newItem.movieName
            }
        }
    }
}