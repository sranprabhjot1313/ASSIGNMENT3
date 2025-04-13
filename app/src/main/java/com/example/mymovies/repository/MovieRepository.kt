package com.example.mymovies.repository

import android.util.Log
import com.example.mymovies.model.Movie
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MovieRepository {
    private val db = FirebaseFirestore.getInstance()
    private val moviesCollection = db.collection("mymovies")

    suspend fun getMovies(): List<Movie> {
        Log.d("MovieRepository", "Starting to fetch movies from collection: mymovies")
        return try {
            val querySnapshot = moviesCollection
                .get()
                .await()

            Log.d("MovieRepository", "Firestore query completed. Found ${querySnapshot.documents.size} documents")
            
            val movies = querySnapshot.documents.mapNotNull { document ->
                try {
                    Log.d("MovieRepository", "Processing document: ${document.id}")
                    val data = document.data
                    if (data != null) {
                        // Convert String to List for actors if needed
                        val actors = when (val actorsField = data["actors"]) {
                            is String -> listOf(actorsField)
                            is List<*> -> actorsField.filterIsInstance<String>()
                            else -> emptyList()
                        }
                        
                        // Convert String to List for directors if needed
                        val directors = when (val directorsField = data["directors"]) {
                            is String -> listOf(directorsField)
                            is List<*> -> directorsField.filterIsInstance<String>()
                            else -> emptyList()
                        }
                        
                        // Convert String to List for genres if needed
                        val genres = when (val genresField = data["genres"]) {
                            is String -> listOf(genresField)
                            is List<*> -> genresField.filterIsInstance<String>()
                            else -> emptyList()
                        }

                        val movie = Movie(
                            id = document.id,
                            title = data["title"] as? String ?: "",
                            directors = directors,
                            actors = actors,
                            genres = genres,
                            year = (data["year"] as? Number)?.toInt() ?: 0,
                            length = (data["length"] as? Number)?.toInt() ?: 0,
                            studio = data["studio"] as? String ?: "",
                            description = data["description"] as? String ?: ""
                        )
                        
                        Log.d("MovieRepository", "Successfully parsed movie: ${movie.title}")
                        movie
                    } else {
                        Log.e("MovieRepository", "Document data is null for document: ${document.id}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("MovieRepository", "Error parsing document ${document.id}", e)
                    null
                }
            }
            
            Log.d("MovieRepository", "Successfully processed ${movies.size} movies")
            movies.sortedBy { it.title }
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error fetching movies from Firestore", e)
            throw e
        }
    }

    suspend fun addMovie(movie: Movie): String {
        Log.d("MovieRepository", "Adding new movie: ${movie.title}")
        return try {
            val docRef = moviesCollection.add(movie).await()
            Log.d("MovieRepository", "Movie added with ID: ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error adding movie", e)
            throw e
        }
    }

    suspend fun updateMovie(movie: Movie) {
        Log.d("MovieRepository", "Updating movie: ${movie.id}")
        if (movie.id.isEmpty()) {
            val error = "Movie ID is required for update"
            Log.e("MovieRepository", error)
            throw Exception(error)
        }
        try {
            moviesCollection.document(movie.id).set(movie).await()
            Log.d("MovieRepository", "Movie updated successfully")
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error updating movie", e)
            throw e
        }
    }

    suspend fun deleteMovie(movieId: String) {
        Log.d("MovieRepository", "Deleting movie: $movieId")
        if (movieId.isEmpty()) {
            val error = "Movie ID is required for deletion"
            Log.e("MovieRepository", error)
            throw Exception(error)
        }
        try {
            moviesCollection.document(movieId).delete().await()
            Log.d("MovieRepository", "Movie deleted successfully")
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error deleting movie", e)
            throw e
        }
    }
} 