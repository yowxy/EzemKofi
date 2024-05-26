package com.example.ezemkofitry

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezemkofitry.databinding.ActivityMainscreenBinding
import com.example.ezemkofitry.databinding.ActivitySearchBinding
import com.example.ezemkofitry.databinding.DesainSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class Search : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    lateinit var cari:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding= ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding .root)

        binding.back.setOnClickListener {
            startActivity(Intent(this@Search,Mainscreen::class.java))
            finish()
        }

        cari = binding.search.text.toString()

        GlobalScope.launch(Dispatchers.IO){
            val con = URL("http:/10.0.2.2:5000/api/coffee$cari").openStream().bufferedReader().readText()
            val data = JSONArray(con)

            runOnUiThread {
                val adapter = object :RecyclerView.Adapter<HorizontalRV>(){
                    override fun onCreateViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): HorizontalRV {
                       val inflate = DesainSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                        return HorizontalRV(inflate)
                    }

                    override fun getItemCount(): Int {
                       return  data.length()
                    }

                    override fun onBindViewHolder(holder: HorizontalRV, position: Int) {
                        val conn = data.getJSONObject(position)
                        holder.binding.nama.text = conn.getString("name")
                        holder.binding.tanggal.text = conn.getString("category")
                        holder.binding.jenis.text = conn.getString("price")
                        holder.binding.rating.text = conn.getString("rating")


                        holder.itemView.setOnClickListener {
                            startActivity(Intent(this@Search,Detail::class.java))
                        }



                        GlobalScope.launch(Dispatchers.IO){
                            val check: Bitmap = BitmapFactory.decodeStream(URL(" http://10.0.2.2:5000/images/${conn.getString("imagePath")}").openStream())
                            runOnUiThread {
                                holder.binding.image.setImageBitmap(check)
                            }
                        }
                    }

                }
                binding.MenuKopi.adapter = adapter
                binding.MenuKopi.layoutManager = LinearLayoutManager(this@Search)
            }
        }
    }
    class HorizontalRV(val binding : DesainSearchBinding) : RecyclerView.ViewHolder(binding.root)
}