package com.appdev.pricelistwithsqlite

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.appdev.pricelistwithsqlite.databinding.ActivityEditBinding

class Edit : AppCompatActivity() {

  private lateinit var editBinding: ActivityEditBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    editBinding = ActivityEditBinding.inflate(layoutInflater)
    setContentView(editBinding.root)

    val prodId = intent.getStringExtra("id")
    val prodName = intent.getStringExtra("name")
    val prodPrice = intent.getStringExtra("price")
    val prodCat = intent.getStringExtra("cat")

    val itemId = editBinding.idTxt
    val editName = editBinding.editNameTxt
    val editPrice = editBinding.editPriceTxt

    val editFoodBtn = editBinding.editFoodBtn
    val editDrinkBtn = editBinding.editDrinkBtn
    val editOtherBtn = editBinding.editOtherBtn

    val editBtn = editBinding.editBtn
    val delBtn = editBinding.delBtn
    val returnBtn = editBinding.returnBtn

    itemId.text = prodId
    editName.setText(prodName)
    editPrice.setText(prodPrice)

    setDefaultCategory(prodCat)

    editFoodBtn.setOnClickListener {
      editDrinkBtn.isChecked = false
      editOtherBtn.isChecked = false
    }

    editDrinkBtn.setOnClickListener {
      editFoodBtn.isChecked = false
      editOtherBtn.isChecked = false
    }

    editOtherBtn.setOnClickListener {
      editFoodBtn.isChecked = false
      editDrinkBtn.isChecked = false
    }

    editBtn.setOnClickListener {
      editRecord()
    }

    delBtn.setOnClickListener {
      delRecord()
    }

    returnBtn.setOnClickListener {
      val viewType = intent.getStringExtra("viewType")
      checkType(viewType)
    }

  }

  private fun categoryButtons(): String {
    val editFoodBtn = editBinding.editFoodBtn
    val editDrinkBtn = editBinding.editDrinkBtn
    val editOtherBtn = editBinding.editOtherBtn

    return if (editFoodBtn.isChecked)
      "Food"
    else if (editDrinkBtn.isChecked)
      "Drink"
    else if (editOtherBtn.isChecked)
      "Other"
    else
      ""
  }

  private fun editRecord() {
    try {
      val prodName = editBinding.editNameTxt
      if (prodName.text.isEmpty()) {
        Toast.makeText(applicationContext, "Product Name is Empty", Toast.LENGTH_SHORT).show()
        return
      }

      val prodPrice = editBinding.editPriceTxt
      if (prodPrice.text.isEmpty()) {
        Toast.makeText(applicationContext, "Product Price is Empty", Toast.LENGTH_SHORT).show()
        return
      }

      val catOfProd = categoryButtons()
      if (catOfProd.isEmpty()) {
        Toast.makeText(applicationContext, "Product Category is not set", Toast.LENGTH_SHORT).show()
        return
      }

      val origCat = intent.getStringExtra("cat")
      val origName = intent.getStringExtra("name")
      val origPrice = intent.getStringExtra("price")

      if(prodName.text.toString() == origName && prodPrice.text.toString() == origPrice && catOfProd == origCat) {
        Toast.makeText(applicationContext, "Please edit at least 1 value in this product.", Toast.LENGTH_SHORT).show()
        return
      }

      val idOfProd = editBinding.idTxt.text.toString()
      val nameOfProd = prodName.text.toString()
      val priceOfProd = prodPrice.text.toString()

      val db: SQLiteDatabase = openOrCreateDatabase("productlistDB", Context.MODE_PRIVATE, null)

      val mysql = "UPDATE tblProduct SET f_prodname = ?, f_prodprice = ?, f_prodcat = ? WHERE id = ?"
      val statement: SQLiteStatement = db.compileStatement(mysql)

      statement.bindString(1, nameOfProd)
      statement.bindString(2, priceOfProd)
      statement.bindString(3, catOfProd)
      statement.bindString(4, idOfProd)
      statement.execute()

      Toast.makeText(applicationContext, "Record Edited Successfully", Toast.LENGTH_SHORT).show()

      val viewType = intent.getStringExtra("viewType")
      checkType(viewType)
    }
    catch(e: Exception) {
      Toast.makeText(applicationContext, "Record Failure", Toast.LENGTH_SHORT).show()
    }
  }

  private fun delRecord() {
    try {
      val idOfProd = editBinding.idTxt.text.toString()

      val db: SQLiteDatabase = openOrCreateDatabase("productlistDB", Context.MODE_PRIVATE, null)

      val delRec = "DELETE FROM tblProduct WHERE id = ?"
      val delStatement: SQLiteStatement = db.compileStatement(delRec)

      delStatement.bindString(1, idOfProd)
      delStatement.execute()

      Toast.makeText(applicationContext, "Record Deleted!", Toast.LENGTH_SHORT).show()

      val viewType = intent.getStringExtra("viewType")
      checkType(viewType)
    }
    catch(e: Exception) {
      Toast.makeText(applicationContext, "Record Failure", Toast.LENGTH_SHORT).show()
    }
  }

  private fun checkType(checkType: String?) {
    val viewIntent = Intent(applicationContext, View::class.java)
    when(checkType) {
      "All" -> viewIntent.putExtra("type", "All")
      "Food" -> viewIntent.putExtra("type", "Food")
      "Drink" -> viewIntent.putExtra("type", "Drink")
      "Other" -> viewIntent.putExtra("type", "Other")
    }
    startActivity(viewIntent)
    this.finish()
  }

  private fun setDefaultCategory(prodCat: String?) {
    val editFoodBtn = editBinding.editFoodBtn
    val editDrinkBtn = editBinding.editDrinkBtn
    val editOtherBtn = editBinding.editOtherBtn

    when(prodCat) {
      "Food" -> {
        editFoodBtn.isChecked = true
        editDrinkBtn.isChecked = false
        editOtherBtn.isChecked = false
      }
      "Drink" -> {
        editFoodBtn.isChecked = false
        editDrinkBtn.isChecked = true
        editOtherBtn.isChecked = false
      }
      "Other" -> {
        editFoodBtn.isChecked = false
        editDrinkBtn.isChecked = false
        editOtherBtn.isChecked = true
      }
    }
  }
}