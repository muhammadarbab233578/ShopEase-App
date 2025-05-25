package com.example.shoppingapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class UserDashboard : AppCompatActivity() {

    private lateinit var buttonHome: LinearLayout
    private lateinit var buttonSearch: LinearLayout
    private lateinit var buttonCart: LinearLayout
    private lateinit var buttonFavorite: LinearLayout
    private lateinit var buttonAccount: LinearLayout

    private lateinit var allButtons: List<LinearLayout>

    private val activeColor = Color.parseColor("#FF5722") // Active icon/text color (orange)
    private val inactiveColor = Color.parseColor("#000000") // Inactive color (black)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        // Initialize bottom nav buttons
        buttonHome = findViewById(R.id.buttonHome)
        buttonSearch = findViewById(R.id.buttonSearch)
        buttonCart = findViewById(R.id.buttonCart)
        buttonFavorite = findViewById(R.id.buttonFavorite)
        buttonAccount = findViewById(R.id.buttonAccount)

        allButtons = listOf(buttonHome, buttonSearch, buttonCart, buttonFavorite, buttonAccount)

        // Default fragment
        loadFragment(HomeFragment())
        setActiveButton(buttonHome)

        buttonHome.setOnClickListener {
            loadFragment(HomeFragment())
            setActiveButton(it)
        }

        buttonSearch.setOnClickListener {
            loadFragment(SearchFragment())
            setActiveButton(it)
        }

        buttonCart.setOnClickListener {
            loadFragment(CartFragment())
            setActiveButton(it)
        }

        buttonFavorite.setOnClickListener {
            loadFragment(FavoriteFragment())
            setActiveButton(it)
        }

        buttonAccount.setOnClickListener {
            loadFragment(AccountFragment())
            setActiveButton(it)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    private fun setActiveButton(active: View) {
        allButtons.forEach { button ->
            val icon = button.findViewById<ImageView>(
                when (button.id) {
                    R.id.buttonHome -> R.id.iconHome
                    R.id.buttonSearch -> R.id.iconSearch
                    R.id.buttonCart -> R.id.iconCart
                    R.id.buttonFavorite -> R.id.iconFavorite
                    R.id.buttonAccount -> R.id.iconAccount
                    else -> 0
                }
            )
            val text = button.getChildAt(1) as TextView

            if (button == active) {
                icon?.setColorFilter(activeColor)
                text.setTextColor(activeColor)
            } else {
                icon?.setColorFilter(inactiveColor)
                text.setTextColor(inactiveColor)
            }
        }
    }
}
