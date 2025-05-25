package com.example.shoppingapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class AdminDashboard : AppCompatActivity() {

    private lateinit var buttonHome: LinearLayout
    private lateinit var buttonSearch: LinearLayout
    private lateinit var buttonRequest: LinearLayout
    private lateinit var buttonMessage: LinearLayout
    private lateinit var buttonAccount: LinearLayout

    private lateinit var allButtons: List<LinearLayout>

    private val activeIconColor = Color.parseColor("#FF5722")  // Active icon/text color (orange)
    private val inactiveIconColor = Color.parseColor("#000000")  // Default icon/text color (black)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        buttonHome = findViewById(R.id.buttonHome)
        buttonSearch = findViewById(R.id.buttonSearch)
        buttonRequest = findViewById(R.id.buttonRequest)
        buttonMessage = findViewById(R.id.buttonMessage)
        buttonAccount = findViewById(R.id.buttonAccount)

        allButtons = listOf(buttonHome, buttonSearch, buttonRequest, buttonMessage, buttonAccount)

        // Load Home by default
        loadFragment(AdminHomeFragment())
        setActiveButton(buttonHome)

        buttonHome.setOnClickListener {
            loadFragment(AdminHomeFragment())
            setActiveButton(it)
        }

        buttonSearch.setOnClickListener {
            loadFragment(AdminSearchFragment())
            setActiveButton(it)
        }

        buttonRequest.setOnClickListener {
            loadFragment(AdminAddProductFragment())
            setActiveButton(it)
        }

        buttonMessage.setOnClickListener {
            loadFragment(AdminMessageFragment())
            setActiveButton(it)
        }

        buttonAccount.setOnClickListener {
            loadFragment(AccountFragment())
            setActiveButton(it)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun setActiveButton(active: View) {
        // For each button, set icon and text color based on whether it is active
        allButtons.forEach { button ->
            val icon = button.findViewById<ImageView>(
                when (button.id) {
                    R.id.buttonHome -> R.id.iconHome
                    R.id.buttonSearch -> R.id.iconSearch
                    R.id.buttonRequest -> R.id.iconAdd
                    R.id.buttonMessage -> R.id.iconMsg
                    R.id.buttonAccount -> R.id.iconAccount
                    else -> 0
                }
            )
            val text = button.getChildAt(1) as TextView

            if (button == active) {
                icon?.setColorFilter(activeIconColor)
                text.setTextColor(activeIconColor)
            } else {
                icon?.setColorFilter(inactiveIconColor)
                text.setTextColor(inactiveIconColor)
            }
        }
    }
}
