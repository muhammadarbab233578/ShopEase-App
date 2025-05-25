package com.example.shoppingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FavoriteAdapter(
    private val context: Context,
    private val userPhone: String,
    private val productList: MutableList<Product>
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.productName)
        val price: TextView = itemView.findViewById(R.id.productPrice)
        val description: TextView = itemView.findViewById(R.id.productDescription)
        val btnUnfavorite: Button = itemView.findViewById(R.id.btnUnfavorite)
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_favourite_product, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val product = productList[position]

        holder.name.text = product.name
        holder.price.text = "Rs. ${product.price}"
        holder.description.text = product.description

        // Remove from favorites
        holder.btnUnfavorite.setOnClickListener {
            val favRef = FirebaseDatabase.getInstance()
                .getReference("favorites")
                .child(userPhone)

            favRef.orderByChild("name").equalTo(product.name)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item in snapshot.children) {
                            item.ref.removeValue()
                        }
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                        productList.removeAt(position)
                        notifyItemRemoved(position)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // Add to cart
        holder.btnAddToCart.setOnClickListener {
            val cartRef = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(userPhone)

            cartRef.orderByChild("name").equalTo(product.name)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(context, "Already in cart", Toast.LENGTH_SHORT).show()
                        } else {
                            val cartId = cartRef.push().key
                            cartId?.let {
                                cartRef.child(it).setValue(product)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
