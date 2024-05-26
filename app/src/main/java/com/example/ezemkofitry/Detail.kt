package com.example.ezemkofitry

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.ezemkofitry.databinding.ActivityDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class Detail : AppCompatActivity() {
   // private var selectedsize:String ="Medium"
    lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonback.setOnClickListener {
            startActivity(Intent(this@Detail, Mainscreen::class.java))
            finish()
        }


        binding.addtocart.setOnClickListener {
            startActivity(Intent(this@Detail,Cart_screen::class.java))
            finish()
            Toast.makeText(this, "anda masuk ke menu cart screen", Toast.LENGTH_SHORT).show()
        }


        var size: Char = 'M'
        var harga: Double = 0.00

        val id = intent.getIntExtra("idCoffe", 0)
        if (id != 0) {
            GlobalScope.launch(Dispatchers.IO) {
                val conn = URL("http://10.0.2.2:5000/api/coffee/$id").openStream().bufferedReader()
                    .readText()
                val data = JSONObject(conn)
                val image =
                    BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/images/${data.getString("imagePath")}").openStream())

                runOnUiThread {
                    harga = data.getDouble("price")
                    binding.ratingCoffe.text = data.getDouble("rating").toString()
                    binding.priceCoffe.text = "$ ${data.getDouble("price")}"
                    binding.namecoffe1.text = data.getString("name")
                    binding.descriptionCoffee.text = data.getString("description")
                    binding.imageCoffe.setImageBitmap(image)
                    binding.buttonback.setOnClickListener {
                        finish()
                    }

                    binding.buttonl.setOnClickListener {
                        harga = (data.getDouble("rating") * 1.15)
                        binding.priceCoffe.text = "$ ${String.format("%.2f", harga)}"
                        size = 'L'
                        rotate(1.15f)
                    }
                    binding.buttons.setOnClickListener {
                        harga = (data.getDouble("rating") * 0.85)
                        binding.priceCoffe.text = "$ ${String.format("%.2f", harga)}"
                        size = 'S'
                        rotate(0.85f)
                    }
                    binding.buttonm.setOnClickListener {
                        harga = data.getDouble("price")
                        binding.priceCoffe.text = "$ ${String.format("%.2f", harga)}"
                        size = 'M'
                        rotate(1.0f)
                    }

                    binding.buttonplus.setOnClickListener {
                        binding.countcoffe.text =
                            (binding.countcoffe.text.toString().toInt() + 1).toString()
                    }
                    binding.buttonminus.setOnClickListener {
                        binding.countcoffe.text =
                            (binding.countcoffe.text.toString().toInt() - 1).toString()
                    }
                }
            }
        }

    }



    fun rotate(scaleFactor: Float) {
        val scaleX = ObjectAnimator.ofFloat(binding.imageCoffe, "scaleX", scaleFactor)
        val scaleY = ObjectAnimator.ofFloat(binding.imageCoffe, "scaleY", scaleFactor)
        val rotate = ObjectAnimator.ofFloat(binding.imageCoffe, "rotation", 0f, 360f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, rotate)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.duration = 500
        animatorSet.start()
    }


}