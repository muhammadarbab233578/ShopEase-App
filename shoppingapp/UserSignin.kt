package com.example.shoppingapp

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class UserSignin : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var signupButton: Button
    private lateinit var showPasswordCheckbox: CheckBox
    private lateinit var loginRedirectText: TextView

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_signin)

        nameInput = findViewById(R.id.nameInput)
        phoneInput = findViewById(R.id.phoneInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        signupButton = findViewById(R.id.signupButton)
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox)
        loginRedirectText = findViewById(R.id.loginRedirectText)

        database = FirebaseDatabase.getInstance("https://shopping-appp-b920f-default-rtdb.firebaseio.com/")
            .getReference("users")

        // Show/Hide Password
        showPasswordCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                confirmPasswordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                confirmPasswordInput.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            passwordInput.setSelection(passwordInput.text.length)
            confirmPasswordInput.setSelection(confirmPasswordInput.text.length)
        }

        signupButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            var isValid = true

            if (name.isEmpty()) {
                nameInput.error = "Name is required"
                isValid = false
            }

            if (phone.isEmpty()) {
                phoneInput.error = "Phone number is required"
                isValid = false
            } else if (phone.length != 11 || !phone.all { it.isDigit() }) {
                phoneInput.error = "Phone number must be exactly 10 digits"
                isValid = false
            }

            // Password validation regex: at least 6 characters, one digit, one letter, one special character
            val passwordPattern = Regex("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{6,}$")
            if (!password.matches(passwordPattern)) {
                passwordInput.error = "Password must be 6+ chars with letter, digit & special char"
                isValid = false
            }

            if (confirmPassword.isEmpty()) {
                confirmPasswordInput.error = "Please confirm password"
                isValid = false
            }

            if (password != confirmPassword) {
                confirmPasswordInput.error = "Passwords do not match"
                isValid = false
            }

            if (!isValid) {
                Toast.makeText(this, "❌ Invalid input!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if phone already exists
            database.orderByChild("phone").equalTo(phone)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(
                                this@UserSignin,
                                "❌ This phone number is already registered",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("SignupCheck", "Phone number already exists: $phone")
                        } else {
                            val userId = database.push().key
                            if (userId == null) {
                                Toast.makeText(this@UserSignin, "❌ Could not generate user ID", Toast.LENGTH_SHORT).show()
                                Log.e("SignupError", "userId null")
                                return
                            }

                            val user = User(name, phone, password)

                            Toast.makeText(this@UserSignin, "⏳ Saving data...", Toast.LENGTH_SHORT).show()

                            database.child(userId).setValue(user)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@UserSignin, "✅ Signup successful", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@UserSignin, UserLogin::class.java))
                                        finish()
                                    } else {
                                        Toast.makeText(this@UserSignin, "❌ Failed to signup: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                        Log.e("SignupFailure", "Failed to store user data", task.exception)
                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@UserSignin,
                            "Database error: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("FirebaseError", "Read cancelled", error.toException())
                    }
                })
        }

        loginRedirectText.setOnClickListener {
            startActivity(Intent(this, UserLogin::class.java))
            finish()
        }
    }
}

