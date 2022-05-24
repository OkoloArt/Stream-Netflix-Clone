package com.example.watchhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchhub.databinding.MovieListDetailBinding
import com.example.watchhub.model.TvShowResults
import com.squareup.picasso.Picasso

class TvShowRecyclerAdapter(
    private val dataSet: List<TvShowResults>,
    private val onItemClicked: (TvShowResults) -> Unit
) : RecyclerView.Adapter<TvShowRecyclerAdapter.TvShowViewHolder>() {

    class TvShowViewHolder(private val binding: MovieListDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tvShowResults: TvShowResults) {
            binding.movieTitle.text = tvShowResults.tvTitle
            val image = "https://image.tmdb.org/t/p/w342${tvShowResults.tvPosterPath}"
            Picasso.get().load(image).into(binding.movieImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        return TvShowViewHolder(
            MovieListDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        val current = dataSet[position]
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    override fun getItemCount() = dataSet.size

}