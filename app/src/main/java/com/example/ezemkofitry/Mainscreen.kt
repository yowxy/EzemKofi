package com.example.ezemkofitry

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ezemkofitry.databinding.ActivityMainscreenBinding
import com.example.ezemkofitry.databinding.ItemCategoryBinding
import com.example.ezemkofitry.databinding.ItemCoffeBinding
import com.example.ezemkofitry.databinding.ItemCoffeTopBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class Mainscreen : AppCompatActivity() {
    lateinit var binding: ActivityMainscreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding=ActivityMainscreenBinding.inflate(layoutInflater)
        setContentView(binding .root)

        binding.search.setOnClickListener {
                startActivity(Intent(this@Mainscreen,Search::class.java))
            }

        binding.imagecard.setOnClickListener {
            startActivity(Intent(this@Mainscreen,Cart_screen::class.java))
            finish()
        }

        GlobalScope.launch(Dispatchers.IO){
            val conn = URL("http://10.0.2.2:5000/api/me").openConnection().apply {this as HttpURLConnection
                setRequestProperty("Authorization", "Bearer ${getSharedPreferences("EsemkaCoffe", Context.MODE_PRIVATE).getString("DataToken", "")}")
            } as HttpURLConnection
            val data = JSONObject(conn.inputStream.bufferedReader().readText())

            runOnUiThread {
                binding.textView.text = data.getString("fullName")
            }
        }


        GlobalScope.launch(Dispatchers.IO){
            val con = URL("http://10.0.2.2:5000/api/coffee-category").openStream().bufferedReader().readText()
            val jsons = JSONArray(con)


            runOnUiThread {
                var isActive: Int = -1
                class HolderData(val viewData:ItemCategoryBinding):RecyclerView.ViewHolder(viewData.root)
                binding.dataCategory.adapter = object:RecyclerView.Adapter<HolderData>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
                        return  HolderData(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false ))
                    }

                    override fun getItemCount(): Int = jsons.length()

                    override fun onBindViewHolder(holder: HolderData, position: Int) {
                        val item = jsons.getJSONObject(position)
                        holder.viewData.categoryName.text = item.getString("name")

                        if (position != isActive) {
                            //holder.viewData.categoryName.background = ContextCompat.getDrawable(this@Mainscreen, R.drawable.rating)
                            //holder.viewData.categoryName.setTextColor(ContextCompat.getColor(this@Mainscreen,R.color.black))
                        } else {
                            holder.viewData.categoryName.background = ContextCompat.getDrawable(this@Mainscreen, R.drawable.coffee_backround)
                            holder.viewData.categoryName.setTextColor(ContextCompat.getColor(this@Mainscreen,R.color.white))
                        }
                        holder.itemView.setOnClickListener {
                            if (isActive != -1) {
                                notifyItemChanged(isActive)
                            }
                            isActive = position
                            notifyItemChanged(isActive)
                            refresh(item.getInt("id"))
                        }
                    }

                }
            }

        }


        GlobalScope.launch(Dispatchers.IO){
            val conn = URL("http://10.0.2.2:5000/api/coffee/top-picks").openStream().bufferedReader().readText()
            val data = JSONArray(conn)


            runOnUiThread {
                class HolderData(val viewData:ItemCoffeTopBinding):ViewHolder(viewData.root)
                binding.dataTopCoffe.adapter = object :RecyclerView.Adapter<HolderData>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
                        return  HolderData( ItemCoffeTopBinding.inflate(LayoutInflater.from(parent.context),parent,false))
                    }

                    override fun getItemCount(): Int = data.length()

                    override fun onBindViewHolder(holder: HolderData, position: Int) {
                        val item = data.getJSONObject(position)
                        holder.viewData.nameCoffe.text = item.getString("name")
                        holder.viewData.rating.text = item.getDouble("rating").toString()
                        holder.viewData.typeCoffe.text = item.getString("category")
                        holder.viewData.price.text = "$ ${item.getDouble("price")}"
                        GlobalScope.launch(Dispatchers.IO) {
                            val url = "http://10.0.2.2:5000/images/${item.getString("imagePath")}"
                            Log.d("cek url", url) // Log the URL here
                            val image = BitmapFactory.decodeStream(URL(url).openStream())
                            runOnUiThread {
                                holder.viewData.imageCoffe.setImageBitmap(image)
                            }
                        }

                    }
                }
            }
        }
        refresh()
    }

    private  fun refresh(id:Int=0){
            GlobalScope.launch(Dispatchers.IO){
                val con = URL(if (id == 0)"http://10.0.2.2:5000/api/coffee" else "http://10.0.2.2:5000/api/coffee?coffeeCategoryID=$id").openStream().bufferedReader().readText()
                val jsons = JSONArray(con)

                runOnUiThread {
                    binding.dataCoffe.adapter = null
                    class HolderData(val viewData:ItemCoffeBinding):RecyclerView.ViewHolder(viewData.root)
                    binding.dataCoffe.adapter = object :RecyclerView.Adapter<HolderData>(){
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): HolderData {
                            return  HolderData(ItemCoffeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
                        }

                        override fun getItemCount(): Int = jsons.length()

                        override fun onBindViewHolder(holder: HolderData, position: Int) {
                            val item = jsons.getJSONObject(position)
                            holder.viewData.namaCoffe.text = item.getString("name")
                            holder.viewData.priceCoffe.text = "$ ${item.getDouble("price")}"
                            holder.viewData.ratingCoffe.text = item.getDouble("rating").toString()

                            holder.itemView.setOnClickListener {
                                holder.itemView.context.startActivity(
                                    Intent(
                                        this@Mainscreen,
                                        Detail::class.java
                                    ).apply {
                                        putExtra("idCoffe", item.getInt("id"))
                                    })
                            }

                            GlobalScope.launch(Dispatchers.IO){
                                val image = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/images/${item.getString("imagePath")}").openStream())
                                runOnUiThread {
                                    holder.viewData.imageCoffe.setImageBitmap(image)
                                }
                            }

                        }

                    }
                }
            }
    }
}