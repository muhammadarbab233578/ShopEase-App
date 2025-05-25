package com.example.shoppingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class AccountFragment : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var btnSignOut: Button
    private lateinit var llChangePassword: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("name", "N/A")
        val phone = sharedPref.getString("phone", "N/A")

        tvUsername = view.findViewById(R.id.tvUsername)
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber)
        btnSignOut = view.findViewById(R.id.btnSignOut)
        llChangePassword = view.findViewById(R.id.llChangePassword)

        tvUsername.text = username
        tvPhoneNumber.text = phone

        btnSignOut.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(requireContext(), UserLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        llChangePassword.setOnClickListener {
            // Dynamically get the correct container ID
            val containerId = when {
                activity?.findViewById<View>(R.id.fragmentContainer) != null -> R.id.fragmentContainer
                activity?.findViewById<View>(R.id.fragmentContainerView) != null -> R.id.fragmentContainerView
                else -> throw IllegalStateException("No valid fragment container found in the activity.")
            }

            parentFragmentManager.beginTransaction()
                .replace(containerId, ChangePassword())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
