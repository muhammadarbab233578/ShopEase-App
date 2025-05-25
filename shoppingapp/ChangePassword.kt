package com.example.shoppingapp

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class ChangePassword : Fragment() {

    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var checkboxShowPassword: CheckBox
    private lateinit var btnChangePassword: Button

    private lateinit var database: DatabaseReference
    private lateinit var userPhone: String
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)

        etOldPassword = view.findViewById(R.id.etOldPassword)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        checkboxShowPassword = view.findViewById(R.id.checkboxShowPassword)
        btnChangePassword = view.findViewById(R.id.btnChangePassword)

        database = FirebaseDatabase.getInstance("https://shopping-appp-b920f-default-rtdb.firebaseio.com/")
            .getReference("users")

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userPhone = sharedPreferences.getString("phone", "") ?: ""

        checkboxShowPassword.setOnCheckedChangeListener { _, isChecked ->
            val inputType = if (isChecked)
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            etOldPassword.inputType = inputType
            etNewPassword.inputType = inputType
            etConfirmPassword.inputType = inputType

            // Move cursor to end after inputType change
            etOldPassword.setSelection(etOldPassword.text.length)
            etNewPassword.setSelection(etNewPassword.text.length)
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
        }

        btnChangePassword.setOnClickListener {
            val oldPassword = etOldPassword.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), "New passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            database.orderByChild("phone").equalTo(userPhone)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var passwordUpdated = false
                        for (userSnap in snapshot.children) {
                            val user = userSnap.getValue(User::class.java)
                            if (user != null && user.password == oldPassword) {
                                userSnap.ref.child("password").setValue(newPassword)
                                sharedPreferences.edit().putString("password", newPassword).apply()
                                Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                                passwordUpdated = true
                                break
                            }
                        }
                        if (!passwordUpdated) {
                            Toast.makeText(requireContext(), "Old password is incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        return view
    }
}
