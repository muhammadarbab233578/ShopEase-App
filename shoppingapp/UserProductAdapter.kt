package com.example.shoppingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserProductAdapter(
    private val context: Context,
    private val productList: MutableList<Product>,
    private val onAddToCart: (Product) -> Unit,
    private val onAddToFavorite: (Product) -> Unit
) : RecyclerView.Adapter<UserProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.productName)
        val price: TextView = itemView.findViewById(R.id.productPrice)
        val description: TextView = itemView.findViewById(R.id.productDescription)
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)
        val btnAddToFavorite: Button = itemView.findViewById(R.id.btnAddToFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.name.text = product.name
        holder.price.text = "Rs. ${product.price}"
        holder.description.text = product.description

        holder.btnAddToCart.setOnClickListener {
            onAddToCart(product)
        }

        holder.btnAddToFavorite.setOnClickListener {
            onAddToFavorite(product)
        }
    }
}
