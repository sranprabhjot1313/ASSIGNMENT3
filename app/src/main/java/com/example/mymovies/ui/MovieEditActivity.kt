package com.example.mymovies.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mymovies.databinding.ActivityMovieEditBinding
import com.example.mymovies.model.Movie
import com.example.mymovies.repository.MovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieEditBinding
    private val movieRepository = MovieRepository()
    private var existingMovie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        existingMovie = intent.getParcelableExtra("movie")
        if (existingMovie != null) {
            populateFields(existingMovie!!)
        }

        binding.saveButton.setOnClickListener {
            saveMovie()
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun populateFields(movie: Movie) {
        binding.titleEditText.setText(movie.title)
        binding.directorsEditText.setText(movie.directors.joinToString(", "))
        binding.actorsEditText.setText(movie.actors.joinToString(", "))
        binding.genresEditText.setText(movie.genres.joinToString(", "))
        binding.yearEditText.setText(movie.year.toString())
        binding.lengthEditText.setText(movie.length.toString())
        binding.studioEditText.setText(movie.studio)
        binding.descriptionEditText.setText(movie.description)
    }

    private fun saveMovie() {
        val title = binding.titleEditText.text.toString()
        val directors = binding.directorsEditText.text.toString().split(",").map { it.trim() }
        val actors = binding.actorsEditText.text.toString().split(",").map { it.trim() }
        val genres = binding.genresEditText.text.toString().split(",").map { it.trim() }
        val year = binding.yearEditText.text.toString().toIntOrNull() ?: 0
        val length = binding.lengthEditText.text.toString().toIntOrNull() ?: 0
        val studio = binding.studioEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()

        if (title.isEmpty() || directors.isEmpty() || year == 0) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val movie = existingMovie?.copy(
            title = title,
            directors = directors,
            actors = actors,
            genres = genres,
            year = year,
            length = length,
            studio = studio,
            description = description
        ) ?: Movie(
            title = title,
            directors = directors,
            actors = actors,
            genres = genres,
            year = year,
            length = length,
            studio = studio,
            description = description
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (existingMovie != null) {
                    movieRepository.updateMovie(movie)
                } else {
                    val newMovieId = movieRepository.addMovie(movie)
                    movieRepository.updateMovie(movie.copy(id = newMovieId))
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MovieEditActivity, "Movie saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MovieEditActivity, "Error saving movie: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
} 