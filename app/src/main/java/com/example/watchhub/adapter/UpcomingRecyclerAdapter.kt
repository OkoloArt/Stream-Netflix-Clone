package com.example.watchhub.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.watchhub.databinding.ComingSoonDetailBinding
import com.example.watchhub.model.MovieResults
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class UpcomingRecyclerAdapter(
    private val dataSet: List<MovieResults>,
    private val onItemClicked: (MovieResults) -> Unit
) : RecyclerView.Adapter<UpcomingRecyclerAdapter.UpcomingMovieViewHolder>() {


    class UpcomingMovieViewHolder(private val binding: ComingSoonDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(movieResults: MovieResults) {
            binding.comingSoonTitle.text = movieResults.movieTitle
            val image = "https://image.tmdb.org/t/p/w342${movieResults.moviePosterPath}"
            Picasso.get().load(image).into(binding.comingSoonImage)
            binding.comingSoonDate.text = diffInDays(movieResults.movieReleaseDate)
            binding.comingSoonMovieDescription.text = movieResults.movieOverview
            binding.date.text = dateDay(movieResults.movieReleaseDate)
            binding.buttonIncreaseMaxLines.setOnClickListener {
                binding.comingSoonMovieDescription.maxLines = 7
                binding.buttonIncreaseMaxLines.visibility=View.INVISIBLE
                binding.buttonDecreaseMaxLines.visibility=View.VISIBLE
            }
            binding.buttonDecreaseMaxLines.setOnClickListener {
                binding.comingSoonMovieDescription.maxLines = 3
                binding.buttonIncreaseMaxLines.visibility=View.VISIBLE
                binding.buttonDecreaseMaxLines.visibility=View.INVISIBLE
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun dateDay(dateString: String): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = formatter.parse(dateString)
            return DateTimeFormatter.ofPattern("MMM\ndd").format(date)

        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun diffInDays(dateString: String): String {
            val format: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val yourDate = format.parse(dateString)

            val todayDate = Calendar.getInstance(Locale.ENGLISH).time
            val diffInDays = ((yourDate!!.time - todayDate.time) / (1000 * 60 * 60 * 24)).toInt()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = formatter.parse(dateString)

            val desiredFormat = DateTimeFormatter.ofPattern("EEEE").format(date)
            val desiredFormat2 = DateTimeFormatter.ofPattern("MMM dd").format(date)

            return if (diffInDays < 7) "Coming $desiredFormat" else "Coming $desiredFormat2"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingMovieViewHolder {
        return UpcomingMovieViewHolder(
            ComingSoonDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: UpcomingMovieViewHolder, position: Int) {
        val current = dataSet[position]
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    override fun getItemCount() = dataSet.size

}