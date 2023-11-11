package com.appdev.pricelistwithsqlite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appdev.pricelistwithsqlite.databinding.ProductItemBinding

class ProductAdapter(private var productList: List<Product>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ProductViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = ProductItemBinding.inflate(inflater,parent,false)

    return ProductViewHolder(binding)
  }

  interface OnItemClickListener {
    fun onItemClicked(prodId: String, prodName: String, prodPrice: String, prodCat: String)
  }

  override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
    holder.bind(productList[position])

    with(holder) {
      productBinding.itemBtn.setOnClickListener {
        val prodId = productList[position].prodID
        val prodName = productList[position].name
        val prodPrice = productList[position].price
        val prodCat = productList[position].category

        listener.onItemClicked(prodId, prodName, prodPrice, prodCat)
      }
    }
  }

  override fun getItemCount(): Int {
    return productList.size
  }
}