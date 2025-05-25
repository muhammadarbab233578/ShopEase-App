package com.example.shoppingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val productList: MutableList<Product>,
    private val onDeleteClicked: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.productName)
        val price: TextView = itemView.findViewById(R.id.productPrice)
        val description: TextView = itemView.findViewById(R.id.productDescription)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.name.text = product.name
        holder.price.text = "Rs. ${product.price}"
        holder.description.text = product.description

        holder.deleteButton.setOnClickListener {
            onDeleteClicked(product)
        }
    }

    fun removeProduct(product: Product) {
        val index = productList.indexOf(product)
        if (index != -1) {
            productList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
