package com.example.shoppingapp

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyCartTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var totalItemsTextView: TextView
    private lateinit var deliveryTextView: TextView
    private lateinit var discountTextView: TextView
    private lateinit var checkoutButton: Button

    private val cartList = mutableListOf<Product>()
    private lateinit var adapter: CartAdapter
    private lateinit var cartRef: DatabaseReference

    private val userPhone by lazy {
        requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("phone", "") ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCart)
        emptyCartTextView = view.findViewById(R.id.textViewEmptyCart)
        totalTextView = view.findViewById(R.id.textViewTotal)
        totalItemsTextView = view.findViewById(R.id.textViewTotalItems)
        deliveryTextView = view.findViewById(R.id.textViewDelivery)
        discountTextView = view.findViewById(R.id.textViewDiscount)
        checkoutButton = view.findViewById(R.id.buttonCheckout)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CartAdapter(requireContext(), cartList, userPhone) {
            updateSummary()
            toggleEmptyMessage()
        }
        recyclerView.adapter = adapter

        cartRef = FirebaseDatabase.getInstance()
            .getReference("carts")
            .child(userPhone)

        loadCartItems()

        checkoutButton.setOnClickListener {
            if (cartList.isEmpty()) {
                Toast.makeText(requireContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = calculateFinalAmount()
            Toast.makeText(requireContext(), "Checkout amount: Rs. $total", Toast.LENGTH_LONG).show()

            cartRef.removeValue().addOnSuccessListener {
                cartList.clear()
                adapter.notifyDataSetChanged()
                updateSummary()
                toggleEmptyMessage()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to clear cart", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadCartItems() {
        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartList.clear()
                for (itemSnapshot in snapshot.children) {
                    val product = itemSnapshot.getValue(Product::class.java)
                    product?.id = itemSnapshot.key ?: ""
                    product?.let { cartList.add(it) }
                }
                adapter.notifyDataSetChanged()
                updateSummary()
                toggleEmptyMessage()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load cart: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleEmptyMessage() {
        if (cartList.isEmpty()) {
            emptyCartTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyCartTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun updateSummary() {
        val totalAmount = cartList.sumOf { it.price.toIntOrNull() ?: 0 }
        val totalItems = cartList.size
        val delivery = if (totalAmount >= 1000) 0 else 100
        val discountPercent = when {
            totalAmount >= 2000 -> 6
            totalAmount >= 1000 -> 3
            else -> 0
        }
        val discountAmount = (totalAmount * discountPercent) / 100
        val finalAmount = totalAmount - discountAmount + delivery

        totalItemsTextView.text = "Total Items: $totalItems"
        deliveryTextView.text = if (delivery == 0) "Delivery Fee: Free" else "Delivery Fee: Rs. $delivery"
        discountTextView.text = "Discount: Rs. $discountAmount"
        totalTextView.text = "Total: Rs. $finalAmount"
    }

    private fun calculateFinalAmount(): Int {
        val totalAmount = cartList.sumOf { it.price.toIntOrNull() ?: 0 }
        val delivery = if (totalAmount >= 1000) 0 else 100
        val discountPercent = when {
            totalAmount >= 2000 -> 6
            totalAmount >= 1000 -> 3
            else -> 0
        }
        val discountAmount = (totalAmount * discountPercent) / 100
        return totalAmount - discountAmount + delivery
    }
}
