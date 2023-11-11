package com.appdev.pricelistwithsqlite

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.appdev.pricelistwithsqlite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var mainBinding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mainBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(mainBinding.root)

    val foodBtn = mainBinding.foodBtn
    val drinkBtn = mainBinding.drinkBtn
    val otherBtn = mainBinding.otherBtn

    val addBtn = mainBinding.addBtn
    val viewBtn = mainBinding.viewBtn
    val foodFilterBtn = mainBinding.foodFilterBtn
    val drinkFilterBtn = mainBinding.drinkFilterBtn
    val otherFilterBtn = mainBinding.otherFilterBtn

    foodBtn.setOnClickListener {
      drinkBtn.isChecked = false
      otherBtn.isChecked = false
    }

    drinkBtn.setOnClickListener {
      foodBtn.isChecked = false
      otherBtn.isChecked = false
    }

    otherBtn.setOnClickListener {
      foodBtn.isChecked = false
      drinkBtn.isChecked = false
    }

    addBtn.setOnClickListener {
      addRecord()
    }

    viewBtn.setOnClickListener {
      viewProducts("All")
    }

    foodFilterBtn.setOnClickListener {
      viewProducts("Food")
    }

    drinkFilterBtn.setOnClickListener {
      viewProducts("Drink")
    }

    otherFilterBtn.setOnClickListener {
      viewProducts("Other")
    }

  }

  private fun categoryButtons(): String? {
    val foodBtn = mainBinding.foodBtn
    val drinkBtn = mainBinding.drinkBtn
    val otherBtn = mainBinding.otherBtn

    return if (foodBtn.isChecked)
      "Food"
    else if (drinkBtn.isChecked)
      "Drink"
    else if (otherBtn.isChecked)
      "Other"
    else
      null
  }

  private fun catButtonClear() {
    val foodBtn = mainBinding.foodBtn
    val drinkBtn = mainBinding.drinkBtn
    val otherBtn = mainBinding.otherBtn

    foodBtn.isChecked = false
    drinkBtn.isChecked = false
    otherBtn.isChecked = false
  }

  private fun addRecord() {
    try {
      val prodName = mainBinding.prodTxt
      if (prodName.text.isEmpty()) {
        Toast.makeText(applicationContext, "Product Name is Empty", Toast.LENGTH_LONG).show()
        return
      }

      val prodPrice = mainBinding.priceTxt
      if (prodPrice.text.isEmpty()) {
        Toast.makeText(applicationContext, "Product Price is Empty", Toast.LENGTH_LONG).show()
        return
      }

      val catOfProd = categoryButtons()
      if (catOfProd == null) {
        Toast.makeText(applicationContext, "Product Category is not set", Toast.LENGTH_LONG).show()
        return
      }

      val nameOfProd: String = prodName.text.toString()
      val priceOfProd: String = prodPrice.text.toString()

      val db: SQLiteDatabase = openOrCreateDatabase("productlistDB", Context.MODE_PRIVATE, null)

      db.execSQL("CREATE TABLE IF NOT EXISTS tblProduct(" +
          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
          "f_prodname VARCHAR," +
          "f_prodprice VARCHAR," +
          "f_prodcat VARCHAR)")

      val mysql = "INSERT INTO tblProduct(f_prodname, f_prodprice, f_prodcat) VALUES(?,?,?)"
      val statement: SQLiteStatement = db.compileStatement(mysql)

      statement.bindString(1, nameOfProd)
      statement.bindString(2, priceOfProd)
      statement.bindString(3, catOfProd)
      statement.execute()

      Toast.makeText(applicationContext, "Record Added Successfully", Toast.LENGTH_LONG).show()

      prodName.text.clear()
      prodPrice.text.clear()
      catButtonClear()

      prodName.requestFocus()
    }
    catch(e: Exception) {
      Toast.makeText(applicationContext, "Record Failure", Toast.LENGTH_LONG).show()
    }
  }

  private fun viewProducts(type: String) {
    val typePrint = type.lowercase()
    Toast.makeText(applicationContext, "Showing $typePrint products...", Toast.LENGTH_LONG).show()
    val viewIntent = Intent(applicationContext, View::class.java)
    viewIntent.putExtra("type", type)
    startActivity(viewIntent)
  }
}