package com.example.ezemkofitry

import android.app.appsearch.GlobalSearchSession
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezemkofitry.databinding.ActivityCartScreenBinding
import com.example.ezemkofitry.databinding.CartItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class Cart_screen : AppCompatActivity() {
    lateinit var binding: ActivityCartScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarCart.setNavigationOnClickListener {
            startActivity(Intent(this@Cart_screen,Detail::class.java))
            finish()
        }


        GlobalScope.launch(Dispatchers.IO){
            val conn = URL("http://10.0.2.2:5000/api/coffee").openStream().bufferedReader().readText()
            val jsons = JSONArray(conn)

            runOnUiThread {
                binding.cartRv.adapter = Radapter(jsons)
                binding.cartRv.layoutManager = LinearLayoutManager(this@Cart_screen)
            }

        }
    }

    class Radapter (val roti : JSONArray): RecyclerView.Adapter<Radapter.Rview>(){
        class Rview(val binding : CartItemBinding, val context: Context) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Rview {
            val adap =  CartItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
            return Rview(adap,parent.context)
        }

        override fun getItemCount(): Int {
            return roti.length()
        }

        override fun onBindViewHolder(holder: Rview, position: Int) {
            val item = roti.getJSONObject(position)
            holder.binding.cartName.text = item.getString("name")
            holder.binding.cartName1.text = item.getString("category")
            holder.binding.cartPrice.text = item.getString("price")

            GlobalScope.launch(Dispatchers.IO){

            }
        }
    }
}