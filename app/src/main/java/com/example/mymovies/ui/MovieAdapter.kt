package com.example.mymovies.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mymovies.databinding.ItemMovieBinding
import com.example.mymovies.model.Movie

class MovieAdapter(
    private val onEditClick: (Movie) -> Unit,
    private val onDeleteClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var movies = mutableListOf<Movie>()

    fun updateMovies(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                titleTextView.text = movie.title
                yearTextView.text = movie.year.toString()
                directorsTextView.text = movie.directors.joinToString(", ")
                actorsTextView.text = movie.actors.joinToString(", ")
                genresTextView.text = movie.genres.joinToString(", ")
                lengthTextView.text = "${movie.length} minutes"
                studioTextView.text = movie.studio
                descriptionTextView.text = movie.description

                editButton.setOnClickListener { onEditClick(movie) }
                deleteButton.setOnClickListener { onDeleteClick(movie) }
            }
        }
    }
} 