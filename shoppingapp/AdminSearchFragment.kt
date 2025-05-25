package com.example.shoppingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class AdminSearchFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageTextView: TextView
    private lateinit var adapter: ProductAdapter
    private lateinit var databaseRef: DatabaseReference
    private val productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_search, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)
        recyclerView = view.findViewById(R.id.searchRecyclerView)
        messageTextView = view.findViewById(R.id.messageTextView)

        adapter = ProductAdapter(productList) { product ->
            deleteProduct(product)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        databaseRef = FirebaseDatabase.getInstance().getReference("products")

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

                    if (productList.isEmpty()) {
                        messageTextView.text = "No product found"
                        messageTextView.visibility = View.VISIBLE
                    } else {
                        messageTextView.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    messageTextView.text = "Error: ${error.message}"
                    messageTextView.visibility = View.VISIBLE
                }
            })
    }

    private fun deleteProduct(product: Product) {
        databaseRef.orderByChild("name").equalTo(product.name)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        data.ref.removeValue()
                    }
                    productList.remove(product)
                    adapter.notifyDataSetChanged()

                    if (productList.isEmpty()) {
                        messageTextView.text = "No product found"
                        messageTextView.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    messageTextView.text = "Error: ${error.message}"
                    messageTextView.visibility = View.VISIBLE
                }
            })
    }
}
