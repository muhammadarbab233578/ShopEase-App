package com.example.shoppingapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*

class UserLogin : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupText = findViewById<TextView>(R.id.signupText)
        val showPassword = findViewById<CheckBox>(R.id.showPassword)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)

        database = FirebaseDatabase.getInstance("https://shopping-appp-b920f-default-rtdb.firebaseio.com/")
            .getReference("users")

        // Show Password Toggle
        showPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // move cursor to end after toggling
            passwordInput.setSelection(passwordInput.text.length)
        }

        // Login Button Click
        loginButton.setOnClickListener {
            val phone = phoneInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (phone.isEmpty()) {
                phoneInput.error = "Phone number required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordInput.error = "Password required"
                return@setOnClickListener
            }

            database.orderByChild("phone").equalTo(phone)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnap in snapshot.children) {
                                val user = userSnap.getValue(User::class.java)
                                if (user != null && user.password == password) {
                                    val editor = sharedPreferences.edit()
                                    editor.putString("phone", user.phone)
                                    editor.putString("password", user.password)
                                    editor.putString("name", user.name)
                                    editor.apply()

                                    if (phone == "12345") {
                                        Toast.makeText(this@UserLogin, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@UserLogin, AdminDashboard::class.java))
                                    } else {
                                        Toast.makeText(this@UserLogin, "Login Successful!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@UserLogin, UserDashboard::class.java))
                                    }
                                    finish()
                                    return
                                } else {
                                    Toast.makeText(this@UserLogin, "Invalid password", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@UserLogin, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@UserLogin, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // Signup click opens registration activity
        signupText.setOnClickListener {
            startActivity(Intent(this, UserSignin::class.java))
        }

        // Forget password - does nothing as requested
        forgotPassword.setOnClickListener {
            // No action
        }
    }
}
