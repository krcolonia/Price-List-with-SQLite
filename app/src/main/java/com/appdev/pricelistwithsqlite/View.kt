package com.appdev.pricelistwithsqlite

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdev.pricelistwithsqlite.databinding.ActivityViewBinding
import java.util.Locale

class View : AppCompatActivity() {

  private lateinit var viewBinding: ActivityViewBinding

  private lateinit var recyclerView: RecyclerView
  private lateinit var searchView: SearchView
  private lateinit var adapter: ProductAdapter

  private var prodIdList = ArrayList<String>()
  private var prodNameList = ArrayList<String>()
  private var prodPriceList = ArrayList<String>()
  private var prodCatList = ArrayList<String>()
  private var productItemList = ArrayList<Product>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewBinding = ActivityViewBinding.inflate(layoutInflater)
    setContentView(viewBinding.root)

    setViewType()

    recyclerView = viewBinding.productRecycler
    searchView = viewBinding.productSearch

    recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    assignArrayLists()
    addDataToList()

    searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query : String?) : Boolean {
        return false
      }

      override fun onQueryTextChange(newText : String?) : Boolean {
        filterList(newText)
        return true
      }
    })

    adapter = ProductAdapter(productItemList, object: ProductAdapter.OnItemClickListener {
      override fun onItemClicked(prodId: String, prodName: String, prodPrice: String, prodCat: String) {
        val editIntent = Intent(applicationContext, Edit::class.java)

        editIntent.putExtra("id", prodId)
        editIntent.putExtra("name", prodName)
        editIntent.putExtra("price", prodPrice)
        editIntent.putExtra("cat", prodCat)

        var type = ""

        when(intent.getStringExtra("type")) {
          "All" -> {
            type = "All"
          }
          "Food" -> {
            type = "Food"
          }
          "Drink" -> {
            type = "Drink"
          }
          "Other" -> {
            type = "Other"
          }
        }

        editIntent.putExtra("viewType", type)
        startActivity(editIntent)
        finish()
      }
    })

    recyclerView.adapter = adapter
  }

  private fun filterList(query : String?) {
    if(query != null) {
      val filteredList = ArrayList<Product>()
      for (i in productItemList) {
        if(i.name.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))) {
          filteredList.add(i)
        }
      }
      if (filteredList.isEmpty()) {
        adapter.clear()
        Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show()
      }
      else {
        adapter.setFilteredList(filteredList)
      }
    }
  }

  @SuppressLint("SetTextI18n")
  private fun setViewType() {
    val viewTitle = viewBinding.viewTitleTxt

    when(intent.getStringExtra("type")) {
      "All" -> {
        viewTitle.text = "All Products"
      }
      "Food" -> {
        viewTitle.text = "Food Products"
      }
      "Drink" -> {
        viewTitle.text = "Drink Products"
      }
      "Other" -> {
        viewTitle.text = "Other Products"
      }
    }
  }

  private fun assignArrayLists() {
    val db: SQLiteDatabase = openOrCreateDatabase("productlistDB", Context.MODE_PRIVATE, null)
    var tmpTable: Cursor = db.rawQuery("SELECT * FROM tblProduct", null)

    if(!tmpTable.moveToFirst()) {
      db.execSQL("DROP TABLE IF EXISTS tblProduct")
      db.execSQL("CREATE TABLE IF NOT EXISTS tblProduct(" +
          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
          "f_prodname VARCHAR," +
          "f_prodprice VARCHAR," +
          "f_prodcat VARCHAR)")
      Toast.makeText(applicationContext, "List is Empty!", Toast.LENGTH_LONG).show()
      this.finish()
    }

    when(intent.getStringExtra("type")) {
      "All" -> {
        tmpTable = db.rawQuery("SELECT * FROM tblProduct ORDER BY f_prodname", null)
      }
      "Food" -> {
        tmpTable = db.rawQuery("SELECT * FROM tblProduct WHERE f_prodcat IN ('Food') ORDER BY f_prodname", null)
      }
      "Drink" -> {
        tmpTable = db.rawQuery("SELECT * FROM tblProduct WHERE f_prodcat IN ('Drink') ORDER BY f_prodname", null)
      }
      "Other" -> {
        tmpTable = db.rawQuery("SELECT * FROM tblProduct WHERE f_prodcat IN ('Other') ORDER BY f_prodname", null)
      }
    }

    val id = tmpTable.getColumnIndex("id")
    val prodName = tmpTable.getColumnIndex("f_prodname")
    val prodPrice = tmpTable.getColumnIndex("f_prodprice")
    val prodCat = tmpTable.getColumnIndex("f_prodcat")

    if(tmpTable.moveToFirst()) {
      do {
        prodIdList.add(tmpTable.getString(id))
        prodNameList.add(tmpTable.getString(prodName))
        prodPriceList.add(tmpTable.getString(prodPrice))
        prodCatList.add(tmpTable.getString(prodCat))
      } while(tmpTable.moveToNext())
    }
    else {
      Toast.makeText(applicationContext, "List is Empty!", Toast.LENGTH_SHORT).show()
      this.finish()
    }

    tmpTable.close()
  }

  private fun addDataToList() {
    for(i in 0..<prodIdList.size)
      productItemList.add(Product(prodIdList[i], prodNameList[i], prodPriceList[i], prodCatList[i]))
  }
}