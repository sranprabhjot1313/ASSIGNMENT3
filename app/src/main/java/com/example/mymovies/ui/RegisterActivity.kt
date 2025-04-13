package com.example.mymovies.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mymovies.databinding.ActivityRegisterBinding
import com.example.mymovies.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val authRepository = AuthRepository()
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "Activity created")

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    if (password.length >= 6) {
                        Log.d(TAG, "Attempting to register user with email: $email")
                        registerUser(email, password)
                    } else {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d(TAG, "Password mismatch")
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "Empty fields detected")
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            Log.d(TAG, "Cancel button clicked, navigating to login")
            navigateToLogin()
        }
    }

    private fun registerUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Starting registration process")
                val result = authRepository.register(email, password)
                withContext(Dispatchers.Main) {
                    result.onSuccess { user ->
                        Log.d(TAG, "Registration successful for user: ${user.email}")
                        Toast.makeText(this@RegisterActivity, "Registration successful! Please log in with your new credentials.", Toast.LENGTH_LONG).show()
                        navigateToLogin()
                    }.onFailure { exception ->
                        Log.e(TAG, "Registration failed", exception)
                        Toast.makeText(this@RegisterActivity, "Registration failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during registration", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "An unexpected error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToLogin() {
        Log.d(TAG, "Navigating to login screen")
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
} 