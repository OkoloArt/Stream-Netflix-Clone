package com.example.watchhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.watchhub.databinding.MovieListDetailBinding
import com.example.watchhub.model.MovieResults
import com.squareup.picasso.Picasso


class MovieRecyclerAdapter(
    private val dataSet: List<MovieResults>,
    private val onItemClicked: (MovieResults) -> Unit
) : RecyclerView.Adapter<MovieRecyclerAdapter.MovieViewHolder>() {


    class MovieViewHolder(private val binding: MovieListDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movieResults: MovieResults) {
            binding.movieTitle.text = movieResults.movieTitle
            val image = "https://image.tmdb.org/t/p/w342${movieResults.moviePosterPath}"
            Picasso.get().load(image).into(binding.movieImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            MovieListDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val current = dataSet[position]
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    override fun getItemCount() = dataSet.size
}