package com.example.shoppingapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class SearchFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageTextView: TextView
    private lateinit var adapter: UserProductAdapter
    private lateinit var databaseRef: DatabaseReference
    private val productList = mutableListOf<Product>()

    private val userPhone by lazy {
        requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            .getString("userPhone", "UnknownUser") ?: "UnknownUser"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)
        recyclerView = view.findViewById(R.id.searchRecyclerView)
        messageTextView = view.findViewById(R.id.messageTextView)

        adapter = UserProductAdapter(
            requireContext(),
            productList,
            onAddToCart = { product -> addToFirebase("addcart", product) },
            onAddToFavorite = { product -> addToFirebase("addfavorit", product) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        databaseRef = FirebaseDatabase.getInstance("https://shopping-appp-b920f-default-rtdb.firebaseio.com/")
            .getReference("products")

        messageTextView.text = "Search product"
        messageTextView.visibility = View.VISIBLE

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchProduct(query)
            } else {
                messageTextView.text = "Search product"
                messageTextView.visibility = View.VISIBLE
                productList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        return view
    }

    private fun searchProduct(query: String) {
        databaseRef.orderByChild("name").startAt(query).endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productList.clear()
                    for (data in snapshot.children) {
                        val product = data.getValue(Product::class.java)
                        if (product != null) {
                            productList.add(product)
                        }
                    }

                    adapter.notifyDataSetChanged()

                    messageTextView.text = if (productList.isEmpty()) "No product found" else ""
                    messageTextView.visibility = if (productList.isEmpty()) View.VISIBLE else View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    messageTextView.text = "Error: ${error.message}"
                    messageTextView.visibility = View.VISIBLE
                }
            })
    }

    private fun addToFirebase(node: String, product: Product) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("$node/$userPhone")

        ref.orderByChild("name").equalTo(product.name)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(
                            requireContext(),
                            "You have already added this product to ${if (node == "addcart") "cart" else "favorites"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val productId = ref.push().key
                        productId?.let {
                            ref.child(it).setValue(product)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Added to ${if (node == "addcart") "cart" else "favorites"}",
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
