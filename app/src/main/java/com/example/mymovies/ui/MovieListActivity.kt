package com.example.mymovies.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymovies.databinding.ActivityMovieListBinding
import com.example.mymovies.model.Movie
import com.example.mymovies.repository.AuthRepository
import com.example.mymovies.repository.MovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieListBinding
    private val authRepository = AuthRepository()
    private val movieRepository = MovieRepository()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MovieListActivity", "Activity created")
        setupRecyclerView()
        loadMovies()

        binding.addMovieButton.setOnClickListener {
            startActivity(Intent(this, MovieEditActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        Log.d("MovieListActivity", "Setting up RecyclerView")
        movieAdapter = MovieAdapter(
            onEditClick = { movie ->
                val intent = Intent(this, MovieEditActivity::class.java)
                intent.putExtra("movie", movie)
                startActivity(intent)
            },
            onDeleteClick = { movie ->
                deleteMovie(movie)
            }
        )

        binding.movieRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MovieListActivity)
            adapter = movieAdapter
        }
    }

    private fun loadMovies() {
        Log.d("MovieListActivity", "Starting to load movies")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val movies = movieRepository.getMovies()
                Log.d("MovieListActivity", "Successfully loaded ${movies.size} movies from repository")
                withContext(Dispatchers.Main) {
                    if (movies.isEmpty()) {
                        Log.d("MovieListActivity", "No movies found in the database")
                        Toast.makeText(
                            this@MovieListActivity,
                            "No movies found. Add your first movie!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    movieAdapter.updateMovies(movies)
                }
            } catch (e: Exception) {
                Log.e("MovieListActivity", "Error loading movies", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MovieListActivity,
                        "Error loading movies: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun deleteMovie(movie: Movie) {
        Log.d("MovieListActivity", "Deleting movie: ${movie.title}")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieRepository.deleteMovie(movie.id)
                withContext(Dispatchers.Main) {
                    loadMovies()
                    Toast.makeText(this@MovieListActivity, "Movie deleted successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MovieListActivity", "Error deleting movie", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MovieListActivity, "Error deleting movie: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("MovieListActivity", "Activity resumed, reloading movies")
        loadMovies()
    }
} 