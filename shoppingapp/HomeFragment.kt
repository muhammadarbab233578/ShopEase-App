package com.example.shoppingapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserProductAdapter
    private val productList = mutableListOf<Product>()
    private lateinit var database: DatabaseReference

    // Get the logged-in user phone number from SharedPreferences
    private val userPhone by lazy {
        requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("phone", "UnknownUser") ?: "UnknownUser"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.productsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = UserProductAdapter(
            requireContext(),
            productList,
            onAddToCart = { product -> addToFirebase("carts", product) },
            onAddToFavorite = { product -> addToFirebase("favorites", product) }
        )
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance("https://shopping-appp-b920f-default-rtdb.firebaseio.com/")
            .getReference("products")

        loadProducts()

        return view
    }

    private fun loadProducts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.id = productSnapshot.key ?: ""
                    product?.let { productList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load products.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addToFirebase(node: String, product: Product) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("$node/$userPhone")

        // Check if product already added by name
        ref.orderByChild("name").equalTo(product.name)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(
                            requireContext(),
                            "You have already added this product to ${if (node == "carts") "cart" else "favorites"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val productId = ref.push().key
                        productId?.let {
                            ref.child(it).setValue(product)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Added to ${if (node == "carts") "cart" else "favorites"}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed: ${it.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
