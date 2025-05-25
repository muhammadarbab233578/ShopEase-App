package com.example.shoppingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminAddProductFragment : Fragment() {

    private lateinit var productNameEditText: TextInputEditText
    private lateinit var productPriceEditText: TextInputEditText
    private lateinit var productDescriptionEditText: TextInputEditText
    private lateinit var addProductButton: Button

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_add_product, container, false)

        productNameEditText = view.findViewById(R.id.productNameEditText)
        productPriceEditText = view.findViewById(R.id.productPriceEditText)
        productDescriptionEditText = view.findViewById(R.id.productDescriptionEditText)
        addProductButton = view.findViewById(R.id.addProductButton)

        database = FirebaseDatabase.getInstance("https://shopping-appp-b920f-default-rtdb.firebaseio.com/")
            .getReference("products")

        addProductButton.setOnClickListener {
            addProduct()
        }

        return view
    }

    private fun addProduct() {
        val name = productNameEditText.text.toString().trim()
        val price = productPriceEditText.text.toString().trim()
        val description = productDescriptionEditText.text.toString().trim()

        if (name.isEmpty()) {
            productNameEditText.error = "Enter product name"
            return
        }

        if (price.isEmpty()) {
            productPriceEditText.error = "Enter product price"
            return
        }

        if (description.isEmpty()) {
            productDescriptionEditText.error = "Enter product description"
            return
        }

        val productId = database.push().key
        if (productId == null) {
            Toast.makeText(requireContext(), "Could not generate product ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Save without image
        val product = Product(productId, name, price, description, imageUrl = "")

        database.child(productId).setValue(product)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show()
                    clearInputs()
                } else {
                    Toast.makeText(requireContext(), "Failed to add product: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun clearInputs() {
        productNameEditText.text?.clear()
        productPriceEditText.text?.clear()
        productDescriptionEditText.text?.clear()
    }
}
