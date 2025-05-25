package com.example.shoppingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class AdminHomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_home, container, false)

        recyclerView = view.findViewById(R.id.productsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ProductAdapter(productList) { product ->
            deleteProduct(product)
        }
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
                    product?.let { productList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteProduct(product: Product) {
        if (product.id.isNotEmpty()) {
            database.child(product.id).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show()
                    productList.remove(product)
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to delete product", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Invalid product ID", Toast.LENGTH_SHORT).show()
        }
    }
}
