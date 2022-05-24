package com.example.watchhub.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchhub.databinding.EpisodeDetailsBinding
import com.example.watchhub.model.EpisodeDetails
import com.squareup.picasso.Picasso

class EpisodeAdapter(
    private val dataSet: List<EpisodeDetails>,
    private val onItemClicked: (EpisodeDetails) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {


    class EpisodeViewHolder(private val binding: EpisodeDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(episodeDetails: EpisodeDetails) {
            if (episodeDetails.episodeOverview.isNotEmpty()) {
                binding.episodeTitle.text = episodeDetails.episodeName
                val image = "https://image.tmdb.org/t/p/w342${episodeDetails.episodePosterPath}"
                Picasso.get().load(image).into(binding.episodeImage)
                binding.episodeDescription.text = episodeDetails.episodeOverview
                binding.episodeNumber.text = "${episodeDetails.episodeNumber}."
                binding.runtime.text = "${episodeDetails.episodeRuntime}m"
            } else {
                binding.cardview.visibility = View.GONE
                binding.episodeTitle.visibility = View.GONE
                binding.episodeDescription.visibility = View.GONE
                binding.episodeImage.visibility = View.GONE
                binding.episodeNumber.visibility = View.GONE
                binding.runtime.visibility = View.GONE

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        return EpisodeViewHolder(
            EpisodeDetailsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val current = dataSet[position]
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    override fun getItemCount() = dataSet.size
}