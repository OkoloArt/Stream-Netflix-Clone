package com.example.watchhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchhub.R
import com.example.watchhub.databinding.CastListDetailBinding
import com.example.watchhub.model.CastResults
import com.squareup.picasso.Picasso

class MovieCastAdapter(
    private val dataSet: List<CastResults>,
    private val onItemClicked: (CastResults) -> Unit
) : RecyclerView.Adapter<MovieCastAdapter.MovieViewHolder>() {

    class MovieViewHolder(private val binding: CastListDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(castResults: CastResults) {
                binding.castName.text = castResults.castName
                val image = "https://image.tmdb.org/t/p/w342" + castResults.castProfilePath
                Picasso.get().load(image).into(binding.castImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            CastListDetailBinding.inflate(
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