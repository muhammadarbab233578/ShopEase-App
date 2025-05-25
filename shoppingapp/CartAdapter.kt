package com.example.shoppingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class CartAdapter(
    private val context: Context,
    private val productList: MutableList<Product>,
    private val userPhone: String,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.productName)
        val price: TextView = view.findViewById(R.id.productPrice)
        val desc: TextView = view.findViewById(R.id.productDescription)
        val btnRemoveFromCart: AppCompatButton = view.findViewById(R.id.btnRemoveFromCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = productList[position]

        holder.name.text = product.name
        holder.price.text = "Rs. ${product.price}"
        holder.desc.text = product.description

        holder.btnRemoveFromCart.setOnClickListener {
            if (product.id.isNullOrEmpty()) {
                Toast.makeText(context, "Cannot remove item without ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val ref = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(userPhone)
                .child(product.id!!)

            ref.removeValue().addOnSuccessListener {
                Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < productList.size) {
                    productList.removeAt(currentPosition)
                    notifyItemRemoved(currentPosition)
                    onCartUpdated()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
