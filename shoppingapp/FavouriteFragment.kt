package com.example.shoppingapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FavoriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noFavTextView: TextView
    private lateinit var adapter: FavoriteAdapter
    private val favoriteList = mutableListOf<Product>()
    private lateinit var database: DatabaseReference

    private val userPhone by lazy {
        requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("phone", "UnknownUser") ?: "UnknownUser"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerView = view.findViewById(R.id.favRecyclerView)
        noFavTextView = view.findViewById(R.id.noFavTextView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = FavoriteAdapter(requireContext(), userPhone, favoriteList)
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance("https://shopping-appp-b920f-default-rtdb.firebaseio.com/")
            .getReference("favorites")
            .child(userPhone)

        loadFavoriteProducts()

        return view
    }

    private fun loadFavoriteProducts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favoriteList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { favoriteList.add(it) }
                }

                adapter.notifyDataSetChanged()

                // Show or hide "No favorites" message
                if (favoriteList.isEmpty()) {
                    noFavTextView.visibility = View.VISIBLE
                } else {
                    noFavTextView.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
