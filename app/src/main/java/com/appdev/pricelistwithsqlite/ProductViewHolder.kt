package com.appdev.pricelistwithsqlite

import androidx.recyclerview.widget.RecyclerView
import com.appdev.pricelistwithsqlite.databinding.ProductItemBinding

class ProductViewHolder(val productBinding: ProductItemBinding) : RecyclerView.ViewHolder(productBinding.root) {
  fun bind(product: Product) {
    productBinding.itemNameTxt.text = product.name
    productBinding.itemPriceTxt.text = product.price
    productBinding.itemCatTxt.text = product.category
  }
}